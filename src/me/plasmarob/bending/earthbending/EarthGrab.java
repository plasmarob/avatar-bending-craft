package me.plasmarob.bending.earthbending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.BendingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class EarthGrab {

	public static ConcurrentHashMap<Integer, EarthGrab> instances = new ConcurrentHashMap<Integer, EarthGrab>();
	private Player player;
	private Block block = null;
	private BlockIterator bit;
	
	public EarthGrab(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player.getEntityId()))
		{
			Bukkit.getLogger().info(Integer.toString(instances.size()));
			instances.put(player.getEntityId(), this);
			bit = new BlockIterator(player, 5);
			Block next;
			while(bit.hasNext())
			{
				next = bit.next();
				if (next.getType() != Material.AIR)
				{
					block = next;
					break;
				}
			}
		}
	}
	
	public static void launch()
	{
		
	}
	
	public static void progressAll() {
		for (int id : instances.keySet())
			instances.get(id).progress();
	}
	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}
	@SuppressWarnings("deprecation")
	public boolean progress() {
		if (player.isDead() || !player.isOnline() || block == null) {
			instances.remove(player.getEntityId());
			return false;
		}
		if (!player.isSneaking()) {
			instances.remove(player.getEntityId());
			Material mat = block.getType();
			block.setType(Material.AIR);
			block.getLocation().getWorld().spawnFallingBlock(block.getLocation(), mat, (byte) 0);
			return false;
		}
		if (BendingPlayer.getKeys(player.getUniqueId()).contains(2))
		{
			new EarthPush(player);
			instances.remove(player.getEntityId());
			return false;
		}
		
		bit = new BlockIterator(player, 5);
		Block next = null;
		while(bit.hasNext())
			next = bit.next();
	
		if (next.getType() == Material.AIR)
		{
			Material mat = block.getType();
			block.setType(Material.AIR);
			block = next;
			block.setType(mat);
		}	
		return true;
	}
}
