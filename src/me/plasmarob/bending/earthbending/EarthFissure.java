package me.plasmarob.bending.earthbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;


public class EarthFissure {

	public static ConcurrentHashMap<Player, EarthFissure> instances = new ConcurrentHashMap<Player, EarthFissure>();
	public static int count = 0;
	//private int id;
	
	private Player player;
	private BlockIterator bit;
	Block next;

	Vector throwPath;
	List<Entity> nearEntities;
	Vector direction;
	Location playerLoc;
	
	public EarthFissure(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == 2)
		{
			
			this.player = player;
			playerLoc = player.getLocation().clone();
			playerLoc.add(0,-1,0);	// the block you're standing on
			playerLoc.setPitch(0);
			Location tempLoc = playerLoc.clone();
			tempLoc.setPitch(-45);
			throwPath = tempLoc.getDirection();
			bit = new BlockIterator(playerLoc, 0.0, 10);
			if (bit.hasNext())
			{
				next = bit.next();
				if (Tools.isEarthbendable(next));
					instances.put(player, this);
			}
		}
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
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		if (bit.hasNext())
		{
			next = bit.next();
			// look up or down for next earthbendable block
			if (Tools.isEarthbendable(next.getRelative(BlockFace.UP)))
				next = next.getRelative(BlockFace.UP);
			else if (!Tools.isEarthbendable(next))
				next = next.getRelative(BlockFace.DOWN);
			
			
			if (!player.isSneaking())
			{
				if (Tools.isEarthbendable(next) && Tools.isCrushable(next.getRelative(0,1,0)))
				{
					if (next.getType() == Material.GRASS)
						next.setType(Material.DIRT);
					next.getRelative(0,1,0).setType(next.getType());
					next.getRelative(0,1,0).setData(next.getData());
					next.setType(Material.AIR);
					new FallingReaction(next.getRelative(0,1,0));
					nearEntities = Tools.getMobsAroundPoint(next.getRelative(0,1,0).getLocation().clone().add(0.5,0,0.5), 1);
					nearEntities.remove(player);
					for (Entity entity : nearEntities) {
						entity.setVelocity(throwPath);
						((Damageable)entity).damage(2, player);
					}
				}
			}
			else
			{
				if (Tools.isEarthbendable(next))
				{
					/*
					new FallingReaction(next.getRelative(0,1,0));
					
					if (Tools.isEarthbendable(next.getRelative(0,-1,0)) || Tools.isCrushable(next.getRelative(0,-1,0)))
					{
						next.getRelative(0,-1,0).setType(next.getType());
						next.getRelative(0,-1,0).setData(next.getData());
						next.setType(Material.AIR);
					}
					*/
					next.getRelative(0,-1,0).setType(Material.AIR);
					new FallingReaction(next);
				}
			}
			player.getWorld().playSound(next.getLocation(),Sound.EXPLODE, 0.1f, 0.2f);
			//player.getWorld().playSound(b.getLocation(),Sound.SILVERFISH_WALK, 1f, 0.1f);
		}
		else
		{
			instances.remove(player);
		}
		
		return false;
		
	}
}
