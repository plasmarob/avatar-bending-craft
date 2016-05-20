package me.plasmarob.bending.firebending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

//TODO: I am able to do fixed-width by pitch wave - see air blade.
public class FireWave extends BendingForm {

	private Player player;
	private ArrayList<BlockIterator> paths = new ArrayList<BlockIterator>();
	private Block next;
	static final int WIDTH = 3;
	Vector direction;
	FireWaveHorizEffect waveHorizEffect;
	FireWaveVerticalEffect waveVertEffect;
	FireCloudEffect waveCloudEffect;
	
	
	int length = 10;
	List<Entity> nearEntities;
	
	public static ConcurrentHashMap<Player, BendingForm> instances = new ConcurrentHashMap<Player, BendingForm>();

	public FireWave(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			instances.put(player, this);
			this.player = player;
			
			int radius = length;
			double count = 15;
			double arcAngle = 90;
			double angleHalf = arcAngle/2.0;
			double angle = arcAngle/(count-1);
			direction = player.getEyeLocation().getDirection();
			
			//get the axis unit vector K around which to rotate for horizontal
			Location unitTemp = player.getEyeLocation().clone();
			
			//unitTemp.setPitch(0);
			unitTemp.setYaw(unitTemp.getYaw()-90);
			Vector k = unitTemp.getDirection();
			k.setY(0);
			k = k.normalize();
			//Tools.say(k.toString());
			
			// end axis vector
			
			for (int i = 0; i < count; i++)
			{
				if (player.isSneaking())	// vertical wave
				{
					Location l = player.getEyeLocation().clone();
					l.setPitch((float) ( l.getPitch() - angleHalf + angle*i ));
					paths.add(new BlockIterator(l,0,radius));
					//waveVertEffect = new FireWaveVerticalEffect(Bending.getEffectManager(), player.getEyeLocation());
					//waveVertEffect.start();
				}
				else
				{
					/*
					player.sendMessage("---------------------------------");
					player.sendMessage(k.toString());
					*/
					
					Location l = player.getEyeLocation().clone();
					l.setDirection(l.getDirection().setY(0));
					l.setYaw((float) ( l.getYaw() - angleHalf + angle*i ));
					//player.sendMessage(Float.toString(l.getYaw()));
					Vector v = l.getDirection().clone();
					v = v.normalize();
					
					//player.sendMessage(v.toString());
					
					float theta = player.getEyeLocation().clone().getPitch();
					Vector part1 = v.clone().multiply(Math.cos(Math.toRadians(theta)));
					Vector part2 = k.clone().crossProduct(v.clone()).multiply(Math.sin(Math.toRadians(theta)));
					Vector part3 = k.clone().multiply(k.clone().dot(v.clone())).multiply(1-Math.cos(Math.toRadians(theta)));
					Vector vRot = part1.add(part2).add(part3);
					
					l.setDirection(vRot);
					paths.add(new BlockIterator(l,0,radius));
		
						
				}
			}
			//waveHorizEffect = new FireWaveHorizEffect(Bending.getEffectManager(), player.getEyeLocation().clone());
			//waveHorizEffect.start();	
			player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.4f);
			
			for (int i = 0; i < paths.size(); i++)
			{
				BlockIterator blit = paths.get(i);
				if (!blit.hasNext())
				{
					paths.remove(blit);
					i--;
					continue;
				}	
				next = blit.next();
			}
			for (int i = 0; i < paths.size(); i++)
			{
				BlockIterator blit = paths.get(i);
				if (!blit.hasNext())
				{
					paths.remove(blit);
					i--;
					continue;
				}	
				next = blit.next();
			}
			
		}
	}
	
	
	
	public static void progressAll() {
		if (instances.size() > 0)
			for (Player p : instances.keySet())
				instances.get(p).progress();
	}
	public static boolean progress(Player p) {
		if (instances.get(p) != null)
			return instances.get(p).progress();
		return false;
	}
	
	public boolean progress() {
		
		
		if (length > 0)
		{
			length--;
			for (int i = 0; i < paths.size(); i++)
			{
				BlockIterator blit = paths.get(i);
				if (!blit.hasNext())
				{
					
					paths.remove(blit);
					i--;
					continue;
				}	
				
				next = blit.next();
				//next.setType(Material.GLASS);	
				
				if (length < 7 && (length < 3 || length % 2 == 0))
				{
					waveCloudEffect = new FireCloudEffect(Bending.getEffectManager(), next.getLocation());
					waveCloudEffect.start();
				}
				
				
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setVelocity(direction);
					((Damageable) e).damage(2.0, player);
				}
				
				/*
				if (!Tools.isCrushable(next) || nearEntities.size() > 0)
				{
					paths.remove(blit);
					player.getWorld().playSound(next.getLocation(),Sound.DIG_GRAVEL, 1f, 0.1f);
					i--;
				}
				*/
				next.breakNaturally();
				
				/*
				Collection<ItemStack> tmp = next.getDrops();
				for (ItemStack is : tmp)
				{
					player.getWorld().dropItem(next.getLocation(), is);
				}
				*/
				
				//next.breakNaturally();
				//next.setType(Material.GLASS);	
			}
		}
		else
			instances.remove(player);
		
		
		
		
		
		
		
		
		
		if (length > 0)
		{
			length--;
			for (int i = 0; i < paths.size(); i++)
			{
				BlockIterator blit = paths.get(i);
				if (!blit.hasNext())
				{
					
					paths.remove(blit);
					i--;
					continue;
				}	
				
				next = blit.next();
				//next.setType(Material.GLASS);	
				
				if (length < 7 && (length < 3 || length % 2 == 0))
				{
					waveCloudEffect = new FireCloudEffect(Bending.getEffectManager(), next.getLocation());
					waveCloudEffect.start();
				}
				
				
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setVelocity(direction);
					((Damageable) e).damage(2.0, player);
				}
				
				/*
				if (!Tools.isCrushable(next) || nearEntities.size() > 0)
				{
					paths.remove(blit);
					player.getWorld().playSound(next.getLocation(),Sound.DIG_GRAVEL, 1f, 0.1f);
					i--;
				}
				*/
				
				
				/*
				Collection<ItemStack> tmp = next.getDrops();
				for (ItemStack is : tmp)
				{
					player.getWorld().dropItem(next.getLocation(), is);
				}
				*/
				
				//next.breakNaturally();
				//next.setType(Material.GLASS);	
			}
		}
		else
			instances.remove(player);
		
		
		
		
		return false;
	}
	
	
	
}
