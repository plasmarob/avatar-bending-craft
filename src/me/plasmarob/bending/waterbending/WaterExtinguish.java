package me.plasmarob.bending.waterbending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class WaterExtinguish extends AbstractBendingForm {
	
	public static ConcurrentHashMap<Player, WaterExtinguish> instances = new ConcurrentHashMap<Player, WaterExtinguish>();
	Block currentBlock;
	boolean coming = false;
	boolean healing = false;
	int healTimer = 0;
	boolean justPlayer = false;
	
	public WaterExtinguish(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			this.player = player;
			next = player.getLocation().getBlock();
			if (!player.isSneaking())
			{
				if (player.getFireTicks() > 0)
				{
					player.setFireTicks(0);
				}
				else
				{
					instances.put(player, this);
					bit = new BlockIterator(player, 10);
				}
			}
			else
			{
				/*
				next = player.getLocation().getBlock();
				if (Tools.waterBreaks(next))
				{
					next.breakNaturally();
					next.setType(Material.WATER);
					currentBlock = next;
				}
				*/
				
				healing = true;
				justPlayer = true;
				instances.put(player, this);
				bit = new BlockIterator(player, 10);
			}
			
		}
	}
	
	@Override
	public boolean progress() {
		
		if (player.isSneaking())
		{
			healing = true;
			if (currentBlock != null && currentBlock.getType() == Material.WATER)
				currentBlock.setType(Material.STATIONARY_WATER);
		}
			
		if (healing == false)
		{
			if (bit.hasNext())
			{
				next = bit.next();
				
				
				// stop burning
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
				if (nearEntities.contains(player))
					nearEntities.remove(player);
				for (Entity e : nearEntities)
				{
					e.setFireTicks(0);
				}
				
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.breakNaturally();
					next.setType(Material.WATER);
					if (currentBlock != null)
						currentBlock.setType(Material.AIR);
					currentBlock = next;
				}
				else // drain the rest to get to the else if
				{
					if (!coming)
					{
						@SuppressWarnings("unused")
						Block tmp;
						while (bit.hasNext())
							 tmp = bit.next();
					}
				}
				
				
				if (next.getRelative(BlockFace.DOWN).getType() == Material.FIRE)
					next.getRelative(BlockFace.DOWN).breakNaturally();
				if (next.getRelative(BlockFace.UP).getType() == Material.FIRE)
					next.getRelative(BlockFace.UP).breakNaturally();
				if (next.getRelative(BlockFace.NORTH).getType() == Material.FIRE)
					next.getRelative(BlockFace.NORTH).breakNaturally();
				if (next.getRelative(BlockFace.SOUTH).getType() == Material.FIRE)
					next.getRelative(BlockFace.SOUTH).breakNaturally();
				if (next.getRelative(BlockFace.EAST).getType() == Material.FIRE)
					next.getRelative(BlockFace.EAST).breakNaturally();
				if (next.getRelative(BlockFace.WEST).getType() == Material.FIRE)
					next.getRelative(BlockFace.WEST).breakNaturally();
				
				if (next.getType() == Material.LAVA)
				{
					next.setType(Material.OBSIDIAN);

					// drain the rest to get to the else if
					if (!coming)
					{
						@SuppressWarnings("unused")
						Block tmp;
						while (bit.hasNext())
							 tmp = bit.next();
					}
					
					if (next.getRelative(BlockFace.DOWN).getType() == Material.LAVA)
						next.getRelative(BlockFace.DOWN).setType(Material.OBSIDIAN);
					if (next.getRelative(BlockFace.UP).getType() == Material.LAVA)
						next.getRelative(BlockFace.UP).setType(Material.OBSIDIAN);
					if (next.getRelative(BlockFace.NORTH).getType() == Material.LAVA)
						next.getRelative(BlockFace.NORTH).setType(Material.OBSIDIAN);
					if (next.getRelative(BlockFace.SOUTH).getType() == Material.LAVA)
						next.getRelative(BlockFace.SOUTH).setType(Material.OBSIDIAN);
					if (next.getRelative(BlockFace.EAST).getType() == Material.LAVA)
						next.getRelative(BlockFace.EAST).setType(Material.OBSIDIAN);
					if (next.getRelative(BlockFace.WEST).getType() == Material.LAVA)
						next.getRelative(BlockFace.WEST).setType(Material.OBSIDIAN);
				}
			}
			else if (!coming)
			{
				coming = true;
				Vector vec = Tools.getDirection(next.getLocation(), player.getEyeLocation());
				Location loc = next.getLocation().clone();
				loc.setDirection(vec);
				bit = new BlockIterator(loc, 0.0, (int)vec.length());
			}
			else
			{
				if (currentBlock != null)
					currentBlock.setType(Material.AIR);
				instances.remove(player);
			}
		}
		else
		{
			healTimer++;
			
			
			if (justPlayer && currentBlock != player.getLocation().getBlock())
			{
				if (currentBlock != null)
					currentBlock.setType(Material.AIR);
				next = player.getLocation().getBlock();
				if (Tools.waterBreaks(next) || Tools.isOfWater(next))
				{
					currentBlock = next;
					currentBlock.breakNaturally();
					currentBlock.setType(Material.STATIONARY_WATER);
				}
			}
			
			/*
			if (currentBlock != null && currentBlock.getType() == Material.WATER)
			{
				currentBlock.setType(Material.STATIONARY_WATER);
			}
			*/
			
			if (healTimer % 10 == 0 && player.isSneaking())
			{
				nearEntities = Tools.getMobsAroundPoint(currentBlock.getLocation(), 2);
				for (Entity e : nearEntities)
				{
					if (e instanceof LivingEntity)
					{
						LivingEntity le = (LivingEntity)e;
						if (le.getHealth() < le.getMaxHealth())
						{
							if (le.getHealth()+1 < le.getMaxHealth())
								le.setHealth(le.getHealth()+1);
							else
								le.setHealth(le.getMaxHealth());
						}
					}
				}
			}
			
			if (justPlayer && player.getLocation().getBlock() != currentBlock)
			{
				if (currentBlock != null)
					currentBlock.setType(Material.AIR);
				
				Block myBlock = player.getLocation().getBlock();
				if (Tools.waterBreaks(myBlock))
				{
					myBlock.breakNaturally();
					myBlock.setType(Material.STATIONARY_WATER);
					currentBlock = myBlock;
				}
			}
			
			
			if (justPlayer && !player.isSneaking())
			{
				if (currentBlock != null)
					currentBlock.setType(Material.AIR);
				instances.remove(player);
			}
			
			if (!player.isSneaking())
			{
				healing = false;
			}
			
		}
		
		return false;
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
	
	
	private boolean hasBlock(Block block)
	{
		if (currentBlock == block)
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
