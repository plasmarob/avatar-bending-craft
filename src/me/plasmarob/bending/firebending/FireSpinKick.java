package me.plasmarob.bending.firebending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class FireSpinKick extends BendingForm {
	//public static ConcurrentHashMap<Player, FireSpinKick> instances = new ConcurrentHashMap<Player, FireSpinKick>();
	public static int count = 0;
	private int delay;
	private Player player;
	private FireSpinKickEffect ringEffect;
	List<Entity> nearEntities;
	Block next;
	
	Location eyes;
	BlockIterator affectedIt;
	
	int turns;
	Location loc;
	float yaw;
	
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
	
	public FireSpinKick(Player player)
	{
		
	
		if (!instances.containsKey(player) && player.isSneaking() 
				&& Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			instances.put(player, this);
			this.player = player;
			delay = 20;
			//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.8f);
			//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 1.4f);
			
			//bit = new BlockIterator(player, 5);
	
			player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.5f);
			//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 1.4f);
			
			ringEffect = new FireSpinKickEffect(Bending.getEffectManager(), player.getLocation());
			ringEffect.start();
			
			eyes = player.getEyeLocation().clone();
			eyes.setYaw(eyes.getYaw() - 50);
			
			turns = 4;
			loc = player.getLocation();
			yaw = loc.getYaw();
			
			List<Entity> nearEntities;
			nearEntities = Tools.getEntitiesAroundPoint(loc, 4);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {

				Vector velocity = Tools.getDirection(loc, entity.getLocation()).normalize().multiply(2);
				velocity.setY(0.4f);
				if ( Math.abs( entity.getLocation().getY() - loc.getY() ) < 0.4)
				{
					entity.setVelocity(velocity);
					if (entity.getFireTicks() < 20) entity.setFireTicks(20);
				}
				
			}
		}
	}
	
	public boolean progress() {
		
		if (turns > 0)
		{
			turns--;
			yaw += 90;
			if (yaw >= 0)
				yaw -= 360;
			loc.setYaw((yaw));
			player.teleport(loc);
			//player.sendMessage(Float.toString(yaw));
		}
		
		
		delay--;
		if (delay <= 0)
			instances.remove(player);
	
		return true;
	}
	
	public static float getYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180 / Math.PI - 90);
    }
}
