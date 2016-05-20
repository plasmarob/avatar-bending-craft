package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

// TODO: I need to add support for ice and snow and other water sources.
// TODO: I need to add damage and knockback for touching it (NO climbing up it!)
public class WaterTwister extends BendingForm {
	
	ArrayList<Block> sourceBlocks;
	int bottomY;
	int topY;
	private ArrayList<Block> usedBlocks;
	private Location startLocation;
	int delay = 2;
	int cycle = 0;
	int maxHeight = 12;
	boolean allowFlight;
	boolean wasFlying;
	int wearDownTimer = 0;
	double energy = 0;
	
	public static ConcurrentHashMap<Player, BendingForm> instances = new ConcurrentHashMap<Player, BendingForm>();
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
	
	public WaterTwister(Player player)
	{
		if (Tools.lastKey(player) != PlayerAction.LEFT_CLICK.val())
			return;
		
		if (!instances.containsKey(player))
		{
			// This block of code looks down 20 blocks for a 2x2 of water.
			// It fails on a non air/water block.
			Location sourceLoc = player.getEyeLocation().clone().add(0, 1, 0);
			boolean found;
			for (int i = 0; i < maxHeight+1+2; i++)
			{
				if (i == maxHeight || sourceLoc.getY() < 1) return; //fail if we haven't found water.
				sourceBlocks = Tools.get2x2LocationBlocks(sourceLoc);
				sourceLoc.add(0, -1, 0);
				found = true;
				for (Block b : sourceBlocks)
				{
					if (b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER)
						continue;
					found = false;	// we need a 2x2 of water
					if (b.getType() != Material.AIR)
						return; // fail if there's not air/water
					else
						break; // not yet if there's air
				}
				if (found) 
					break;
			}
			// we only get here if we have found a 2x2 of water in 20 tries.
			
			usedBlocks = new ArrayList<Block>();
			
			this.player = player;
			allowFlight = player.getAllowFlight();
			wasFlying = player.isFlying();
			
			player.setAllowFlight(true);
			player.setFlying(true);
			
			instances.put(player, this);
	
			bottomY = sourceLoc.getBlockY();
			topY = bottomY;
			startLocation = player.getLocation().clone();
			startLocation.setY(topY);
			cycle = 0;
		}
		else
		{
			// if you're here, left click occurred and player has twister
			
			new LaunchedWater(player);

			/*
			instances.get(player).kill();
			instances.remove(player);
			*/
		}
	}
	
	
	public boolean progress() {	
		
		player.setFallDistance(0);
		
		// If on land too long, end it
		Block standBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if ( ( (!Tools.isOfWater(standBlock) && standBlock.getType() != Material.AIR)
				|| ((Entity)player).isOnGround()) && player.isSneaking())
		{
			new TwisterBurst(player, (int)(energy));
			remove();
			return false;
		}
		if (maxHeight < 1)
		{	
			remove();
			return false;
		}
		
		
		Block bottomBlock = player.getLocation().getBlock();
		while(bottomBlock.getY() > 1)
		{
			bottomBlock = bottomBlock.getRelative(BlockFace.DOWN);
			if (bottomBlock.getType() != Material.STATIONARY_WATER && 
				bottomBlock.getType() != Material.WATER &&
				bottomBlock.getType() != Material.AIR) 
			{
				if (maxHeight > topY - bottomY)
				maxHeight = topY - bottomY;
				break;
			}
			if (!usedBlocks.contains(bottomBlock) &&
				(bottomBlock.getType() == Material.STATIONARY_WATER || 
				bottomBlock.getType() == Material.WATER))
			{
				break;
			}
		}
		if (wearDownTimer == 0)
		{
			if (!Tools.isOfWater(bottomBlock) && !Tools.waterBreaks(bottomBlock))	
			{
				maxHeight--;
			}
			else
				maxHeight = 12;
		}
		wearDownTimer = (wearDownTimer + 1) % 15;

		if ( player.getLocation().getY() - topY < 1)
		{	
			
			if (player.isSneaking())
				energy += 2*(topY - player.getLocation().getBlockY());
			else if (energy > 0.2)
				energy -= 0.2;
			//Tools.say(energy);
			topY = (int) player.getLocation().getY();
		}
		else if (delay % 2 == 0)
			topY++;
		
		
		bottomY = bottomBlock.getY();
		startLocation = player.getLocation().clone();
		startLocation.setY(bottomY);
		
		
		int height = topY - bottomY;
		if (height > maxHeight || topY != (int) player.getLocation().getY()) {
			player.setFlying(false);
		} else {
			player.setAllowFlight(true);
			player.setFlying(true);
		}	
		
		delay++;
		if (delay % 2 == 0)
			return true;
		cycle+=3;
	
		
		Block b;
		flushBlocks();
		for (int i = -1; i < (topY - bottomY); i++)
		{
			b = Tools.get2x2CornerBlock(startLocation.clone()).getRelative(0,i+1,0);
			
			int t = (i + cycle) % 4;
			
			switch (t)
			{
				case 0:
					if (b.getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.setType(Material.STATIONARY_WATER);
						usedBlocks.add(b);
					}
					if (b.getRelative(0,0,1).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(0,0,1).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(0,0,1));
					}
					break;
				case 1:
					if (b.getRelative(0,0,1).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(0,0,1).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(0,0,1));
					}
					if (b.getRelative(1,0,1).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(1,0,1).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(1,0,1));
					}
					break;
				case 2:
					if (b.getRelative(1,0,1).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(1,0,1).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(1,0,1));
					}
					if (b.getRelative(1,0,0).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(1,0,0).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(1,0,0));
					}
					break;
				case 3:
					if (b.getRelative(1,0,0).getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.getRelative(1,0,0).setType(Material.STATIONARY_WATER);
						usedBlocks.add(b.getRelative(1,0,0));
					}
					if (b.getType() == Material.AIR || b.getType() == Material.FIRE)
					{
						b.setType(Material.STATIONARY_WATER);
						usedBlocks.add(b);
					}
					break;
			}
		}
		
		return true;
	}
	
	@Override
	public void remove() 
	{
		instances.remove(player);
		player.setFlying(wasFlying);
		player.setAllowFlight(allowFlight);
		flushBlocks();
	}
	public static void removeAll() {
		if (instances.size() > 0)
		{
			List<Player> list = Collections.list(instances.keys());
			for (Player p : list)
				instances.get(p).remove();
		}
	}
	public static void removePlayer(Player player) {
		if (instances.get(player) != null)
			instances.get(player).remove();
	}
	
	
	
	public void flushBlocks()
	{
		for (Block b : usedBlocks)
		{
			b.setType(Material.AIR);
		}
		usedBlocks.clear();
	}
	private boolean hasBlock(Block block)
	{
		if (usedBlocks == null)
			return false;
		if (usedBlocks.contains(block))
			return true;
		return false;
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (((WaterTwister)instances.get(p)).hasBlock(block))
				return true;
		}
		return false;
	}
}
