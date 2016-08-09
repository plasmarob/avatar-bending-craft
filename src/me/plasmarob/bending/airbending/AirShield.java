package me.plasmarob.bending.airbending;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirShield {

	public static ConcurrentHashMap<Player, AirShield> instances = new ConcurrentHashMap<Player, AirShield>();
	private Player player;
	ShieldEffect shield;
	List<Entity> nearEntities;
	public AirShield(Player player)
	{
		this.player = player;
		if (Tools.lastKey(player) != 2)
			return;
		if (!instances.containsKey(player))
		{
			instances.put(player, this);
			if (shield != null) 
				shield.cancel();
			shield = new ShieldEffect(Bending.getEffectManager(), player);
			shield.start();
			//flight = new Flight(player);
			//Tools.playAllSounds(player, 0.1f);
			
		}
		else
		{
			instances.get(player).getShield().cancel();
			instances.remove(player);
		}
		
		//Block b = player.getLocation().getBlock();
		//b.setTypeId(71);
		//b.getRelative(0, 1, 0).setType(Material.FIRE);
		//Packet51MapChunk packet = new Packet51MapChunk(...);
		//CraftPlayer
		//player.s.get.getHandler().netServerHandler.sendPacket(packet);
	}
	
	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		
		players.addAll(instances.keySet());
		return players;
	}
	
	
	public ShieldEffect getShield()
	{
		return shield;
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
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_BREATHE,0.1f, 0.1f);
		player.setFallDistance(0);
		if (player.isDead() || !player.isOnline() || player.isSprinting()) {
			
			instances.remove(player);
			shield.cancel();
			return false;
			
		}
		else if (shield.isDone())
		{
			shield.cancel();
			shield = new ShieldEffect(Bending.getEffectManager(), player);
			shield.start();
		}
		
		
		if (!player.isSneaking() && 
			(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR ||
			player.getLocation().getBlock().getRelative(0,-2,0).getType() != Material.AIR))
		{
			player.setVelocity(new Vector(0,0.07,0));
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		else if (player.isSneaking())
		{
			player.setVelocity(new Vector(0,-0.07,0));
			player.setFlying(false);
		}		
		else
		{
			player.setVelocity(new Vector(0,0,0));
		}

		
		Location center = player.getLocation().add(0,2,0);
		nearEntities = Tools.getEntitiesAroundPoint(center, 5);
		nearEntities.remove(player);
		for (Entity entity : nearEntities) {
			// I need to make this convert to location, set yaw, and go back again
			entity.setVelocity(Tools.getDirection(center, entity.getLocation()).normalize());			
		}
		
		return true;
	}
	
	
	
}