package me.plasmarob.bending.airbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class AirBlade extends AbstractBendingForm {

	private ArrayList<BlockIterator> paths = new ArrayList<BlockIterator>();
	static final int WIDTH = 4;
	Vector direction;
	Vector launch;
	AirWaveEffect waveEffect;
	int length = 24;
	List<Entity> nearEntities;
	
	public AirBlade(Player player)
	{	
		if (instances.containsKey(player) || Tools.lastKey(player) != PlayerAction.LEFT_CLICK.val() ||
				Tools.isWater(player.getEyeLocation().getBlock().getType())) {
			return; 
		}	
		instances.put(player, this);
		this.player = player;
		direction = player.getEyeLocation().getDirection();
		launch = direction.clone().normalize().multiply(3);
		Location tempLoc = player.getEyeLocation().clone();

		// effect and sound
		waveEffect = new AirWaveEffect(Bending.getEffectManager(), player.getEyeLocation(), player.isSneaking());
		waveEffect.start();
		player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERDRAGON_FLAP, 0.4f, 0.3f);
		
		// Spawn Particles
		if (player.isSneaking()) {	// vertical wave

			Location tLoc = player.getLocation().clone();
			tLoc.setDirection(direction);
			tLoc.setPitch(tLoc.getPitch()+10);
			
			float pitch = tempLoc.getPitch();
			tempLoc.setPitch(pitch + 90);
			BlockIterator bt = new BlockIterator(tempLoc);
			for (int i = 0; i < 1; i++) {
				if (!bt.hasNext())
					break;
				paths.add(new BlockIterator(bt.next().getLocation().clone().setDirection(direction)));
			}
			
			tempLoc = player.getEyeLocation().clone();
			pitch = tempLoc.getPitch();
			pitch -= 90;
			tempLoc.setPitch(pitch);
			bt = new BlockIterator(tempLoc);
			for (int i = 0; i < WIDTH + 1; i++) {
				if (!bt.hasNext())
					break;
				paths.add(new BlockIterator(bt.next().getLocation().clone().setDirection(direction)));
			}
			
			// ignore blocks under me! (first few)
			for (BlockIterator blit : paths) {
				for (int i = 0; i < 3; i++)
					if(blit.hasNext())
						blit.next();
			}
		} else {	// horizontal wave			
			//turn left and right to grab blocks in a line that will form the wave
			
			//turn left, grab blocks to iterate with
			float yaw = tempLoc.getYaw();
			yaw += 90;
			if (yaw > 0) yaw -= 360;
			tempLoc.setYaw(yaw);
			tempLoc.setPitch(0);
			BlockIterator bt = new BlockIterator(tempLoc);
			for (int i = 0; i < WIDTH; i++) {
				if (!bt.hasNext())
					break;
				Block nxt = bt.next();
				paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
			}
			//turn right, grab blocks to iterate with
			tempLoc = player.getEyeLocation().clone();
			yaw = tempLoc.getYaw();
			yaw -= 90;
			if (yaw < -360) yaw += 360;
			tempLoc.setYaw(yaw);
			tempLoc.setPitch(0);
			bt = new BlockIterator(tempLoc);
			for (int i = 0; i < WIDTH; i++) {
				if (!bt.hasNext())
					break;
				Block nxt = bt.next();
				paths.add(new BlockIterator(nxt.getLocation().setDirection(direction)));
			}	
		}
		
		Location eyeLoc = player.getEyeLocation().clone();
		eyeLoc.setYaw(eyeLoc.getYaw() + 180);
		eyeLoc.setPitch(-eyeLoc.getPitch());
		player.setVelocity(eyeLoc.getDirection().multiply(1));
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP,1f, 1f);
	}
	
	public boolean progress() {
	
		//DO TWICE FOR TWICE THE SPEED
		for (int j=0; j < 1; j++)
			if (length > 0)
			{
				length--;
				// iterate from each block in the wave
				for (int i = 0; i < paths.size(); i++) {
					BlockIterator blit = paths.get(i);
					if (!blit.hasNext()) {
						paths.remove(blit);
						i--;
						continue;
					}	
					next = blit.next();
					
					
					// TODO: consider performance boost by removing all paths hitting entity outside loop
					nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 1.5);
					if (nearEntities.contains(player))
						nearEntities.remove(player);
					
					for (Entity e : nearEntities) {
						if (e.isOnGround()) {
							e.setVelocity(launch.clone().setY(0.1));
						} else 
							e.setVelocity(launch);
						((Damageable) e).damage(2.0, player);
					}
					
					if (!Tools.isAirBlowable(next) || nearEntities.size() > 0) {
						paths.remove(blit);
						i--;
						//player.getWorld().playSound(next.getLocation(),Sound.ENTITY_ENDERDRAGON_FLAP, 0.2f, 0.1f);
						player.getWorld().playSound(next.getLocation(), Sound.ENTITY_HORSE_BREATHE,0.4f, 0.1f);
						
					}
					if (Tools.isAirBlowable(next)) {
						Collection<ItemStack> tmp = next.getDrops();
						for (ItemStack is : tmp) {
							player.getWorld().dropItem(next.getLocation(), is);
						}
						next.breakNaturally();
					}
				}
			} else {
				instances.remove(player);
			}
		return false;
	}
}
