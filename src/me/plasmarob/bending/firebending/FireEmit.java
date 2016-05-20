package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;






import org.bukkit.util.Vector;

import me.plasmarob.bending.Bending;
//import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireEmit extends BendingForm {

	int actionNum;
	private FireEmitEffect emitEffect;
	Vector knockbackDir;
	
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
	
	public FireEmit(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player))
		{
			if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			{
				actionNum = PlayerAction.LEFT_CLICK.val();
				instances.put(player, this);
				
				emitEffect = new FireEmitEffect(Bending.getEffectManager(), player.getEyeLocation(), 1);
				emitEffect.start();

				player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, (float)(Math.random()*0.4 + 0.5));
				bit = new BlockIterator(player, 3);
				next = bit.next();
				
				Location tempLoc = player.getEyeLocation().clone();
				tempLoc.setPitch(-10);
				knockbackDir = tempLoc.getDirection().normalize().multiply(0.2);
			}
			else if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
			{
				actionNum = PlayerAction.SNEAK_ON.val();
				instances.put(player, this);
				
				emitEffect = new FireEmitEffect(Bending.getEffectManager(), player.getEyeLocation(), 2);
				emitEffect.start();

				player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, (float)(Math.random()*0.4 + 0.5));
				Location where = player.getLocation();
				where.setDirection(player.getEyeLocation().getDirection());
				where.setPitch(0);
				
				Location tempLoc = player.getEyeLocation().clone();
				tempLoc.setPitch(-40);
				knockbackDir = tempLoc.getDirection().normalize().multiply(0.5);
				
				bit = new BlockIterator(where, 0, 2);
				next = bit.next();
			}
		}
	}
	
	@Override
	public boolean progress() {

		if (bit.hasNext())
		{
			prev = next;
			next = bit.next();
			nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
			nearEntities.remove(player);
			
			if (nearEntities.size() > 0)
			{
				for (Entity e : nearEntities)
				{
					e.setFireTicks(20);
					e.setVelocity(e.getVelocity().add(knockbackDir));
					// strange bug where the entity freezes in midair (pig)
				}	
			}
		}
		else
			instances.remove(player);
		return false;
	}
}
