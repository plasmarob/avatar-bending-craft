package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class CopyOfWaterStreaming {

	public static ConcurrentHashMap<Player, CopyOfWaterStreaming> instances = new ConcurrentHashMap<Player, CopyOfWaterStreaming>();
	private Player player;
	private ConcurrentHashMap<Block, Float> angles = new ConcurrentHashMap<Block, Float>();
	private ArrayList<Block> blocksPulled = new ArrayList<Block>();
	private ArrayList<Block> blocksHeld = new ArrayList<Block>();
	
	static ArrayList<Block> blocksLaunched = new ArrayList<Block>();
	static ArrayList<Vector> launchDirs = new ArrayList<Vector>();
	static final byte full = (byte)1; 
	
	public CopyOfWaterStreaming(Player player)
	{
		if (Tools.lastKey(player) == 1 && !instances.containsKey(player))
		{
			instances.put(player, this);
			this.player = player;
			
		}
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val() 
				&& instances.containsKey(player))
		{
			instances.get(player).streamAction();
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
	
	
	public static boolean hasPlayer(Player p)
	{
		return instances.containsKey(p);
	}
	
	@SuppressWarnings("deprecation")
	public void streamAction()
	{
		
		//player.getItemInHand().getType() == Material.WATER_BUCKET
		BlockIterator bit = new BlockIterator(player, 10);
		Block next;
		int mode = 1;
		while (bit.hasNext())
		{
			next = bit.next();
			if (Tools.isOfWater(next) && !blocksPulled.contains(next))
			{
				if (blocksHeld.size() < 3 && !blocksHeld.contains(next))
				{
					blocksPulled.add(next);
					mode = 0;
				}
				break;
			}
		}
		
		
		
		if (mode == 1 && blocksHeld.size() > 0)
		{
			Block b = blocksHeld.get(0);
			angles.remove(b);
			blocksHeld.remove(b);
			b.setType(Material.AIR);
			b.setType(Material.WATER);
			b.setData(full);
			//blocksLaunched.add(b);
			new LaunchedWater(player);
		}
		else if (mode == 1 && blocksHeld.size() < 3 && 
			player.getItemInHand().getType() == Material.WATER_BUCKET)
		{
			ItemStack is = new ItemStack(Material.BUCKET);
			player.setItemInHand(is);
			blocksHeld.add(player.getEyeLocation().getBlock());
			angles.put(player.getEyeLocation().getBlock(), 0.0f);
			mode = 0;
		}
		
		return;
	}
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		
		
		if (!player.isSneaking())
		{
			for (Block b : blocksHeld)
			{
				b.setType(Material.AIR);
				b.setType(Material.WATER);
				b.setData(full);
			}
			for (Block b : blocksPulled)
			{
				b.setType(Material.AIR);
				b.setType(Material.WATER);
				b.setData(full);
			}
			angles.clear();
			instances.remove(player);
			return false;
		}
		
	
		
		// Rotate the block around the player (second part)
		int sizeToTry = blocksHeld.size();
		for (int i = 0; i < sizeToTry; i++)
		{
			Block b = blocksHeld.get(i);
			
			Block t;//dummy value
			Location tLoc = player.getEyeLocation().clone();
			float angle = angles.get(b) + 20;
			tLoc.setYaw(angle);
			tLoc.setPitch(0);
			
			BlockIterator tBit = new BlockIterator(tLoc, 0.0, 3);
			t = tBit.next();
			while (tBit.hasNext())
				t = tBit.next();
			
			angles.remove(b);
			if (t.getType() == Material.AIR && t.getLocation().distance(b.getLocation()) >= 1)
			{
				blocksHeld.remove(b);
				t.setType(Material.WATER);
				b.setType(Material.AIR);
				blocksHeld.add(t);
				angles.put(t, angle);
				//i--;
				//sizeToTry--;		
			}
			else
			{
				angles.put(b, angle);
			}
		}
		
		
		// Bring the block to the player (first part)
		sizeToTry = blocksPulled.size();
		for (int i = 0; i < sizeToTry; i++)
		{
			//if ()
			Block b = blocksPulled.get(i);
			Double distance = b.getLocation().distance(player.getEyeLocation());
			if (distance < 4 && 
				b.getLocation().getBlockY() == player.getEyeLocation().getBlockY()
				)
			{
				Location tmp = player.getEyeLocation().clone(); 
				tmp.setDirection(Tools.getDirection(player.getEyeLocation(), b.getLocation()));
				angles.put(b, tmp.getYaw());
				blocksPulled.remove(b);
				i--;
				sizeToTry--;
				blocksHeld.add(b);
				continue;
			}
			
			
			Location loc = b.getLocation().clone();
			loc.setDirection(Tools.getDirection(loc, player.getEyeLocation()));
			BlockIterator bit = new BlockIterator(loc, 0.0, 2);
			bit.next();
			Block next = bit.next();
			if ((next.getType() != Material.AIR || distance <= 4 ) && 
				b.getRelative(BlockFace.UP).getType() == Material.AIR)
			{
				next = b.getRelative(BlockFace.UP);
			}
			if (next.getType() == Material.AIR)
			{
				blocksPulled.remove(b);
				next.setType(Material.WATER);
				b.setType(Material.AIR);
				blocksPulled.add(next);
				i--;
				sizeToTry--;		
			}
		}
		
		return false;
	}


	private boolean hasBlock(Block block)
	{
		if (blocksPulled == null && blocksHeld == null)
			return false;
		
		if (blocksPulled != null && blocksPulled.contains(block))
			return true;
		if (blocksHeld != null && blocksHeld.contains(block))
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






