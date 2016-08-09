package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class FireLaunch extends AbstractBendingForm {

	FireFootEffect footEffect;
	
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
