package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class FireJuggle extends AbstractBendingForm {

	// written down on iPad 
	//TODO: find a good usage for juggling
	
	
	//private ArrayList<FallingBlock> flames = new ArrayList<FallingBlock>();
	
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
	
	public FireJuggle(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
			this.player = player;
		}
		
		if (instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			
			
			
		}
		
	}
	
	
	@Override
	public boolean progress() {
		// TODO Auto-generated method stub
		return false;
	}

}
