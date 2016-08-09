package me.plasmarob.bending.airbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AirScooter extends AbstractBendingForm {
	public static ConcurrentHashMap<Player, AirScooter> instances = new ConcurrentHashMap<Player, AirScooter>();
	private Player player;
	ScooterEffect scooterEffect;
	List<Entity> nearEntities;
	
	boolean allowFlight;
	boolean wasFlying;
	private Pig scooterPig;
	
	final float MAX=1.2f;
	float speed = 0f;
	Vector speedDir = new Vector();
	
	public AirScooter(Player player)
	{
		this.player = player;
		allowFlight = player.getAllowFlight();
		wasFlying = player.isFlying();
		
		player.setAllowFlight(true);
		
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_OFF.val())
		{
			boolean hasroom = true;
			ArrayList<Block> four = new ArrayList<Block>();
			Block cornerblock = Tools.get2x2CornerBlock(player.getLocation().add(0,-1,0));
			four.add(cornerblock);
			four.add(cornerblock.getRelative(1, 0, 0));
			four.add(cornerblock.getRelative(1, 0, 1));
			four.add(cornerblock.getRelative(0, 0, 1));
			for (Block b : four)
			{
				if(!Tools.waterBreaks(b))
					hasroom = false;
			}
			if (hasroom)
			{
				
				instances.put(player, this);
				
				scooterEffect = new ScooterEffect(Bending.getEffectManager(), player);
				scooterEffect.start();
				 
				
				player.setAllowFlight(true);
				player.setFlying(true);
				
				scooterPig = (Pig)player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
				scooterPig.setRemoveWhenFarAway(true);
				scooterPig.setInvulnerable(true);
				scooterPig.setPassenger(player);
				
				net.minecraft.server.v1_10_R1.Entity netEntity = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPig)scooterPig).getHandle();
				//Make a silent invisible pig to ride
				NBTTagCompound tag = new NBTTagCompound();
				netEntity.e(tag); // Apply the current values
				tag.setInt("Silent", 1); // Apply silent value
				netEntity.f(tag); // Set the tag
				
				PotionEffect poe = new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0);
				((LivingEntity)scooterPig).addPotionEffect(poe);
				
				scooterPig.setPassenger(player);
				
			}
			//Tools.playAllSounds(player, 0.1f);
			
		}
		else
		{
			//if (instances.containsKey(player) && Tools.lastKey(player) == 0)
			if (instances.containsKey(player) && player.isSneaking())
			{
				instances.get(player).cancelEffect();		
				instances.get(player).stopScoot();
				player.setAllowFlight(allowFlight);
				player.setFlying(false);
				
				
				
				instances.remove(player);
			}
			
		}
	}
	
	
	
	public void stopScoot() {
		if (scooterPig != null) {
			//scooterPig.eject();
			//scooterPig.setHealth(0);
			scooterPig.remove();
			
			scooterEffect.cancel();
			player.setAllowFlight(allowFlight);
			player.setFlying(false);
			
			instances.remove(player);
		}
	}
	
	
	public void cancelEffect()
	{
		if (scooterEffect != null) 
			scooterEffect.cancel();
	}
	
	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(instances.keySet());
		return players;
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
	
	@Override
	public boolean progress() {
		
		player.setAllowFlight(true);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_BREATHE,0.1f, 0.9f);
		player.setFallDistance(0);
		if (player.isDead() || !player.isOnline() || player.getEyeLocation().getBlock().getType() != Material.AIR) {
			stopScoot();
			scooterEffect.cancel();
			player.setAllowFlight(allowFlight);
			player.setFlying(false);
			
			instances.remove(player);
			return false;
		}
		
		
		if (scooterEffect == null)
			Bukkit.getLogger().info("null");
		else if (scooterEffect.isDone())
		{
			scooterEffect.cancel();
			scooterEffect = new ScooterEffect(Bending.getEffectManager(), player);
			scooterEffect.start();
		}
		
		
		
		Location playerdir = player.getEyeLocation().clone();
		playerdir.setPitch(0);
		
		if (speedDir.equals(playerdir.getDirection().normalize())) {
			if (speed < MAX)
				speed += 0.1;
		} else {
			if (speed >= 0)
				speed -= 0.02;
		}
		speedDir = playerdir.getDirection().normalize();
		//scooterPig.setVelocity(playerdir.getDirection().multiply(speed));
		
		float highestY = 0;
		ArrayList<Block> four = new ArrayList<Block>();

		Block cornerblock = ((Pig)scooterPig).getLocation().getBlock();
		four.add(cornerblock);
		four.add(cornerblock.getRelative(1, 0, 0));
		four.add(cornerblock.getRelative(0, 0, 1));
		four.add(cornerblock.getRelative(-1, 0, 0));
		four.add(cornerblock.getRelative(0, 0, -1));
		
		four.add(cornerblock.getRelative(1, 0, 1));
		four.add(cornerblock.getRelative(-1, 0, 1));
		four.add(cornerblock.getRelative(-1, 0, -1));
		four.add(cornerblock.getRelative(1, 0, -1));
		
		for (Block b : four) {
			if(b.getType() != Material.AIR) {
				highestY = b.getY() + 1f;
				break;
			}
		}
		
		Location d = player.getEyeLocation().clone();
		if (scooterPig.isOnGround()) {
			d.setPitch(0);
			scooterPig.setVelocity(d.getDirection().normalize().multiply(speed));
		} else {
			scooterPig.setVelocity(d.getDirection().normalize().multiply(speed*0.5));
		}
			
		playerdir.setPitch(-90);
		if (highestY > scooterPig.getLocation().getY())
			scooterPig.setVelocity(playerdir.getDirection().multiply(0.7));
		
		return true;
	}
}
