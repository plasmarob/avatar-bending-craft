package me.plasmarob.bending.firebending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

@SuppressWarnings({ "unused", "deprecation" })
public class FireBall {

		
	public static ConcurrentHashMap<Player, FireBall> instances = new ConcurrentHashMap<Player, FireBall>();
	private Player player;

	private BlockIterator bit;
	List<Entity> nearEntities;
	Block next;
	private int delay;
	private Location direction;
	Location lightloc;
	Block aboveBlock;
	
	double arcAcc = 0;
	double arcSpeed = 0;
	int arcHeight = 0;
	
	public FireBall(Player player)
	{
		if (Tools.lastKey(player) != PlayerAction.LEFT_CLICK.val())
			return;
		
		// how to do it with fallingblocks
		/*
		Location tempLoc = player.getEyeLocation().clone();
		
		FallingBlock fb = player.getWorld().spawnFallingBlock(
				player.getEyeLocation(), Material.FIRE, (byte)0 );
		fb.setDropItem(false);
		*/
		if (!instances.containsKey(player))
		{
			instances.put(player, this);
			this.player = player;
			//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.8f);
			//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 1.4f);
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.1f, 0.1f);
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.1f, 0.2f);
			
			direction = player.getEyeLocation().clone();
			direction.setDirection(player.getEyeLocation().getDirection());
			
			bit = new BlockIterator(direction.clone(), 0.0, 50);
			for (int i = 0; i < 6; i++)
			{
				next = bit.next();
			}
			
			delay = 6000;

			aboveBlock = next;
			//Block aboveBlock = player.getLocation().add(0,4,0).getBlock();
			aboveBlock.setType(Material.FENCE);
			
			aboveBlock.getRelative(1, 0, 0).setType(Material.FIRE);
			aboveBlock.getRelative(-1, 0, 0).setType(Material.FIRE);
			aboveBlock.getRelative(0, 1, 0).setType(Material.FIRE);
			//aboveBlock.getRelative(0, -1, 0).setType(Material.FIRE);
			aboveBlock.getRelative(0, 0, 1).setType(Material.FIRE);
			aboveBlock.getRelative(0, 0, -1).setType(Material.FIRE);
			
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
	
	public boolean progress() {
		delay--;
		
		arcAcc += 0.03;
		arcSpeed -= arcAcc;
		arcHeight = (int)arcSpeed;
		//player.sendMessage(Integer.toString(arcHeight));
		
		/*
		timer = (timer + 1) % 4;
		if (timer == 0)
		{
			bit = new BlockIterator(direction, 0.0, 4);
		}
		
		Vector dir = direction.getDirection();
				direction = next.getLocation();
				direction.setDirection(dir);
		*/
		
		//Vector old = direction.getDirection();
		//old.setY(old.getY() - 0.9f);
		//direction.setDirection(old);
		if(bit.hasNext())
		{
			next = bit.next();
			
			
			
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			if (aboveBlock.getRelative(0, -1, 0).getType() == Material.FIRE) aboveBlock.getRelative(0, -1, 0).setType(Material.AIR);
			
			aboveBlock = next.getRelative(0, arcHeight, 0);
			if (aboveBlock.getType() == Material.AIR) 
			{
				nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), 1.5);
				nearEntities.remove(player);
				for (Entity entity : nearEntities) {
					if (entity.getFireTicks() < 20) entity.setFireTicks(20);
					entity.setVelocity(direction.getDirection().multiply(1.1));
				}
				// TODO: add the isBurnable method to tools so grass is ignored.
				//Block aboveBlock = player.getLocation().add(0,4,0).getBlock();
				aboveBlock.setType(Material.FENCE);
				if (aboveBlock.getRelative(1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(1, 0, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(-1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(-1, 0, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 1, 0).getType() == Material.AIR) aboveBlock.getRelative(0, 1, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, -1, 0).getType() == Material.AIR && (aboveBlock.getRelative(0, -2, 0).getType() == Material.AIR || arcHeight < -10)) 
					aboveBlock.getRelative(0, -1, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 0, 1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, 1).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 0, -1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, -1).setType(Material.FIRE);
			}	
			else
			{
				
				//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.8f);
				player.getWorld().playSound(aboveBlock.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.1f, 0.1f);
				//Tools.playAllSounds(player, 0.1f);
				
				for (int i = arcHeight; i < 0; i++)
				{
					aboveBlock = next.getRelative(0, i, 0);
					if (aboveBlock.getType() == Material.AIR)
					{
						
						aboveBlock.setType(Material.FENCE);
						if (aboveBlock.getRelative(1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(1, 0, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(-1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(-1, 0, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 1, 0).getType() == Material.AIR) aboveBlock.getRelative(0, 1, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, -1, 0).getType() == Material.AIR && (aboveBlock.getRelative(0, -2, 0).getType() == Material.AIR || arcHeight < -10)) 
							aboveBlock.getRelative(0, -1, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 0, 1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, 1).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 0, -1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, -1).setType(Material.FIRE);
						break;
					}
				}
				if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);

				instances.remove(player);

			}
		}
		else
		{
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			instances.remove(player);
		}
		/*
		if(bit.hasNext())
		{
			next = bit.next();
			
			nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), 1.5);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				if (entity.getFireTicks() < 20) entity.setFireTicks(20);
				//entity.setVelocity(entity.getVelocity().add(direction.multiply(1.1)));
			}
			if (next.getType() != Material.AIR && next.getRelative(0,1,0).getType() == Material.AIR) 
				next.getRelative(0,1,0).setType(Material.FIRE);
			if (next.getType() == Material.ICE)
			{
				next.setType(Material.WATER);
				next.setData((byte)1);
			}
			//if (next.getType() == Material.AIR && next.getRelative(0,-1,0).getType() != Material.AIR) 
			//	next.setType(Material.FIRE);
			
		}
		*/
		
		
		if (delay <= 0)
		{
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			instances.remove(player);
			return false;
			//LightSource.createLightSource(lightloc, 0);
			//LightSource.updateChunk(lightloc);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
delay--;
		
		arcAcc += 0.03;
		arcSpeed -= arcAcc;
		arcHeight = (int)arcSpeed;
		//player.sendMessage(Integer.toString(arcHeight));
		
		/*
		timer = (timer + 1) % 4;
		if (timer == 0)
		{
			bit = new BlockIterator(direction, 0.0, 4);
		}
		
		Vector dir = direction.getDirection();
				direction = next.getLocation();
				direction.setDirection(dir);
		*/
		
		//Vector old = direction.getDirection();
		//old.setY(old.getY() - 0.9f);
		//direction.setDirection(old);
		if(bit.hasNext())
		{
			next = bit.next();
			
			
			
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			if (aboveBlock.getRelative(0, -1, 0).getType() == Material.FIRE) aboveBlock.getRelative(0, -1, 0).setType(Material.AIR);
			
			aboveBlock = next.getRelative(0, arcHeight, 0);
			if (aboveBlock.getType() == Material.AIR) 
			{
				nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), 1.5);
				nearEntities.remove(player);
				for (Entity entity : nearEntities) {
					if (entity.getFireTicks() < 20) entity.setFireTicks(20);
					entity.setVelocity(direction.getDirection().multiply(1.1));
				}
				// TODO: add the isBurnable method to tools so grass is ignored.
				//Block aboveBlock = player.getLocation().add(0,4,0).getBlock();
				aboveBlock.setType(Material.FENCE);
				if (aboveBlock.getRelative(1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(1, 0, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(-1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(-1, 0, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 1, 0).getType() == Material.AIR) aboveBlock.getRelative(0, 1, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, -1, 0).getType() == Material.AIR && (aboveBlock.getRelative(0, -2, 0).getType() == Material.AIR || arcHeight < -10)) 
					aboveBlock.getRelative(0, -1, 0).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 0, 1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, 1).setType(Material.FIRE);
				if (aboveBlock.getRelative(0, 0, -1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, -1).setType(Material.FIRE);
			}	
			else
			{
				
				//player.getWorld().playSound(player.getLocation(),Sound.GHAST_FIREBALL,0.5f, 0.8f);
				//player.getWorld().playSound(aboveBlock.getLocation(), Sound.GHAST_FIREBALL, 0.3f, 0.1f);
				player.getWorld().playSound(aboveBlock.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.1f, 0.1f);
				//Tools.playAllSounds(player, 0.1f);
				
				for (int i = arcHeight; i < 0; i++)
				{
					aboveBlock = next.getRelative(0, i, 0);
					if (aboveBlock.getType() == Material.AIR)
					{
						
						aboveBlock.setType(Material.FENCE);
						if (aboveBlock.getRelative(1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(1, 0, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(-1, 0, 0).getType() == Material.AIR) aboveBlock.getRelative(-1, 0, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 1, 0).getType() == Material.AIR) aboveBlock.getRelative(0, 1, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, -1, 0).getType() == Material.AIR && (aboveBlock.getRelative(0, -2, 0).getType() == Material.AIR || arcHeight < -10)) 
							aboveBlock.getRelative(0, -1, 0).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 0, 1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, 1).setType(Material.FIRE);
						if (aboveBlock.getRelative(0, 0, -1).getType() == Material.AIR) aboveBlock.getRelative(0, 0, -1).setType(Material.FIRE);
						break;
					}
				}
				if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);

				instances.remove(player);

			}
		}
		else
		{
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			instances.remove(player);
		}
		/*
		if(bit.hasNext())
		{
			next = bit.next();
			
			nearEntities = Tools.getEntitiesAroundPoint(next.getLocation(), 1.5);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				if (entity.getFireTicks() < 20) entity.setFireTicks(20);
				//entity.setVelocity(entity.getVelocity().add(direction.multiply(1.1)));
			}
			if (next.getType() != Material.AIR && next.getRelative(0,1,0).getType() == Material.AIR) 
				next.getRelative(0,1,0).setType(Material.FIRE);
			if (next.getType() == Material.ICE)
			{
				next.setType(Material.WATER);
				next.setData((byte)1);
			}
			//if (next.getType() == Material.AIR && next.getRelative(0,-1,0).getType() != Material.AIR) 
			//	next.setType(Material.FIRE);
			
		}
		*/
		
		
		if (delay <= 0)
		{
			if (aboveBlock.getType() == Material.FENCE) aboveBlock.setType(Material.AIR);
			instances.remove(player);
			//LightSource.createLightSource(lightloc, 0);
			//LightSource.updateChunk(lightloc);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return true;
	}
	
	
}
