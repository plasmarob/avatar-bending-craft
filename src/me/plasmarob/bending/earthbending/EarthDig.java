package me.plasmarob.bending.earthbending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class EarthDig {

	public static ConcurrentHashMap<Player, EarthDig> instances = new ConcurrentHashMap<Player, EarthDig>();
	BlockIterator bit;
	Block next;
	public EarthDig(Player player)
	{
		if (Tools.lastKey(player) == 2)
		{
			bit = new BlockIterator(player, 2);
			while (bit.hasNext())
			{
				next = bit.next();	
				if (Tools.isEarthbendable(next))
				{
					next.breakNaturally();
					break;
				}
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
	
	public boolean progress()
	{
		return false;
	}
}
