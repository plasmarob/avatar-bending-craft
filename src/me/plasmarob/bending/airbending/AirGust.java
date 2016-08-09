package me.plasmarob.bending.airbending;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class AirGust extends AbstractBendingForm {

	public GustEffect gust;
	private int delay;
	private BlockIterator bit;
	private Vector direction;
	private boolean isMissile = false;
	private int width = 2;
	
	public AirGust(Player player) {
		this.player = player;

		if (Tools.isWater(player.getEyeLocation().getBlock().getType()))
			return;
		
		if (!instances.containsKey(player) && Tools.lastKey(player) == 2 &&
				player.getEyeLocation().getBlock().getType() == Material.AIR) {
			instances.put(player, this);
			direction = player.getEyeLocation().getDirection();
			delay = 20;
			bit = new BlockIterator(player, 25);
			
			Location eyeLoc = player.getEyeLocation().clone();
			eyeLoc.setYaw(eyeLoc.getYaw() + 180);
			eyeLoc.setPitch(-eyeLoc.getPitch());
			player.setVelocity(eyeLoc.getDirection().multiply(1));
			// sneaking is missile v. gust
			isMissile = player.isSneaking();
			// the visible effect
			gust = new GustEffect(Bending.getEffectManager(), player.getEyeLocation(), isMissile);
			gust.start();
			if (!isMissile)
				width = 3;
			//player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP,1f, 1f);
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERDRAGON_FLAP, 0.2f, 0.3f);
		}
	}
	
	public static boolean playerBlown(Player p) {
		if (instances.containsKey(p))
			return true;
		return false;
	}
	
	public boolean progress() {
		for (int run = 0; run < 1; run++) {
			delay--;
			if (delay <= 0) {
				instances.remove(player);
				return true;
			}
			
			if(bit.hasNext()) {
				next = bit.next();
				if (next.getType() != Material.AIR) {
					delay = 0; //no more air;
					if (!gust.isDone())
						gust.cancel();
					instances.remove(player);
					return true;
				}
				
				nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), width);
				nearEntities.remove(player);
					
				for (Entity e : nearEntities) {
					if (isMissile) {
						if (e.isOnGround()) {
							e.setVelocity(direction.clone().normalize().multiply(2).setY(0.1));
						} else 
							e.setVelocity(direction);
						
						if (e instanceof Damageable)
							((Damageable)e).damage(2.0, player);
					} else {
						if (e.isOnGround()) {
							e.setVelocity(direction.clone().normalize().setY(0.1));
						} else 
							e.setVelocity(direction);
					}
				}
			}
		}
		return false;
	}
}
