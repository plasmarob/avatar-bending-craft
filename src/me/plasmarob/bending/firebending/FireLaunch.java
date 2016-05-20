package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireLaunch extends BendingForm {

	FireFootEffect footEffect;
	
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
	
	public FireLaunch(Player player)
	{
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			if (!instances.containsKey(player))
				instances.put(player, this);
			else
				instances.remove(player);
			this.player = player;
		}
	}
	
	@Override
	public boolean progress() {
		
		player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(0.7));
		footEffect = new FireFootEffect(Bending.getEffectManager(), player);
		footEffect.start();

		if (!player.isSprinting()) 
			instances.remove(player);
		return false;
	}

}
