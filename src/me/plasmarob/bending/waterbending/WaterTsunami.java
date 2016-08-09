package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class WaterTsunami extends AbstractBendingForm {

	int timer = 0;
	public static ConcurrentHashMap<Player, AbstractBendingForm> instances = new ConcurrentHashMap<Player, AbstractBendingForm>();
	int maxHeight = 1;
	int maxWidth = 1;
	int waterCount;
	static List<BlockFace> faces;
	List<Block> baseBlocks = new ArrayList<Block>();
	List<Block> waterBlocks = new ArrayList<Block>();
	List<Block> blockLine = new ArrayList<Block>();
	HashMap<Block, Integer> heights = new HashMap<Block, Integer>();
	Location center;
	int currentHeight = 0;
	Block tmp;
	int step = 1;
	int waveStage = 0;
	int baseY;
	int heightBoost = 0;
	List<Entity> pushedEntities;
	Vector pushVector;
	
	public WaterTsunami(Player player)
	{
		if (faces == null)
		{
			faces = new LinkedList<BlockFace>();
			faces.add(BlockFace.UP);
			faces.add(BlockFace.DOWN);
			faces.add(BlockFace.NORTH);
			faces.add(BlockFace.SOUTH);
			faces.add(BlockFace.EAST);
			faces.add(BlockFace.WEST);
		}
		
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val() )
		{
			boolean found = false;
			bit = new BlockIterator(player, 20);
			while (bit.hasNext())
			{
				next = bit.next();
				if (Tools.isWater(next.getType()))
				{	
					baseY = next.getY();
					found = true;
					break;
				}
			}
			
			if (found)
			{
				// caculate max height based on found water
				HashSet<Block> blocks = new HashSet<Block>();
				blocks.add(next);
				HashSet<Block> tmpSet = new HashSet<Block>();
				Block t;
				for (int i = 0; i < 10; i++)
				{
					for (Block b : blocks)
					{
						for (BlockFace bf : faces)
						{
							t = b.getRelative(bf);
							if (Tools.isWater(t.getType()))
								tmpSet.add(t);
						}
					}
					blocks.addAll(tmpSet);
				}
				waterCount = blocks.size();
				blocks.clear();
				if (waterCount >= 5)
					maxHeight = 2;
				while (waterCount > 10)
				{
					waterCount = (int)waterCount/2; // (solve 10*2^x)
					maxHeight++;
				}
				maxWidth = (int)Math.ceil(maxHeight/2.0);
				// End height calculation
				
				// get source blocks horizontally, perpendicular to player
				center = next.getLocation().clone();
				Location eyeDir = player.getEyeLocation().clone();
				eyeDir.setPitch(0);
				center.setDirection(eyeDir.getDirection().clone());
				pushVector = center.getDirection().clone();
				pushVector.setY(0.7);
				pushVector.normalize().multiply(1.2); 
				center.setYaw(center.getYaw() + 90);
				Location left = center.clone();
				center.setYaw(center.getYaw() - 180);
				Location right = center.clone();
				BlockIterator lbit = new BlockIterator(left, 0.0, maxWidth);
				BlockIterator rbit = new BlockIterator(right, 0.0, maxWidth);
				center.setYaw(center.getYaw() + 90); //recenter "center"
				
				while (lbit.hasNext())
				{
					next = lbit.next();
					if (Tools.isWater(next.getType()))
					{
						baseBlocks.add(next);
						heights.put(next, maxHeight);
					}
				}
				while (rbit.hasNext())
				{
					next = rbit.next();
					if (Tools.isWater(next.getType()))
					{
						baseBlocks.add(next);
						heights.put(next, maxHeight);
					}
				}
				
				blockLine.addAll(baseBlocks);
				
				this.player = player;
				instances.put(player, this);
			}
		}
		else if (instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			((WaterTsunami)instances.get(player)).launch();
		}
	}
	
	protected void launch()
	{
		step = 2;
		nearEntities = Tools.getEntitiesAroundPoint(center, 20);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean progress() {
		
		//Tools.say(player, "progress");
		
		if (!player.isSneaking())
		{
			if (step == 1)
				step = -1;
		}
		
		timer++;
		if ( (timer % 3 != 0 && (step != 2)) || (timer % 2 != 0 && (step == 2)) )
			return false;
		
		
		if (step == 1)
		{
			if (currentHeight < maxHeight)
			{
				currentHeight++;
				for (int i = 0; i < blockLine.size(); i++)
				{
					Block b = blockLine.get(i).getRelative(BlockFace.UP);
					if (Tools.waterBreaks(b) || Tools.isWater(b.getType()))
					{
						blockLine.set(i, b); // replace the old one
						b.setType(Material.WATER);
						waterBlocks.add(b);	
					}
					else
					{
						// store away this local maxHeight
						Block t = blockLine.get(i);
						t.getRelative(0,baseY-t.getY(),0);
						heights.put(t, t.getY()-baseY);
						// remove from next loop
						blockLine.remove(i);
						i--;
					}
				}
			}
		}
		else if (step == -1)
		{
			if (currentHeight > 0)
			{
				for (int i = 0; i < baseBlocks.size(); i++)
				{
					Block b = baseBlocks.get(i).getRelative(0,currentHeight,0);
					if (Tools.isWater(b.getType()))
					{
						b.setType(Material.AIR);
						waterBlocks.remove(b);	
					}
				}
				currentHeight--;
			}
			else
				instances.remove(player);
		}
		else if (step == 2)
		{
			waveStage++;
			maxHeight++;
			if (waveStage < 10)
			{
				for (Block b : waterBlocks)
					b.setType(Material.AIR);
			}
			else
				for (Block b : waterBlocks)
				{
					b.setData((byte)7);
				}
			waterBlocks.clear();
			center.setPitch(-90);
			
			switch (waveStage)
			{
				case 1:
					waveStep2(-90, -70);
					break;
				case 2:
					waveStep3(-90,-70,-45);
					break;
				case 3:
					waveStep3(-90,-60,-30);
					break;
				case 4:
					waveStep2(-60,-30);
					break;
				case 5:
					waveStep2(-45,-15);
					break;
				case 6:
					waveStep2(-30,0);
					break;
				case 7:
					waveStep2(-15,0);
					break;
				case 8:
					waveStep2(-10,0);
					break;
				case 9:
					waveStep2(-5,0);
					break;
				case 10:
				{
					for (Block b : waterBlocks)
					{
						//b.setType(Material.AIR);
						b.setData((byte)7);
					}
					waterBlocks.clear();
					instances.remove(player);
				}
					break;
			}
			heightBoost++;
		}
		return false;
	}
	
	
	private void waveStep2(int pitch1, int pitch2)
	{
		for (Block b : baseBlocks)
		{
			int len1 = (int)Math.floor(maxHeight/2);
			int len2 = maxHeight - len1;
			
			int localHeight = heights.get(b) + heightBoost;
			int localcurrent = 0;
			
			Location bitloc = b.getLocation();
			bitloc.setYaw(center.getYaw());
			bitloc.setPitch(pitch1);
			bit = new BlockIterator(bitloc, 0.0, len1);
			while (bit.hasNext())
			{
				if (localcurrent >= localHeight)
					break;
				localcurrent++;
				next = bit.next();
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.setType(Material.WATER);
					waterBlocks.add(next);	
				}
				pushedEntities = Tools.getEntitiesAroundFromList(next.getLocation(), 1.3, nearEntities);
				for (Entity e : pushedEntities)
					e.setVelocity(pushVector);
			}
			bitloc = next.getLocation(); 
			bitloc.setYaw(center.getYaw());
			bitloc.setPitch(pitch2);
			bit = new BlockIterator(bitloc, 0.0, len2);
			while (bit.hasNext())
			{
				if (localcurrent >= localHeight)
					break;
				localcurrent++;
				next = bit.next();
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.setType(Material.WATER);
					waterBlocks.add(next);	
				}
				pushedEntities = Tools.getEntitiesAroundFromList(next.getLocation(), 1.3, nearEntities);
				for (Entity e : pushedEntities)
					e.setVelocity(pushVector);
			}
		}
	}
	
	private void waveStep3(int pitch1, int pitch2, int pitch3)
	{
		for (Block b : baseBlocks)
		{
			int len1 = (int)Math.floor(maxHeight/3);
			int len2 = (int)Math.floor(maxHeight/3);
			int len3 = maxHeight - 2*len2;
			
			int localHeight = heights.get(b) + heightBoost++;
			int localcurrent = 0;
			
			Location bitloc = b.getLocation();
			bitloc.setYaw(center.getYaw());
			bitloc.setPitch(pitch1);
			bit = new BlockIterator(bitloc, 0.0, len1);
			while (bit.hasNext())
			{
				if (localcurrent >= localHeight)
					break;
				localcurrent++;
				next = bit.next();
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.setType(Material.WATER);
					waterBlocks.add(next);	
				}
				pushedEntities = Tools.getEntitiesAroundFromList(next.getLocation(), 1.3, nearEntities);
				for (Entity e : pushedEntities)
					e.setVelocity(pushVector);
			}
			bitloc = next.getLocation(); 
			bitloc.setYaw(center.getYaw());
			bitloc.setPitch(pitch2);
			bit = new BlockIterator(bitloc, 0.0, len2);
			while (bit.hasNext())
			{
				if (localcurrent >= localHeight)
					break;
				localcurrent++;
				next = bit.next();
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.setType(Material.WATER);
					waterBlocks.add(next);	
				}
				pushedEntities = Tools.getEntitiesAroundFromList(next.getLocation(), 1.3, nearEntities);
				for (Entity e : pushedEntities)
					e.setVelocity(pushVector);
			}
			bitloc = next.getLocation(); 
			bitloc.setYaw(center.getYaw());
			bitloc.setPitch(pitch3);
			bit = new BlockIterator(bitloc, 0.0, len3);
			while (bit.hasNext())
			{
				if (localcurrent >= localHeight)
					break;
				localcurrent++;
				next = bit.next();
				if (Tools.waterBreaks(next) || Tools.isWater(next.getType()))
				{
					next.setType(Material.WATER);
					waterBlocks.add(next);	
				}
				pushedEntities = Tools.getEntitiesAroundFromList(next.getLocation(), 1.3, nearEntities);
				for (Entity e : pushedEntities)
					e.setVelocity(pushVector);
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

	public boolean hasBlock(Block block) {
		return waterBlocks.contains(block);
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (((WaterTsunami)instances.get(p)).hasBlock(block))
				return true;
		}
		return false;
	}
}
