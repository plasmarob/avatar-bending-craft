package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.BendingPlayer;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class WaterBlade extends AbstractBendingForm {

	public static ConcurrentHashMap<Player, WaterBlade> instances = new ConcurrentHashMap<Player, WaterBlade>();
	private Player player;
	private ArrayList<Block> bladeBlocks = new ArrayList<Block>(); //actes like a queue for add/remove
	private ArrayList<Block> heldBlocks = new ArrayList<Block>();
	private int it = 0;  
	private boolean swipeDir;
	
	private Block next;
	static final int WIDTH = 3;
	Vector direction;
	
	int length = 4;
	List<Entity> nearEntities;
	boolean ending = false;
	
	public WaterBlade(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			instances.put(player, this);
			this.player = player;
			swipeDir = BendingPlayer.getBendingPlayer(player).getSwipeDir();
			BendingPlayer.getBendingPlayer(player).toggleSwipe();
			
			int radius = length;
			double count = 15;
			double arcAngle = 90;
			double angleHalf = arcAngle/2.0;
			double angle = arcAngle/(count-1);
			direction = player.getEyeLocation().getDirection();
			
			//get the axis unit vector K around which to rotate for horizontal
			Location unitTemp = player.getEyeLocation().clone();
			
			unitTemp.setYaw(unitTemp.getYaw()-90);
			Vector k = unitTemp.getDirection();
			k.setY(0);
			k = k.normalize();
			
			// end axis vector
			
			for (int i = 0; i < count; i++)
			{
				if (player.isSneaking())	// vertical wave
				{
					Location l = player.getEyeLocation().clone();
					l.setPitch((float) ( l.getPitch() - angleHalf + angle*i ));
					
					bit = new BlockIterator(l,0,radius);
					while (bit.hasNext())
						next = bit.next();
					bladeBlocks.add(next);
				}
				else
				{
					Location l = player.getEyeLocation().clone();
					l.setDirection(l.getDirection().setY(0));
					l.setYaw((float) ( l.getYaw() - angleHalf + angle*i ));
					Vector v = l.getDirection().clone();
					v = v.normalize();

					float theta = player.getEyeLocation().clone().getPitch();
					Vector part1 = v.clone().multiply(Math.cos(Math.toRadians(theta)));
					Vector part2 = k.clone().crossProduct(v.clone()).multiply(Math.sin(Math.toRadians(theta)));
					Vector part3 = k.clone().multiply(k.clone().dot(v.clone())).multiply(1-Math.cos(Math.toRadians(theta)));
					Vector vRot = part1.add(part2).add(part3);
					
					l.setDirection(vRot);
					
					bit = new BlockIterator(l,0,radius);
					while (bit.hasNext())
						next = bit.next();
					bladeBlocks.add(next);	
				}
			}
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ARROW_SHOOT,0.5f, 0.4f);
			
			
			if (swipeDir)
				it = bladeBlocks.size() - 1;
			else
				it = 0;
			
			//start at the angle where it can appear
			while (!Tools.waterBreaks(bladeBlocks.get(it)))
			{
				next = bladeBlocks.get(it);
				
				it = (swipeDir) ? (it-1) : (it+1);
				
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setVelocity(direction);
					((Damageable) e).damage(4.0, player);
				}
				if ( it < 0 || it >= bladeBlocks.size() )
				{
					instances.remove(player);
					break;
				}
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
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		for (int n = 0; n < 2; n++)
		{
			if (ending)
			{
				if (heldBlocks.size() > 0)
				{
					if (Tools.isWater(heldBlocks.get(0).getType()))
						heldBlocks.get(0).setType(Material.AIR);
					heldBlocks.remove(0);
				}
				else
					instances.remove(player);
			}
			else	// main loop
			{
				if (it < 0 || it >= bladeBlocks.size())
				{
					ending = true;
					return false;
				}
				
				// get next one
				next = bladeBlocks.get(it);
				it = (swipeDir) ? (it-1) : (it+1);
				// deal damage
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setVelocity(direction);
					((Damageable) e).damage(4.0, player);
				}
				// break stuff and trail off
				if (!Tools.isUnbreakable(next))
				{
					if (!Tools.isWater(next.getType()) && !Tools.waterBreaks(next))
						ending = true;
					next.breakNaturally();
					next.setType(Material.WATER);
					
					heldBlocks.add(next);
					if (heldBlocks.size() > 4)
					{
						heldBlocks.get(0).setType(Material.AIR);
						heldBlocks.remove(0);
					}
				}
			}
		}
		
		//keep it small and blade-like
		for (int i = 1; i < heldBlocks.size(); i++)
		{
			heldBlocks.get(i).setData((byte)6);
		}
		
		return false;
	}
	
	
	private boolean hasBlock(Block block)
	{
		if (heldBlocks != null && heldBlocks.contains(block))
			return true;
		return false;
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (instances.get(p).hasBlock(block))
				return true;
		}
		return false;
	}
}
