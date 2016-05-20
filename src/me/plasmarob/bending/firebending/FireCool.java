package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireCool extends BendingForm {

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
	
	public FireCool(Player player)
	{
		this.player = player;
		if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
		}
	}
	
	@Override
	public boolean progress() {
		
		bit = new BlockIterator(player, 15);
		while (bit.hasNext())
		{
			next = bit.next();
			if (next.getType() == Material.LAVA 
					|| next.getType() == Material.STATIONARY_LAVA)
				next.setType(Material.OBSIDIAN);
			if (next.getType() == Material.FIRE)
				next.setType(Material.AIR);
		}
		
		if (!player.isSneaking())
			instances.remove(player);
		return false;
	}

}
