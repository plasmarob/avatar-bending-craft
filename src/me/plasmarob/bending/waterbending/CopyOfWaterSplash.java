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
public class CopyOfWaterSplash {
	// HAS TO HAVE Integer - player is not the primary key (player can spawn more than 1)
	public static ConcurrentHashMap<Integer, CopyOfWaterSplash> instances = new ConcurrentHashMap<Integer, CopyOfWaterSplash>();
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
	
	int timeout = 200;
	List<Entity> nearEntities;
	static final int WIDTH = 2;
	static final int LENGTH = 7;
	static final int REACH = 10;
	
	public CopyOfWaterSplash(Player player)
	{
		if(!player.isSneaking() || Tools.lastKey(player) != PlayerAction.LEFT_CLICK.val())	//shift+lclick only
			return;
		//Tools.say(player, "HERE 1");
		remaining = LENGTH*2;
		count++;
		id = count;
		instances.put(id, this);
		this.player = player;
		Block sourceBlock = null;
		
		bit = new BlockIterator(player, REACH);
		Block next;
		
		Material playerMat = player.getLocation().getBlock().getType();
		if ( playerMat == Material.WATER || playerMat == Material.STATIONARY_WATER )
		{
			// push player in the opposite direction of their eyes
			Location eyeLoc = player.getEyeLocation().clone();
			eyeLoc.setYaw(eyeLoc.getYaw() + 180);
			eyeLoc.setPitch(-eyeLoc.getPitch());
			player.setVelocity(eyeLoc.getDirection().multiply(1));
		}
		
		while(bit.hasNext())
		{
			//Tools.say(player, "HERE 2");
			next = bit.next();
			if (next.getType() == Material.STATIONARY_WATER)
			{
				sourceBlock = next;
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
				break;
			}
		}
		if (sourceBlock == null)
			instances.remove(id);
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
		//Tools.say(player, "HERE 3");
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
		
		return true;
	}
}
