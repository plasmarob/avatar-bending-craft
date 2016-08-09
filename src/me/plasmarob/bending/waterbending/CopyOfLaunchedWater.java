package me.plasmarob.bending.waterbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.util.Tools;

/**
 * 
 * @author Robert
 *
 */
public class CopyOfLaunchedWater
{
	public static ConcurrentHashMap<Integer, CopyOfLaunchedWater> instances = new ConcurrentHashMap<Integer, CopyOfLaunchedWater>();
	static int counter = 0;
	private int id = 0;
	Block next, prev, tmp;
	Location location;
	BlockIterator bit;
	static final byte full = (byte)4; 
	List<Entity> nearEntities;
	Player player;
	Vector throwPath;
	
	public CopyOfLaunchedWater(Player player, Location from, Location to)
	{
		this.player = player;
		counter++;
		id = counter;
		instances.put(new Integer(id), this);
		throwPath = Tools.getDirection(from, to).clone().normalize();
		Location bitLoc = from.clone();
		bitLoc.setDirection(throwPath.clone()); // get direct path
		throwPath.setY(throwPath.getY()+0.5); // make entity hit go up a little
		throwPath.normalize();
		throwPath = throwPath.multiply(0.5);
		bit = new BlockIterator(bitLoc, 0.0, 30);
		next = bit.next();
		next = bit.next();
		prev = next;
		next = bit.next();
	}
	
	public CopyOfLaunchedWater(Player player)
	{
		this.player = player;
		counter++;
		id = counter;
		instances.put(new Integer(id), this);
		throwPath = player.getEyeLocation().clone().getDirection().normalize();
		throwPath.setY(throwPath.getY()+0.5);
		bit = new BlockIterator(player, 30);
		next = bit.next();
		next = bit.next();
		prev = next;
		next = bit.next();
	}
	
	public void progress()
	{
		for (int i = 0; i < 2; i++)
		{
			if (!bit.hasNext())
			{	
				if (Tools.isWater(next.getType()))
					next.setType(Material.AIR);
				if (Tools.isWater(prev.getType()))
					prev.setType(Material.AIR);
				instances.remove(id);
				return;
			}	
			prev = next;
			tmp = bit.next();
			//look for collisions with other LaunchedWater
			if (CopyOfLaunchedWater.blockHeld(tmp))
			{
				if (Tools.isWater(prev.getType()))
					prev.setType(Material.AIR);
				instances.remove(id);
				return;
			}
			next = tmp;
			
			if (!Tools.waterBreaks(next) 
					|| Tools.isWater(next.getType()))
			{
				//added stationary water here
				if (Tools.isWater(prev.getType()))
				{
					prev.setType(Material.AIR);
				}
				instances.remove(id);
				return;
			}
			
			prev.setType(Material.AIR);
			next.setType(Material.WATER);

			nearEntities = Tools.getMobsAroundPoint(next.getLocation().clone(), 1.5);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				entity.setVelocity(throwPath.multiply(1.2));
				((Damageable)entity).damage(4, player);
			}
		}
	}
	
	public static void progressAll()
	{
		if (instances != null && instances.size() > 0)
			for (Integer i : instances.keySet())
			{
				instances.get(i).progress();
			}
	}

	@SuppressWarnings("unused")
	private boolean hasBlock(Block blok)
	{
		if ( (next != null && next.equals(blok)) ||
				(prev != null && prev.equals(blok)) )
			return true;
		return false;
	}
	public static boolean blockHeld(Block block)
	{
		for (CopyOfLaunchedWater lw : instances.values())
		{
			if (lw.next.equals(block) || lw.prev.equals(block))
				return true;
		}
		return false;
	}	
}
