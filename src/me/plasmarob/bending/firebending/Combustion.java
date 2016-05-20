package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class Combustion extends BendingForm {

	int timer = 0;
	boolean ready = false;
	boolean launched = false;
	SmokeChargeEffect seffect;
	
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
	
	public Combustion(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
		}
		else if (instances.containsKey(player))
		{
			Combustion c = ((Combustion) instances.get(player));
			if (Tools.lastKey(player) == PlayerAction.SNEAK_OFF.val())
			{
				if (!c.ready)
				{
					instances.remove(player);
				}
				else if (!c.launched)
				{
					
					c.launched = true;
					player.getWorld().playSound(player.getLocation(),Sound.FUSE,2.0f, 0.6f);
					c.effect = new CombustionEffect(Bending.getEffectManager(), player.getEyeLocation());
					c.effect.start();
					c.bit = new BlockIterator(player, 50);
					c.next = c.bit.next();
				}
			}
			else if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
				c.boom();
		}
	}
	
	
	private void boom()
	{
		if (prev != null)
		{
			player.getWorld().createExplosion(prev.getLocation().getX(),
					prev.getLocation().getY(),
					prev.getLocation().getZ(), 4.0f, true, true);
		}
		else if (next != null)
		{
			player.getWorld().createExplosion(next.getLocation().getX(),
					next.getLocation().getY(),
					next.getLocation().getZ(), 4.0f, true, true);
		}
		instances.remove(player);
		effect.cancel();
	}
	
	@Override
	public boolean progress() {
		
		if (launched)
		{
			if (bit.hasNext())
			{
				if (timer % 4 == 0)
				{
					prev = next;
					next = bit.next();
					nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
					//nearEntities.addAll(Tools.getMobsAroundPoint(next.getRelative(0,-1,0).getLocation(), 1));
					nearEntities.remove(player);
					if (nearEntities.size() > 0 || Tools.isSolid(next) || !Tools.firePasses(prev))
						boom();
				}
			}
			else
			{
				instances.remove(player);
			}
		}
		else if (ready)
		{
			seffect = new SmokeChargeEffect(Bending.getEffectManager(), player.getEyeLocation());
			seffect.start();
		}
		
		timer++;
		if (timer > 150)
		{
			ready = true;
		}
		
		return false;
	}

}
