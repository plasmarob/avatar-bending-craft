package me.plasmarob.bending.firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireBeam extends BendingForm{
	
	int actionNum;
	//private FireEmitEffect emitEffect;
	//private int knockback = 0;
	Vector knockbackDir;
	//BlockIterator bit;
	
	int charge = 0;
	int range;
	boolean activated = false;
	int time = 0;
	
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
	
	public FireBeam(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player))
		{
			if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			{
				bit = new BlockIterator(player, 3);
				next = bit.next();
				next = bit.next();
				next = bit.next();
				FireCloudEffect waveCloudEffect = new FireCloudEffect(Bending.getEffectManager(), next.getLocation(), 100);
				waveCloudEffect.start();
				knockbackDir = player.getEyeLocation().getDirection();
				player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.4f);
			}
			else
				instances.put(player, this);
		}
	}
	
	@Override
	public boolean progress() {
		
		if (!activated)
		{
			if (player.isSneaking())
			{
				if (charge < 100) charge++;
			}
			else
				if (charge > 0) charge--;
			
			if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			{
				activated = true;
				range = 2*((int)(charge / 25))+2;
				bit = new BlockIterator(player, range);
				
			}
		}
		
		if (activated)
		{
			if (time++ % 2 == 1)	//only every other tick
			{
				charge -= 5;
				if (bit.hasNext())
					next = bit.next();
				
				if (!Tools.firePasses(next))
				{
					instances.remove(player);
					return false;
				}
				
				FireCloudEffect waveCloudEffect = new FireCloudEffect(Bending.getEffectManager(), next.getLocation());
				waveCloudEffect.start();
				
				
				nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
				nearEntities.remove(player);
				
				if (nearEntities.size() > 0)
				{
					for (Entity e : nearEntities)
					{
						e.setFireTicks(20);
						//e.setVelocity(e.getVelocity().add(knockbackDir.normalize().multiply(0.3*knockback)));
					}	
				}
			}
		}
		
		player.sendMessage(Integer.toString(charge));
		if (charge <= 0)
			instances.remove(player);
		
		return false;
	}

}
