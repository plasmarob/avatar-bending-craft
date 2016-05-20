package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class FireJet extends BendingForm {
	
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
	
	public FireJet(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			instances.put(player, this);
			effect = new FireJetEffect(Bending.getEffectManager(), player.getEyeLocation());
			effect.start();
			player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.7f);
			bit = new BlockIterator(player, 10);
			next = bit.next();
		}
	}

	public boolean progress() {
		
		if (bit.hasNext())
		{
			prev = next;
			next = bit.next();
			nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1);
			nearEntities.addAll(Tools.getMobsAroundPoint(next.getRelative(0,-1,0).getLocation(), 1));
			nearEntities.remove(player);
			if (nearEntities.size() > 0 || Tools.isSolid(next))
			{
				if(!Tools.firePasses(next))
				{
					prev.setType(Material.FIRE);
					instances.remove(player);
				}
			}
			if (nearEntities.size() > 0)
			{
				for (Entity e : nearEntities)
					e.setFireTicks(40);
			}
			if (next.getType() == Material.AIR)
				next.setType(Material.FIRE);
		}
		else
			instances.remove(player);
		return false;
	}
}


