package me.plasmarob.bending.waterbending;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

/**
 * Water manipulation move
 * * has the ability to drop a fish
 * * currently only moves a block of water about
 * * TODO: add velocity calc and enemy damage
 * 
 * @author      Robert Thorne <plasmarob@gmail.com>
 * @version     0.3                
 * @since       2014-10-04
 */
//TODO: It needs to do minor damage and pop on an entity
@SuppressWarnings("deprecation") // Water uses Byte
public class WaterBlob extends BendingForm {

	private Block block = null;
	int distance = 0;
	private static final byte full = 0x2;
	Material mat;
	Location loc;
	Location where;
	
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
	
	public WaterBlob(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player))
		{
			instances.put(player, this);
			bit = new BlockIterator(player, 5);
			Block next;
			while(bit.hasNext())
			{
				next = bit.next();
				distance++;
				if (next.getType() != Material.AIR)
				{
					if (Tools.isOfWater(next))
						block = next;
					if (block == null)
					{
						instances.remove(player);
						return;
					}
					if (block != null)
						loc = block.getLocation().clone();
					break;
				}
			}
			if (distance > 5)
				distance = 5;
			if (block == null)
				instances.remove(player);
		}
	}

	@SuppressWarnings("unused")
	public boolean progress() {
		if (player.isDead() || !player.isOnline() || block == null) {
			if (block == null)
			{
				if (loc == null)
				{
					instances.remove(player);
					return false;
				}
				String temp = loc.toString();
				block = loc.getBlock();
				block.setType(Material.WATER);
				block.setData(full);
			}
			instances.remove(player);
			return false;
		}
				
		bit = new BlockIterator(player, distance);
		next = null;
		while(bit.hasNext())
			next = bit.next();
		if (next.getType() == Material.AIR)
		{	
			if (block != null)
			{
				mat = block.getType();
				mat = Material.STATIONARY_WATER;
			}
			if (next != null)
			{
				block.setType(Material.AIR);
				block = next;
				block.setType(mat); 
				if (block != null)
					loc = block.getLocation().clone();
			}
			player.getWorld().playSound(block.getLocation(),Sound.SWIM,0.2f, 1.5f);
		}	
		else
		{
			loc = block.getLocation().clone();
		}
		
		if (!player.isSneaking()) {
			block.setType(Material.WATER);
			block.setData(full);
			player.getWorld().playSound(block.getLocation(),Sound.SPLASH2, 0.3f, 2f);
			
			// 1 in 10 fish drop
			Random rand = new Random();
			int fish = rand.nextInt(20);
			if (fish == 0)
				player.getWorld().dropItem(block.getLocation(), new ItemStack(Material.RAW_FISH, 1));		
			instances.remove(player);
			return false;
		}
		return true;
	}
}
