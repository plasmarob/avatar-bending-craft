package me.plasmarob.bending.earthbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.util.Tools;

public class EarthPush {

	public static ConcurrentHashMap<Integer, EarthPush> instances = new ConcurrentHashMap<Integer, EarthPush>();
	private Player player;
	private Block block;
	private Block next;
	private Block oldblock;
	private BlockIterator bit;
	public static int hashnum = 0;
	private int myHashnum;
	private boolean originalGone;
	
	Vector throwPath;
	List<Entity> nearEntities;
	
	public EarthPush(Player player)
	{
		if (Tools.lastKey(player) == 2)
		{
			this.player = player;
			originalGone = false;
			myHashnum = hashnum++;

			bit = new BlockIterator(player, 100);
			BlockIterator temp = bit;
			Block next;
			//boolean goyet = false;
			
			while(temp.hasNext())
			{
				next = temp.next();
				
				if (!Tools.isCrushable(next) && Tools.isEarthbendable(next))
				{
					this.block = next;
					oldblock = this.block;
					new FallingReaction(block.getRelative(0,1,0));
					//player.getWorld().playSound(block.getLocation(),Sound.EXPLODE, 0.5f, 1.3f);
					break;
				}
			}
			
			throwPath = player.getEyeLocation().clone().getDirection();
			
			if (block != null)
			{
				//Bukkit.getLogger().info(Integer.toString(instances.size()));
				instances.put(myHashnum, this);
			}
		}
	}
	
	public static void progressAll() {
		for (int id : instances.keySet())
			instances.get(id).progress();
	}
	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		//Block next = null;
		//int current = 0;
		/*
		while(current != location && bit.hasNext())
		{
		    next = bit.next();
		    current++;
		}
		*/
		
		Block temp = null;

		if (next != null)
			temp = next;
		if (bit.hasNext())
		{
			next = bit.next();
			if (!Tools.isCrushable(next) || Tools.isStopper(next))
			{
				Material mat = block.getType();
				byte dat = block.getData();
				block.setType(Material.AIR);
				player.getWorld().spawnFallingBlock(block.getLocation(), mat, dat);
				player.getWorld().playSound(block.getLocation(),Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1.3f);
		
				instances.remove(myHashnum);
				return false;
			}
			next.setType(block.getType());
			nearEntities = Tools.getEntitiesAroundPoint(next.getLocation().clone().add(0.5,0,0.5), 1);
			nearEntities.remove(player);
			for (Entity entity : nearEntities) {
				entity.setVelocity(throwPath);		
			}
			block = next;
		}
		else
		{
			next.setType(Material.AIR);
			instances.remove(myHashnum);
			return false;
		}
		if (temp != null)
			temp.setType(Material.AIR);
		
		if (player.isDead() || !player.isOnline()) {
			instances.remove(myHashnum);
			return false;
		}
		if (!originalGone)
		{
			originalGone = true;
			oldblock.setType(Material.AIR);
		}
		
		
		return true;
	}
}
