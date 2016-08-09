package me.plasmarob.bending.earthbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.util.Tools;


/**
 * I NEED TO:
 * - Give sneak and area grab (for pushing entire pillars, walls, etc)
 * 	- condition: faces facing as selected face are exposed (to crushable)
 * 	- condition: faces facing other direction are exposed (to crushable) 
 * @author Robert
 *
 */

public class EarthLaunch {

	public static ConcurrentHashMap<Player, EarthLaunch> instances = new ConcurrentHashMap<Player, EarthLaunch>();
	private Player player;
	//private ArrayList<Block> launchables = new ArrayList<Block>();
	public static ConcurrentHashMap<Block, Integer> launchables = new ConcurrentHashMap<Block, Integer>();
	private ArrayList<FallingBlock> launched = new ArrayList<FallingBlock>();
	BlockIterator bit;
	Block next;
	List<Entity> nearEntities;
	Vector playerDirection;
	int delay = 0;
	
	public EarthLaunch(Player player)
	{
		if (!instances.containsKey(player))
		{
			if(Tools.lastKey(player) == 1)
			{
				instances.put(player, this);
				this.player = player;
			}
		}
		else
		{
			
			if (instances.get(player).getDelay() == 0) 
			{
				instances.get(player).recieve();
			}
		}
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	@SuppressWarnings("deprecation")
	public void recieve()
	{
		delay = 8;
		Block found = null;
		if(Tools.lastKey(player) == 2)
		{
			bit = new BlockIterator(player, 10);
			while(bit.hasNext())
			{
				next = bit.next();
				//Tools.say(player, launchables.size());
				
				if (Tools.isEarthbendable(next))
				{
					if (Tools.isCrushable(next.getRelative(0,1,0)))
					{
						int py = player.getEyeLocation().getBlockY();
						if (!launchables.containsKey(next))
						{
							player.getWorld().playSound(next.getLocation(),Sound.ENTITY_GENERIC_EXPLODE, 0.2f, 1.2f);
							launchables.put(next, py);
							//Tools.say(player, launchables.size());
							break;
						}
						else //if (py == next.getY())
						{
							found = next;
						}
					}
					if (!launchables.containsKey(next))
						break;
				}
			}
			
			if (found != null)
			{
				player.getWorld().playSound(found.getLocation(),Sound.ENTITY_ENDERDRAGON_FLAP, 0.3f, 0.5f);
				Material mat = found.getType();
				byte dat = found.getData();
				found.setType(Material.AIR);
				Location tempLoc = found.getLocation().clone();
				//tempLoc.setDirection(player.getEyeLocation().getDirection().normalize().multiply(3));
				FallingBlock fb = found.getWorld().spawnFallingBlock(tempLoc, mat, dat);
				fb.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(2));
				launched.add(fb);
				playerDirection = player.getEyeLocation().getDirection();
				launchables.remove(found);
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
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		if(player == null || !player.isOnline())
		{
			instances.remove(player);
			return false;
		}
			
		
		if (delay > 0)
			delay--;
		
		if(Tools.lastKey(player) == 0)
		{
			ConcurrentHashMap<Block, Integer> keptBlocks = new ConcurrentHashMap<Block, Integer>();
			for (Block b : launchables.keySet())
			{
				if (b.getRelative(BlockFace.DOWN).getType() == Material.AIR)
				{
					Material mat = b.getType();
					byte dat = b.getData();
					b.setType(Material.AIR);
					b.getWorld().spawnFallingBlock(b.getLocation(), mat, dat);
				}
				else
				{
					keptBlocks.put(b, launchables.get(b));
				}
				
			}
			launchables.clear();
			launchables.putAll(keptBlocks);
			
			if (launched.isEmpty())
				instances.remove(player);
			return false;
		}
		
		
		for (int i = 0; i < launched.size(); i++)
		{
			FallingBlock fb = launched.get(i);
			nearEntities = Tools.getEntitiesAroundPoint(fb.getLocation().clone(), 1.4);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				if ((entity instanceof Damageable))
				{
					entity.setVelocity(playerDirection.normalize());
					((Damageable)entity).damage(4.0, player);
				}	
			}
			if (fb.isOnGround())
			{
				launched.remove(fb);
				i--;
			}
		}
		
		
		//int py = player.getEyeLocation().getBlockY();
		//int inum = 0;
		for (Block b : launchables.keySet())
		{
			if (launchables.get(b) > b.getY())
			{
				if (Tools.isCrushable(b.getRelative(0,1,0)))
				{
					if (b.getType() == Material.SAND)
						// if the byte is 1, it's red sand.
						// if it's stained clay, it's orange
						if (b.getData() == 0)
							b.getRelative(0,1,0).setType(Material.SANDSTONE);
						else
							b.getRelative(0,1,0).setType(Material.STAINED_CLAY);
					else
						b.getRelative(0,1,0).setType(b.getType());
					b.getRelative(0,1,0).setData(b.getData());
					b.setType(Material.AIR);
					
					int y = launchables.get(b);
					launchables.remove(b);
					b = b.getRelative(0,1,0);
					launchables.put(b, y);
					//launchables.replace(b, inum);
				}
			}
			else if (launchables.get(b) < b.getY())
			{
				if (Tools.isCrushable(b.getRelative(0,-1,0)))
				{
					if (b.getType() == Material.SAND)
						if (b.getData() == 0)
							b.getRelative(0,1,0).setType(Material.SANDSTONE);
						else
							b.getRelative(0,1,0).setType(Material.STAINED_CLAY);
					else
						b.getRelative(0,-1,0).setType(b.getType());
					b.getRelative(0,-1,0).setData(b.getData());
					b.setType(Material.AIR);

					int y = launchables.get(b);
					launchables.remove(b);
					b = b.getRelative(0,-1,0);
					launchables.put(b, y);
					//launchables.replace(b, inum);
				}
			}
			//inum++;
		}
		
		
		
		return false;
	}
}
