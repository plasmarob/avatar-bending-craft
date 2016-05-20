package me.plasmarob.bending.airbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class AirGust {
	
	public static ConcurrentHashMap<Player, AirGust> instances = new ConcurrentHashMap<Player, AirGust>();
	private Player player;
	public GustEffect gust;
	private int delay;
	private BlockIterator bit;
	List<Entity> nearEntities;
	Block next;
	private Vector direction;
	
	private boolean isMissile = false;
	
	public AirGust(Player player)
	{
		this.player = player;

		if (!instances.containsKey(player) && Tools.lastKey(player) == 2 &&
				//player.isSneaking() &&
				player.getEyeLocation().getBlock().getType() == Material.AIR)
		{
			instances.put(player, this);
			direction = player.getEyeLocation().getDirection();
			delay = 20;
			bit = new BlockIterator(player, 12);
			
			Location eyeLoc = player.getEyeLocation().clone();
			eyeLoc.setYaw(eyeLoc.getYaw() + 180);
			eyeLoc.setPitch(-eyeLoc.getPitch());
			player.setVelocity(eyeLoc.getDirection().multiply(1));
			// sneaking is missile v. gust
			isMissile = player.isSneaking();
			// the visible effect
			gust = new GustEffect(Bending.getEffectManager(), player.getEyeLocation(), isMissile);
			gust.start();
		}
	}
	
	public static boolean playerBlown(Player p)
	{
		if (instances.containsKey(p))
			return true;
		return false;
	}
	
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
	
	public boolean progress() {
		delay--;
		if (delay <= 0)
		{
			instances.remove(player);
			return true;
		}
		
		if(bit.hasNext())
		{
			next = bit.next();
			
			if (next.getType() != Material.AIR)
			{
				delay = 0; //no more air;
				if (!gust.isDone())
					gust.cancel();
				instances.remove(player);
				return true;
			}
			
			nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), 2);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				entity.setVelocity(entity.getVelocity().add(direction.multiply(1)));
				if (isMissile)
				{
					entity.setVelocity(entity.getVelocity().add(direction.multiply(2)));
					if (entity instanceof Damageable)
						((Damageable)entity).damage(2.0, player);
				}
			}		
		}
		
		return false;
	}
}
