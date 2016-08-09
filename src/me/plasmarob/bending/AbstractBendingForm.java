package me.plasmarob.bending;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import de.slikey.effectlib.Effect;

public abstract class AbstractBendingForm {
	
	protected Player player;
	protected Effect effect;
	protected List<Entity> nearEntities;
	
	protected BlockIterator bit;
	protected Block next;
	protected Block prev;
	
	public static ConcurrentHashMap<Player, AbstractBendingForm> instances = new ConcurrentHashMap<Player, AbstractBendingForm>();
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
	
	abstract public boolean progress();
	
	public static void removeAll() {
		if (instances.size() > 0)
		{
			List<Player> list = Collections.list(instances.keys());
			for (Player p : list)
				instances.get(p).remove();
		}
	}
	public static void removePlayer(Player player) {
		instances.get(player).remove();
	}
	
	// Don't confuse instances.remove() with BendingForm.remove()
	// This should often be overridden
	public void remove() {
		instances.remove(player);
	}
}

