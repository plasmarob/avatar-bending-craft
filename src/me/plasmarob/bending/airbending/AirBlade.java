package me.plasmarob.bending.airbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.Tools;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class AirBlade {

	public static ConcurrentHashMap<Player, AirBlade> instances = new ConcurrentHashMap<Player, AirBlade>();
	
	private Player player;
	private ArrayList<BlockIterator> paths = new ArrayList<BlockIterator>();
	private Block next;
	static final int WIDTH = 3;
	Vector direction;
	AirWaveEffect waveEffect;
	AirWaveVerticalEffect waveVertEffect;
	int length = 20;
	List<Entity> nearEntities;
	
	public AirBlade(Player player)
	{
		
		if (!instances.containsKey(player) && Tools.lastKey(player) == 2)
		{
			
			instances.put(player, this);
			this.player = player;
			direction = player.getEyeLocation().getDirection();
			Location tempLoc = player.getEyeLocation().clone();
			
			if (player.isSneaking())	// vertical wave
			{

				float pitch = tempLoc.getPitch();
				tempLoc.setPitch(pitch + 90);
				BlockIterator bt = new BlockIterator(tempLoc);
				for (int i = 0; i < 1; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
				}
				
				tempLoc = player.getEyeLocation().clone();
				pitch = tempLoc.getPitch();
				pitch -= 90;
				tempLoc.setPitch(pitch);
				bt = new BlockIterator(tempLoc);
				for (int i = 0; i < WIDTH + 1; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
				}
				
				// ignore blocks under me! (first few)
				for (BlockIterator blit : paths)
				{
					for (int i = 0; i < 3; i++)
						if(blit.hasNext())
							blit.next();
				}
				
				// effect and sound
				waveVertEffect = new AirWaveVerticalEffect(Bending.getEffectManager(), player.getEyeLocation());
				waveVertEffect.start();
			}
			else	// horizontal wave
			{
				
				float yaw = tempLoc.getYaw();
				yaw += 90;
				if (yaw > 0) yaw -= 360;
				tempLoc.setYaw(yaw);
				tempLoc.setPitch(0);
				BlockIterator bt = new BlockIterator(tempLoc);
				for (int i = 0; i < WIDTH; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
				}
				
				tempLoc = player.getEyeLocation().clone();
				yaw = tempLoc.getYaw();
				yaw -= 90;
				if (yaw < -360) yaw += 360;
				tempLoc.setYaw(yaw);
				tempLoc.setPitch(0);
				bt = new BlockIterator(tempLoc);
				for (int i = 0; i < WIDTH; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
				}
				//effect and sound
				waveEffect = new AirWaveEffect(Bending.getEffectManager(), player.getEyeLocation());
				waveEffect.start();		
			}
			
			player.getWorld().playSound(player.getLocation(),Sound.ENDERDRAGON_WINGS, 0.4f, 0.3f);
			
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
				
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setVelocity(direction);
					((Damageable) e).damage(2.0, player);
				}
				
				
				if (!Tools.isCrushable(next) || nearEntities.size() > 0)
				{
					paths.remove(blit);
					player.getWorld().playSound(next.getLocation(),Sound.DIG_GRAVEL, 0.2f, 0.1f);
					i--;
				}
				Collection<ItemStack> tmp = next.getDrops();
				for (ItemStack is : tmp)
				{
					player.getWorld().dropItem(next.getLocation(), is);
				}
				next.breakNaturally();
				
			}
			
		}
		else
		{
			instances.remove(player);
		}
		
		
		
		return false;
	}
}
