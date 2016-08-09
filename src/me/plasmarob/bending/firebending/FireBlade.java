package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class FireBlade extends AbstractBendingForm {
	
	int timer = 0;
	
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
	
	public FireBlade(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.3f, 0.7f);
		}
		else if (instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_OFF.val())
			instances.remove(player);
	}

	public boolean progress() {
		timer++;
		if (timer % 4 == 0)
		{
			new FireJetEffect(Bending.getEffectManager(), player.getEyeLocation()).start();
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.1f, 0.7f);
		}
		
		bit = new BlockIterator(player, 8);
		next = bit.next();
		while (bit.hasNext())
		{
			prev = next;
			next = bit.next();
			nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
			nearEntities.remove(player);
			if (!Tools.isUnbreakable(next))
				next.breakNaturally();
			if (nearEntities.size() > 0)
			{
				for (Entity e : nearEntities)
					e.setFireTicks(40);
			}
		}
		return false;
	}
}


