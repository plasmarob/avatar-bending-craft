package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

/**
 * Water push/splash move
 * 
 * * TODO: add pull (sneak toggle), so there's push and pull
 * 
 * @author      Robert Thorne <plasmarob@gmail.com>
 * @version     0.3                
 * @since       2014-10-05
 */
//TODO: add player pitch check since sneak for push v pull
public class WaterSplash {
	// HAS TO HAVE Integer - player is not the primary key (player can spawn more than 1)
	public static ConcurrentHashMap<Integer, WaterSplash> instances = new ConcurrentHashMap<Integer, WaterSplash>();
	public static int count = 0;
	private int id;
	private Player player;
	BlockIterator bit;
	private Vector vector;
	private static final byte full = 0x1;
	int remaining;
	Location loc;
	BlockIterator path;
	private ArrayList<Block> waters = new ArrayList<Block>();
	private ArrayList<BlockIterator> paths = new ArrayList<BlockIterator>();
	Block sourceBlock;
	
	int timeout = 200;
	List<Entity> nearEntities;
	static final int WIDTH = 2;
	static final int LENGTH = 7;
	static final int REACH = 20;
	
	double yaw0;
	double pitch0;
	boolean decided = false;
	
	public WaterSplash(Player player)
	{
		if(Tools.lastKey(player) != PlayerAction.SNEAK_ON.val() && 
			Tools.lastKey(player) != PlayerAction.LEFT_CLICK.val())	//sneak only
			return;
		
		
		if (player.isInsideVehicle())
		{
			if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val()
				&& player.getVehicle() instanceof Boat )
			{
				Block chosenBlock = player.getEyeLocation().getBlock();
				for (int i = 0; i < 3; i++)
				{
					if (Tools.isWater(chosenBlock.getType()))
					{
						sourceBlock = chosenBlock;
						//decided = true;
						break;
					}
					chosenBlock = chosenBlock.getRelative(BlockFace.DOWN);
				}
			}
		}
		
		count++;
		id = count;
		instances.put(id, this);
		this.player = player;
		yaw0 = player.getEyeLocation().getYaw();
		pitch0 = player.getEyeLocation().getPitch();
		
		remaining = LENGTH*2;
		
		
		bit = new BlockIterator(player, REACH);
		Block next;
		
		
		if (!player.isInsideVehicle())
			while(bit.hasNext())
			{
				next = bit.next();
				if (next.getType() == Material.STATIONARY_WATER)
				{
					sourceBlock = next;
					break;
				}
			}
		
		
		if (sourceBlock == null)
		{
			instances.remove(id);
			return;
		}
		
		// else go on with the creation if l-click
		Material playerMat = player.getLocation().getBlock().getType();
		if ( !player.isInsideVehicle() && 
				Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val() && Tools.isWater(playerMat))
		{
			// push player in the opposite direction of their eyes
			Location eyeLoc = player.getEyeLocation().clone();
			eyeLoc.setYaw(eyeLoc.getYaw() + 180);
			eyeLoc.setPitch(-eyeLoc.getPitch());
			player.setVelocity(eyeLoc.getDirection().multiply(1));
			
			// Copied code begins here
			decided = true;
			
			player.getWorld().playSound(sourceBlock.getLocation(),Sound.WATER,0.4f, 1f);
			vector = player.getEyeLocation().getDirection();
			loc = sourceBlock.getLocation();
			loc.setDirection(vector);
			
			loc.setPitch(-25);
			vector = loc.getDirection();	//direction to send enemies
			loc.setPitch(0);
			
			paths.add(new BlockIterator(loc));
			
			Location tempLoc = loc.clone();
			float yaw = tempLoc.getYaw();
			yaw += 90;
			if (yaw > 0) yaw -= 360;
			tempLoc.setYaw(yaw);
			BlockIterator bt = new BlockIterator(tempLoc);
			for (int i = 0; i < WIDTH; i++)
			{
				if (!bt.hasNext())
					break;
				Block nxt = bt.next();
				paths.add(new BlockIterator(nxt.getLocation().setDirection(loc.getDirection())));
			}
			
			tempLoc = loc.clone();
			yaw = tempLoc.getYaw();
			yaw -= 90;
			if (yaw < -360) yaw += 360;
			tempLoc.setYaw(yaw);
			bt = new BlockIterator(tempLoc);
			for (int i = 0; i < WIDTH; i++)
			{
				if (!bt.hasNext())
					break;
				Block nxt = bt.next();
				paths.add(new BlockIterator(nxt.getLocation().setDirection(loc.getDirection())));
			}
		}
		
		
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
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
	
		if (!decided)
		{
			if (pitch0 - player.getEyeLocation().getPitch() > 2 || 
				pitch0 - player.getEyeLocation().getPitch() < -2 ||
				yaw0 - player.getEyeLocation().getYaw() > 4 || 
				yaw0 - player.getEyeLocation().getYaw() < -4 ||
				player.isInsideVehicle()
				)
			{
				decided = true;
				player.getWorld().playSound(sourceBlock.getLocation(),Sound.SWIM,0.2f, 1.5f);
				player.getWorld().playSound(sourceBlock.getLocation(),Sound.WATER,0.4f, 1f);
				vector = player.getEyeLocation().getDirection();
				loc = sourceBlock.getLocation();
				loc.setDirection(vector);
				
				if (!player.isInsideVehicle())	// only forward
				{
					if(pitch0 - player.getEyeLocation().getPitch() < -2)
						loc.setYaw(loc.getYaw() + 180);
					else if (yaw0 - player.getEyeLocation().getYaw() > 4)
						loc.setYaw(loc.getYaw() - 90);
					else if (yaw0 - player.getEyeLocation().getYaw() < -4)
						loc.setYaw(loc.getYaw() + 90);
				}
				
				
				loc.setPitch(-25);
				vector = loc.getDirection();	//direction to send enemies
				loc.setPitch(0);
				
				paths.add(new BlockIterator(loc));
				
				Location tempLoc = loc.clone();
				float yaw = tempLoc.getYaw();
				yaw += 90;
				if (yaw > 0) yaw -= 360;
				tempLoc.setYaw(yaw);
				BlockIterator bt = new BlockIterator(tempLoc);
				for (int i = 0; i < WIDTH; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(loc.getDirection())));
				}
				
				tempLoc = loc.clone();
				yaw = tempLoc.getYaw();
				yaw -= 90;
				if (yaw < -360) yaw += 360;
				tempLoc.setYaw(yaw);
				bt = new BlockIterator(tempLoc);
				for (int i = 0; i < WIDTH; i++)
				{
					if (!bt.hasNext())
						break;
					Block nxt = bt.next();
					paths.add(new BlockIterator(nxt.getLocation().setDirection(loc.getDirection())));
				}
			}
			
			
			
		}
		else // run wave
		{
			remaining--;
			timeout--;

			boolean cont = true;
			//speed up held water
			for (Block b : waters)
			{
				if (b.getData() >= 1 && b.getData() < 7)
					b.setData((byte)(b.getData()+1));
				if (b.getType() == Material.WATER)
					cont = true;
			}
			if (!cont || timeout < 0)
			{
				instances.remove(id);
				return true;
			}
			
			if (remaining % 2 == 0)
				return true;
			
			boolean land = false;
			for (BlockIterator blit : paths)
			{
				if(blit.hasNext() && remaining > 0)
				{
					Block bk = blit.next();	
					Block above = bk.getLocation().add(0,1,0).getBlock();
					if (above.getType() == Material.AIR)
					{
						above.setType(Material.WATER);
						above.setData(full);
						waters.add(above);
						
						nearEntities = Tools.getEntitiesAroundPoint(above.getLocation(), 1.5);
						nearEntities.remove(player);
						for (Entity entity : nearEntities) {
							entity.setVelocity(vector.normalize().multiply(0.5));
						}
					}
					if (bk.getType() != Material.WATER && bk.getType() != Material.STATIONARY_WATER && bk.getType() != Material.ICE)
						land = true;	//don't go as far onto land. (halved)
				}		
			}
			if (land) remaining-=2;
		}
		
		return true;
	}
}
