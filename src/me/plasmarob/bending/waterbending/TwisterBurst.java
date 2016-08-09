package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.util.Tools;

public class TwisterBurst {

	public static ConcurrentHashMap<Integer, TwisterBurst> instances = new ConcurrentHashMap<Integer, TwisterBurst>();
	private int id;
	public static int count = 0;
	Location origin;
	Player player;
	static int maxRadius = 20;
	// the angle to move by each time in making the circle
	static final int alphaAngle = (int) 60 / (maxRadius + 1);
	int delay = 0;
	ArrayList<BlockIterator> iterators = new ArrayList<BlockIterator>();
	ArrayList<Block> waterBlocks = new ArrayList<Block>();
	int currentRadius = 0;
	List<Entity> nearEntities;
	ArrayList<Entity> damagedEntities = new ArrayList<Entity>();
	
	ArrayList<Block> tempWaters = new ArrayList<Block>();
	

	public TwisterBurst(Player player)
	{
		new TwisterBurst(player, 20);
	}
	
	public TwisterBurst(Player player, int r)
	{
		count++;
		id = count;
		instances.put(id, this);
		maxRadius = r;
		
		this.player = player;
		origin = player.getLocation().clone();
		origin.setPitch(0);

		int angle = 0;
		while (angle < 360)
		{
			origin.setYaw(angle);
			iterators.add(new BlockIterator(origin));
			origin.add(0,1,0);
			iterators.add(new BlockIterator(origin));
			origin.add(0,1,0);
			iterators.add(new BlockIterator(origin));
			origin.add(0,-2,0);
			angle += alphaAngle;
		}
	}
	
	
	
	private boolean hasBlock(Block block)
	{
		if (waterBlocks == null)
			return false;
		if (waterBlocks.contains(block))
			return true;
		return false;
	}
	public static boolean blockHeld(Block block)
	{
		for (Integer i : instances.keySet())
		{
			if (instances.get(i).hasBlock(block))
				return true;
		}
		return false;
	}
	
	public static void progressAll() {
		if (instances.size() > 0)
			for (Integer p : instances.keySet())
				instances.get(p).progress();
	}
	public static boolean progress(Integer p) {
		if (instances.get(p) != null)
			return instances.get(p).progress();
		return false;
	}
	public boolean progress() {
		delay++;
		
		for(Block b : waterBlocks)
			b.setType(Material.AIR);
		waterBlocks.clear();
		
		currentRadius++;
		if (currentRadius > maxRadius)
		{
			instances.remove(id);
			return false;
		}
		
		BlockIterator bit;
		Block next;
		for (int i = 0; i < iterators.size(); i++)
		{
			bit = iterators.get(i);
			if (!bit.hasNext())
				continue;
			next = bit.next();
			if(next.getLocation().distance(origin) < currentRadius)
			{
				i--;
				continue;
			}
			// test for already added block, add block, set air to water, etc
			if (!waterBlocks.contains(next))
			{
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.breakNaturally();
					next.setType(Material.STATIONARY_WATER);
					waterBlocks.add(next);
				}
				else
				{
					iterators.remove(i);
					i--;
				}
				
			}	
		}
		
		
		nearEntities = Tools.getEntitiesAroundPoint(origin, currentRadius+2);
		nearEntities.remove(player);
		for (Entity e : nearEntities)
		{
			//if (origin.distance(e.getLocation()) > currentRadius-1)
			if (waterBlocks.contains(e.getLocation().getBlock())
					|| (e instanceof LivingEntity && 
					waterBlocks.contains(((LivingEntity)e).getEyeLocation().getBlock())) )
			{
				e.setVelocity(Tools.getDirection(origin, e.getLocation()).normalize().multiply(1.2));
				if (!damagedEntities.contains(e) && e instanceof Damageable)
					((Damageable)e).damage(2.0, player);
				damagedEntities.add(e);
			}	
		}
		
		return false;
	}

}
