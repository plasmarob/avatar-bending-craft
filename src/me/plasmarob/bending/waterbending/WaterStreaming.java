package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class WaterStreaming {

	//DEPRECATED
	static ArrayList<Block> blocksLaunched = new ArrayList<Block>();
	static ArrayList<Vector> launchDirs = new ArrayList<Vector>();
	static final byte full = (byte)1; 
	
	// Stuff i'm actually using
	public static ConcurrentHashMap<Player, WaterStreaming> instances = new ConcurrentHashMap<Player, WaterStreaming>();
	private Player player;
	private BlockIterator bit;
	private Block next;
	
	
	
	private HashMap<Block, BlockIterator> blocksComing = new HashMap<Block, BlockIterator>();
	private HashMap<Block, BlockIterator> newBlocksComing;	
	
	
	private HashMap<Block, Integer> blocksFound = new HashMap<Block, Integer>();
	private HashMap<Block, Integer> newBlocks;
	private HashMap<Block, Integer> blocksRotatingMid = new HashMap<Block, Integer>();	
	private HashMap<Block, Integer> blocksRotatingTop = new HashMap<Block, Integer>();	
	private HashMap<Block, Integer> blocksRotatingBot = new HashMap<Block, Integer>();	
	
	Location loc;
	List<Entity> nearEntities;
	Entity target;
	int timer = 0;
	
	
	public WaterStreaming(Player player)
	{
		this.player = player;
		
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			if (!instances.containsKey(player))
			{
				instances.put(player, this);
			}
			else
			{
				if (!player.isSneaking())
				{
					((WaterStreaming)instances.get(player)).lookForTargetOrSource();
				}
				else
				{
					((WaterStreaming)instances.get(player)).lookForTargetOrAllSources();
				}
			}
			
			
		}
	}
	
	
	


	
	/** lookForTargetOrSource()
	 * - look for water, but choose a target if found first
	 * * This sequence is key to what we find
	 * - this guarantees we don't get water behind a target instead
	 * - also guarantees we get water and not underwater entity
	 * 
	 * - heldSize() condition ensures we look for water anyway if there's no ammo
	 */
	private void lookForTargetOrSource()
	{
		bit = new BlockIterator(player, 30);
		boolean decided = false;
		while (bit.hasNext())
		{	
			next = bit.next();
			//stop at mob if found
			nearEntities = Tools.getMobsAroundPoint(next.getLocation().clone(), 1.5);
			nearEntities.remove(player);
			if (nearEntities.size() > 0 & heldSize() > 0)
			{
				target = nearEntities.get(0);
				decided = true;
				
				double distance = 50;
				Block bullet = player.getEyeLocation().getBlock();
				if (blocksFound.size() > 0)
				{
					for (Block b : blocksFound.keySet())
					{
						if (Tools.getDirection(b.getLocation(), target.getLocation()).length() < distance)
							bullet = b;
					}
					blocksFound.remove(bullet);
					bullet.setType(Material.AIR);
					new LaunchedWater(player, bullet.getLocation(), target.getLocation().add(0,1,0));
				}
				else if (blocksComing.size() > 0)
				{
					for (Block b : blocksComing.keySet())
					{
						if (Tools.getDirection(b.getLocation(), target.getLocation()).length() < distance)
							bullet = b;
					}
					blocksComing.remove(bullet);
					bullet.setType(Material.AIR);
					new LaunchedWater(player, bullet.getLocation(), target.getLocation().add(0,1,0));
				}
				else if (blocksRotatingBot.size() > 0)
				{
					for (Block b : blocksRotatingBot.keySet())
					{
						if (Tools.getDirection(b.getLocation(), target.getLocation()).length() < distance)
							bullet = b;
					}
					blocksRotatingBot.remove(bullet);
					bullet.setType(Material.AIR);
					new LaunchedWater(player, player.getLocation(), target.getLocation().add(0,1,0));
				}
				else if (blocksRotatingTop.size() > 0)
				{
					for (Block b : blocksRotatingTop.keySet())
					{
						if (Tools.getDirection(b.getLocation(), target.getLocation()).length() < distance)
							bullet = b;
					}
					blocksRotatingTop.remove(bullet);
					bullet.setType(Material.AIR);
					new LaunchedWater(player, player.getLocation(), target.getLocation().add(0,1,0));
				}
				else if (blocksRotatingMid.size() > 0)
				{
					for (Block b : blocksRotatingMid.keySet())
					{
						if (Tools.getDirection(b.getLocation(), target.getLocation()).length() < distance)
							bullet = b;
					}
					blocksRotatingMid.remove(bullet);
					bullet.setType(Material.AIR);
					new LaunchedWater(player, player.getLocation(), target.getLocation().add(0,1,0));
				}
				break;
			}
			
			//stop at water if found without finding mob
			if (Tools.isOfWater(next) && 
				Tools.waterBreaks(next.getRelative(BlockFace.UP)) &&
				!WaterStreaming.blockHeld(next) &&
				!LaunchedWater.blockHeld(next)) // don't re-add same block
			{
				next.setType(Material.WATER);
				blocksFound.put(next, 0);
				decided = true;
				break;
			}
		}
		
		if (!decided)	// if nothing's found, just shoot
		{
			if (blocksRotatingBot.size() > 0)
			{
				Block bullet = player.getEyeLocation().getBlock();
				for (Block b : blocksRotatingBot.keySet())
				{
					bullet = b;
					break;
				}
				blocksRotatingBot.remove(bullet);
				bullet.setType(Material.AIR);
				new LaunchedWater(player);
			}
			else if (blocksRotatingTop.size() > 0)
			{
				Block bullet = player.getEyeLocation().getBlock();
				for (Block b : blocksRotatingTop.keySet())
				{
					bullet = b;
					break;
				}
				blocksRotatingTop.remove(bullet);
				bullet.setType(Material.AIR);
				new LaunchedWater(player);
			}
			else if (blocksRotatingMid.size() > 0)
			{
				Block bullet = player.getEyeLocation().getBlock();
				for (Block b : blocksRotatingMid.keySet())
				{
					bullet = b;
					break;
				}
				blocksRotatingMid.remove(bullet);
				bullet.setType(Material.AIR);
				new LaunchedWater(player);
			}
		}
		else if (!decided && player.getItemInHand().getType() == Material.WATER_BUCKET)
		{
			// TODO: bucket controlling logic here - filling & using
		}
	}
	
	
	
	
	private void lookForTargetOrAllSources() {
		bit = new BlockIterator(player, 30);
		boolean decided = false;
		while (bit.hasNext())
		{	
			next = bit.next();
			//stop at mob if found
			nearEntities = Tools.getMobsAroundPoint(next.getLocation().clone(), 1.5);
			nearEntities.remove(player);
			if (nearEntities.size() > 0 & heldSize() > 0)
			{
				target = nearEntities.get(0);
				decided = true;	
				for (Block b : blocksComing.keySet())
				{
					new LaunchedWater(player, b.getLocation(), target.getLocation().add(0,0.5,0));
					if (Tools.isWater(b.getType()))
						b.setType(Material.AIR);
				}
				for (Block b : blocksFound.keySet())
				{
					new LaunchedWater(player, b.getLocation(), target.getLocation().add(0,0.5,0));
					if (Tools.isWater(b.getType()))
						b.setType(Material.AIR);
				}
				for (Block b : blocksRotatingMid.keySet())
				{
					new LaunchedWater(player, b.getLocation(), target.getLocation().add(0,0.5,0));
					if (Tools.isWater(b.getType()))
						b.setType(Material.AIR);
				}
				for (Block b : blocksRotatingTop.keySet())
				{
					new LaunchedWater(player, b.getLocation(), target.getLocation().add(0,0.5,0));
					if (Tools.isWater(b.getType()))
						b.setType(Material.AIR);
				}
				for (Block b : blocksRotatingBot.keySet())
				{
					new LaunchedWater(player, b.getLocation(), target.getLocation().add(0,0.5,0));
					if (Tools.isWater(b.getType()))
						b.setType(Material.AIR);
				}
				blocksComing.clear();
				blocksFound.clear();
				blocksRotatingMid.clear();
				blocksRotatingTop.clear();
				blocksRotatingBot.clear();
				break;
			}
		}
		
		if (!decided)
		{
			Location center;// = player.getLocation().add(0,-1,0);
			int relativeHeight = 0;
			int waterCount = 0;
			for (float angle = 0; angle < 360; angle += 22.5) // 16 times
			{
				center = player.getLocation().add(0,-1,0);
				center.setPitch(0);
				center.setYaw(angle);
				bit = new BlockIterator(center, 0.0, 20);
				while (bit.hasNext())
				{
					next = bit.next().getRelative(0, relativeHeight, 0);
					//Block newBlock = next;
					boolean found = false;
					if (Tools.waterBreaks(next)) // is air, etc.
					{
						for (int i = 0; i < 20; i++)
						{
							if (Tools.waterBreaks(next)) // is air, etc.
							{
								relativeHeight--;
								next = next.getRelative(BlockFace.DOWN);
							}
							else
							{
								found = true;
								//next.setType(Material.PACKED_ICE);
								break;
							}
						}
					}
					else if (Tools.showsBelow(next.getRelative(BlockFace.UP)))
					{
						found = true;
						//next.setType(Material.PACKED_ICE);
					}
					else // something solid above
					{
						for (int i = 0; i < 20; i++)
						{
							if (Tools.showsBelow(next.getRelative(BlockFace.UP)))
							{
								found = true;
								//next.setType(Material.PACKED_ICE);
								break;
							}
							else
							{
								relativeHeight++;
								next = next.getRelative(BlockFace.UP);
							}
						}
					}
					if (!found) 
						break; // we couldn't find the next block, so terminate
					
					if (Tools.isWater(next.getType()))
					{
						waterCount++;
						if (waterCount >= 4)
						{
							if (Tools.waterBreaks(next.getRelative(BlockFace.UP)) &&
								!WaterStreaming.blockHeld(next) &&
								!LaunchedWater.blockHeld(next)) // don't re-add same block
							{
								next.setType(Material.WATER);
								blocksFound.put(next, -1);
								break; // found our souce - time to leave.
							}
						}
					}
					else
					{
						// if there's not enough water, start over looking.
						if (waterCount > 0)
							waterCount = 0;
					}
					
					
				}
			}
		}
	}


	private int heldSize()
	{
		return blocksFound.size() + blocksComing.size() 
		+ blocksRotatingMid.size() + blocksRotatingTop.size() + blocksRotatingBot.size();
	}



	//@SuppressWarnings("deprecation")
	public boolean progress() {
		timer++;
		
		// Move BLOCKS FOUND slowly
		if (timer % 4 == 0)
		{
			newBlocks = new HashMap<Block, Integer>();
			for (Block b : blocksFound.keySet())
			{
				int n = blocksFound.get(b) + 1; // increment rising process
				
				if (n < 3)
				{
					if (Tools.waterBreaks(b.getRelative(BlockFace.UP)))
					{
						b.setType(Material.AIR);
						newBlocks.put(b.getRelative(BlockFace.UP), n);
						b.getRelative(BlockFace.UP).setType(Material.WATER);
					}
					else
					{
						newBlocks.put(b, n); //keep the same one, but increment n
					}
				}
				else
				{
					loc = b.getLocation().clone();
					Vector vec = Tools.getDirection(loc, player.getEyeLocation()).clone();
					if (vec.length() > 3)
					{
						loc.setDirection(vec);
						blocksComing.put(b, new BlockIterator(loc, 0.0, 4));
					}
					else
					{
						// convert to player held block
						next.setType(Material.AIR);
					}
				}
			}
			blocksFound.clear();
			blocksFound.putAll(newBlocks);
		}
		
		
		if (true) // (timer % 2 == 0)
		{
			// Move BLOCKS COMING to player
			newBlocksComing = new HashMap<Block, BlockIterator>();
			for (Block b : blocksComing.keySet())
			{
				if (Tools.isWater(b.getType()))
					b.setType(Material.AIR);
				BlockIterator bitTemp = blocksComing.get(b);
				if (bitTemp.hasNext())
				{
					next = bitTemp.next();
					if (Tools.waterBreaks(next))
						next.setType(Material.WATER);
					//When we run out of iterator, add a new one
					if (!bitTemp.hasNext())
					{
						loc = b.getLocation().clone();
						Vector vec = Tools.getDirection(loc, player.getEyeLocation()).clone();
						if (vec.length() > 3)
						{
							loc.setDirection(vec);
							newBlocksComing.put(next, new BlockIterator(loc, 0.0, 4));
						}
						else
						{
							// convert to player held block
							if (Tools.isWater(b.getType()))
								next.setType(Material.AIR);
							
							if (blocksRotatingMid.size() < 10)
							{
								blocksRotatingMid.put(next, 0); //only adds 1 to the size 
								double anglechange = 360/blocksRotatingMid.size();
								double angle = 0;
								for (Block br : blocksRotatingMid.keySet())
								{
									blocksRotatingMid.put(br, (int)angle);
									angle += anglechange;
								}
							}
							else if (blocksRotatingTop.size() < 10)
							{
								blocksRotatingTop.put(next, 0); //only adds 1 to the size 
								double anglechange = 360/blocksRotatingTop.size();
								double angle = 0;
								for (Block br : blocksRotatingTop.keySet())
								{
									blocksRotatingTop.put(br, (int)angle);
									angle += anglechange;
								}
							}
							else if (blocksRotatingBot.size() < 10)
							{
								blocksRotatingBot.put(next, 0); //only adds 1 to the size 
								double anglechange = 360/blocksRotatingBot.size();
								double angle = 0;
								for (Block br : blocksRotatingBot.keySet())
								{
									blocksRotatingBot.put(br, (int)angle);
									angle += anglechange;
								}
							}
							
							
						}
					}
					else
					{
						newBlocksComing.put(next, bitTemp);
					}
				}
				
			}
			blocksComing.clear();
			blocksComing.putAll(newBlocksComing);
		}
		
		
		
		
		newBlocks = new HashMap<Block, Integer>();
		for (Block b : blocksRotatingMid.keySet())
		{
			if (Tools.isWater(b.getType()))
					b.setType(Material.AIR);
			int angle = blocksRotatingMid.get(b);
			angle += 5;
			
			loc = player.getEyeLocation().clone();
			loc.setYaw(angle);
			loc.setPitch(0);
			bit = new BlockIterator(loc, 0.0, 3);
			while (bit.hasNext())
				next = bit.next();
			if (Tools.waterBreaks(next))
				next.setType(Material.WATER);
			newBlocks.put(next, angle);
		}
		blocksRotatingMid.clear();
		blocksRotatingMid.putAll(newBlocks);
		
		newBlocks = new HashMap<Block, Integer>();
		for (Block b : blocksRotatingTop.keySet())
		{
			if (Tools.isWater(b.getType()))
					b.setType(Material.AIR);
			int angle = blocksRotatingTop.get(b);
			angle += 5;
			
			loc = player.getEyeLocation().add(0,1,0).clone();
			loc.setYaw(angle);
			loc.setPitch(0);
			bit = new BlockIterator(loc, 0.0, 3);
			while (bit.hasNext())
				next = bit.next();
			if (Tools.waterBreaks(next))
				next.setType(Material.WATER);
			newBlocks.put(next, angle);
		}
		blocksRotatingTop.clear();
		blocksRotatingTop.putAll(newBlocks);
		
		newBlocks = new HashMap<Block, Integer>();
		for (Block b : blocksRotatingBot.keySet())
		{
			if (Tools.isWater(b.getType()))
					b.setType(Material.AIR);
			int angle = blocksRotatingBot.get(b);
			angle += 5;
			
			loc = player.getEyeLocation().add(0,-1,0).clone();
			loc.setYaw(angle);
			loc.setPitch(0);
			bit = new BlockIterator(loc, 0.0, 3);
			while (bit.hasNext())
				next = bit.next();
			if (Tools.waterBreaks(next))
				next.setType(Material.WATER);
			newBlocks.put(next, angle);
		}
		blocksRotatingBot.clear();
		blocksRotatingBot.putAll(newBlocks);
		
		if (heldSize() == 0)
		{
			instances.remove(player);
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
		/*
		if (blocksPulled != null && blocksPulled.contains(block))
			return true;
			*/
		if (blocksFound != null && blocksFound.containsKey(block))
			return true;
		if (blocksComing != null && blocksComing.containsKey(block))
			return true;
		if (blocksRotatingMid != null && blocksRotatingMid.containsKey(block))
			return true;
		if (blocksRotatingTop != null && blocksRotatingTop.containsKey(block))
			return true;
		if (blocksRotatingBot != null && blocksRotatingBot.containsKey(block))
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






