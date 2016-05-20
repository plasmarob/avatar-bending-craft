package me.plasmarob.bending.airbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.Tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AirScooter {
	public static ConcurrentHashMap<Player, AirScooter> instances = new ConcurrentHashMap<Player, AirScooter>();
	private Player player;
	ScooterEffect scooter;
	List<Entity> nearEntities;
	
	boolean allowFlight;
	boolean wasFlying;
	
	public AirScooter(Player player)
	{
		this.player = player;
		allowFlight = player.getAllowFlight();
		wasFlying = player.isFlying();
		
		if (!instances.containsKey(player) && Tools.lastKey(player) == 1)
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
				scooter = new ScooterEffect(Bending.getEffectManager(), player);
				scooter.start();

				player.setAllowFlight(true);
				player.setFlying(true);
			}
			//Tools.playAllSounds(player, 0.1f);
			
		}
		else
		{
			if (instances.containsKey(player) && Tools.lastKey(player) == 0)
			{
				instances.get(player).cancelEffect();		
				instances.remove(player);
			}
			player.setAllowFlight(allowFlight);
			player.setFlying(wasFlying);
		}
	}
	
	
	
	public void cancelEffect()
	{
		if (scooter != null) 
			scooter.cancel();
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
	public boolean progress() {
		player.getWorld().playSound(player.getLocation(), Sound.HORSE_BREATHE,0.1f, 0.9f);
		player.setFallDistance(0);
		if (player.isDead() || !player.isOnline()) {
			
			instances.remove(player);
			scooter.cancel();
			player.setAllowFlight(allowFlight);
			player.setFlying(wasFlying);
			return false;
			
		}
		if (scooter == null)
			Bukkit.getLogger().info("null");
		else if (scooter.isDone())
		{
			scooter.cancel();
			scooter = new ScooterEffect(Bending.getEffectManager(), player);
			scooter.start();
		}
		
		Location playerdir = player.getEyeLocation().clone();
		playerdir.setPitch(0);
		player.setVelocity(playerdir.getDirection().multiply(0.5));
		
		int highestY = 0;
		ArrayList<Block> four = new ArrayList<Block>();
		Block cornerblock = Tools.get2x2CornerBlock(player.getLocation());
		four.add(cornerblock);
		four.add(cornerblock.getRelative(1, 0, 0));
		four.add(cornerblock.getRelative(1, 0, 1));
		four.add(cornerblock.getRelative(0, 0, 1));
		for (Block b : four)
		{
			if(b.getType() != Material.AIR)
				highestY = b.getY() + 1;
		}
		playerdir.setPitch(-90);
		if (highestY > player.getLocation().getY())
			player.setVelocity(playerdir.getDirection().multiply(0.5));
		
			
		
	
		return true;
	}
}
