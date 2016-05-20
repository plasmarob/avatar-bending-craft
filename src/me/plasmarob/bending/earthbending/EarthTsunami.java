package me.plasmarob.bending.earthbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class EarthTsunami {
	
	public static ConcurrentHashMap<Player, EarthTsunami> instances = new ConcurrentHashMap<Player, EarthTsunami>();
	private Player player;
	private int phase = 0;
	Block tmp;
	private ArrayList<Block> roots = new ArrayList<Block>();
	private int rootY;
	private ArrayList<Block> sides = new ArrayList<Block>();
	private ArrayList<Block> blockList = new ArrayList<Block>();
	private ArrayList<FallingBlock> launched = new ArrayList<FallingBlock>();
	Material tmpMaterial;
	Byte tmpData;
	int delay = 0;
	// TODO: earthbendable only (is cloning water right now)
	
	@SuppressWarnings("deprecation")
	public EarthTsunami(Player player)
	{
		if (Tools.lastKey(player) == 1 && !instances.contains(player))
		{
			instances.put(player, this);
			this.player = player;
			Location tempLoc = player.getEyeLocation().clone();
			tempLoc.setPitch(0);
			
			BlockIterator bit = new BlockIterator(tempLoc, 0.0, 5);
			Block next = player.getEyeLocation().getBlock();//dummy init so it has a block
			
			while (bit.hasNext())
				next = bit.next();
			
			for (int i = 0; i < 5; i++)
			{
				if (Tools.isEarthbendable(next))
					break;
				else
					next = next.getRelative(BlockFace.DOWN);
			}
			if (!Tools.isEarthbendable(next))
				instances.remove(player);
			else
			{
				//next.setType(Material.STONE);
				roots.add(next);
				rootY = next.getY();
				
				Location turnLoc = next.getLocation();
				turnLoc.setDirection(tempLoc.getDirection());
				
				turnLoc.setYaw(tempLoc.getYaw()+90);
				BlockIterator left = new BlockIterator(turnLoc,0.0,2);
				turnLoc.setYaw(tempLoc.getYaw()-90);
				BlockIterator right = new BlockIterator(turnLoc,0.0,2);
				while (left.hasNext() && right.hasNext())
				{
					tmp = left.next();
					if (!roots.contains(tmp))
						roots.add(tmp);
					tmp = right.next();
					if (!roots.contains(tmp))
						roots.add(tmp);
				}
				
				for (Block b : roots)
				{
					//b.setType(Material.STONE);
					
					sides.add(b.getRelative(BlockFace.NORTH));
					sides.add(b.getRelative(BlockFace.SOUTH));
					sides.add(b.getRelative(BlockFace.EAST));
					sides.add(b.getRelative(BlockFace.WEST));
					b.getRelative(BlockFace.UP).setType(b.getType());
					b.getRelative(BlockFace.UP).setData(b.getData());
				}
				for (Block b : roots)
				{
					if (sides.contains(b))
						sides.remove(b);
				}
				for (Block b : sides)
				{
					b.setType(b.getRelative(BlockFace.DOWN).getType());
					b.setData(b.getRelative(BlockFace.DOWN).getData());
				}	
			}
		}
		
		if (Tools.lastKey(player) == 2 && instances.containsKey(player))
		{
			instances.get(player).launchWave();
		}
	}
	
	
	public ArrayList<FallingBlock> getFallingBlocks()
	{
		return launched;
	}
	
	public static ArrayList<FallingBlock> getAllFallingBlocks()
	{
		ArrayList<FallingBlock> afb = new ArrayList<FallingBlock>();
		if (instances.size() > 0)
			for (Player p : instances.keySet())
			{
				afb.addAll(instances.get(p).launched);
			}
		return afb;
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
	public void launchWave()
	{
		if (phase == 11)
		{
			for (int i = 0; i < blockList.size(); i++)
			{
				Block b = blockList.get(i);
				Material mat = b.getType();
				byte dat = b.getData();
				b.setType(Material.AIR);
				FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), mat, dat);
				double distance = fb.getLocation().getY() - rootY + 1;
				//double speed = 0.7 * Math.sqrt(Math.pow(arg0, arg1)distance) / 5.0 + 0.5;
				double speed = 0.1 + distance*0.1;
				fb.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(speed));
				launched.add(fb);
				blockList.remove(b);
				i--;
			}
			phase = 1000;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		delay++;
		
		if (phase < 0)
		{
			instances.remove(player);
			return false;
		}
		
		if (phase > 100)
		{
			phase--;
			if (phase < 970)
				instances.remove(player);
			return false;
		}
		
		if (player.isSneaking())
		{
			if (phase < 11) phase++;
			
			if (phase == 2 || phase == 4 || phase == 6)
			{
				blockList.addAll(roots);
				blockList.addAll(sides);
				
				for (int i = 0; i < roots.size(); i++)
				{
					tmpMaterial = roots.get(i).getType();
					tmpData = roots.get(i).getData();
					roots.set(i, roots.get(i).getRelative(BlockFace.UP));
					roots.get(i).setType(tmpMaterial);
					roots.get(i).setData(tmpData);
				}
				blockList.addAll(roots);
			}
			
			if (phase == 8)
			{
				for (int i = 0; i < roots.size() - 2; i++)
				{
					tmpMaterial = roots.get(i).getType();
					tmpData = roots.get(i).getData();
					roots.set(i, roots.get(i).getRelative(BlockFace.UP));
					roots.get(i).setType(tmpMaterial);
					roots.get(i).setData(tmpData);
					blockList.add(roots.get(i));
				}
			}
			if (phase == 4)
			{	
				for (Block b : sides)
				{
					b = b.getRelative(BlockFace.UP);
					b.setType(b.getRelative(BlockFace.DOWN).getType());
					b.setData(b.getRelative(BlockFace.DOWN).getData());
					blockList.add(b);
				}
				for (Block b : roots)
				{
					if (sides.contains(b))
						sides.remove(b);
				}
			}
			return false;
		}
		else//if !player.isSneaking()
		{
			phase--;
			
			if (phase == 1 || phase == 3 || phase == 5)
			{
				//blockList.addAll(roots);
				//blockList.addAll(sides);
				
				
				for (int i = 0; i < roots.size(); i++)
				{
					roots.get(i).setType(Material.AIR);
					blockList.remove(roots.get(i));
					roots.set(i, roots.get(i).getRelative(BlockFace.DOWN));
				}
				//blockList.addAll(roots);
			}
			if (phase == 7)
			{
				for (int i = 0; i < roots.size() - 2; i++)
				{
					roots.get(i).setType(Material.AIR);
					blockList.remove(roots.get(i));
					roots.set(i, roots.get(i).getRelative(BlockFace.DOWN));
				}
			}
			if (phase == 3)
			{	
				for (Block b : sides)
				{
					b.getRelative(BlockFace.UP).setType(Material.AIR);
					blockList.remove(b);
					//b = b.getRelative(BlockFace.DOWN);
					//b.setType(Material.AIR);
				}
			}
			return false;
		}
		
		
		
		
		/*
		
		
		if (phase == 3 && delay < 0)	//always false
		{
			for (int i = 0; i < blockList.size(); i++)
			{
				Block b = blockList.get(i);
				Material mat = b.getType();
				byte dat = b.getData();
				b.setType(Material.AIR);
				FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), mat, dat);
				double distance = fb.getLocation().getY() - rootY + 1;
				//double speed = 0.7 * Math.sqrt(Math.pow(arg0, arg1)distance) / 5.0 + 0.5;
				double speed = 0.5 + distance*0.05;
				fb.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(speed));
				launched.add(fb);
				blockList.remove(b);
				i--;
			}
			phase = 4;
			return false;
		}
			*/
		
		//instances.remove(player);
	}

	
}
