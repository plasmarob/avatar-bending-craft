package me.plasmarob.bending.firebending;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireBlast extends BendingForm {

	int timer = 0;
	static Random rnd = new Random();
	
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
	
	public FireBlast(Player player)
	{
		this.player = player;
		if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean progress() {
		timer++;
		if (timer % 5 == 0)
		{
			Location loc = player.getEyeLocation().clone();
			FallingBlock fb = player.getWorld().spawnFallingBlock(loc.clone(), Material.FIRE, (byte)0 );
			loc.setPitch((float) (loc.getPitch() + (rnd.nextFloat()-0.5)*30));
			loc.setYaw((float) (loc.getYaw() + (rnd.nextFloat()-0.5)*40));
			fb.setVelocity(loc.getDirection().clone());
		}
		
		if (!player.isSneaking())
			instances.remove(player);
		return false;
	}

}
