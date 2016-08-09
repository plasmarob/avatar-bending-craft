package me.plasmarob.bending.earthbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.util.Tools;

public class SandTarget {

	public static ConcurrentHashMap<Player, SandTarget> instances = new ConcurrentHashMap<Player, SandTarget>();
	@SuppressWarnings("unused")
	private Player player;
	BlockIterator bit;
	Block next;
	
	List<Entity> nearEntities;
	
	public SandTarget(Player player)
	{
		if (!instances.containsKey(player))
		{
			instances.put(player, this);
			this.player = player;
			bit = new BlockIterator(player, 10);
			if (Tools.lastKey(player) == 2 && player.isSneaking())
			{
				while(bit.hasNext())
				{
					next = bit.next();
					nearEntities = Tools.getEntitiesAroundPoint(next.getRelative(0,-1,0).getLocation().clone(), 1);
					nearEntities.addAll( Tools.getEntitiesAroundPoint(next.getLocation().clone(), 1) );
					nearEntities.remove(player);
					for (Entity entity : nearEntities) 
					{
						if (!(entity instanceof LivingEntity))
							continue;
						//Tools.say(player, entity.toString());
						Block blk = entity.getLocation().getBlock();

						blk = blk.getRelative(0,-1,0);
						
						if (Tools.isEarthbendable(blk.getRelative(1,0,-1))) blk.getRelative(1,0,-1).setType(Material.SAND);
						if (Tools.isEarthbendable(blk.getRelative(1,0,0))) blk.getRelative(1,0,0).setType(Material.SAND);
						if (Tools.isEarthbendable(blk.getRelative(1,0,1))) blk.getRelative(1,0,1).setType(Material.SAND);
						
						if (Tools.isEarthbendable(blk.getRelative(0,0,-1))) blk.getRelative(0,0,-1).setType(Material.SAND);
						if (Tools.isEarthbendable(blk)) blk.setType(Material.SAND);
						if (Tools.isEarthbendable(blk.getRelative(0,0,1))) blk.getRelative(0,0,1).setType(Material.SAND);
						
						if (Tools.isEarthbendable(blk.getRelative(-1,0,-1))) blk.getRelative(-1,0,-1).setType(Material.SAND);
						if (Tools.isEarthbendable(blk.getRelative(-1,0,0))) blk.getRelative(-1,0,0).setType(Material.SAND);
						if (Tools.isEarthbendable(blk.getRelative(-1,0,1))) blk.getRelative(-1,0,1).setType(Material.SAND);
						
						
						if (Tools.isEarthbendable(blk.getRelative(1,-1,-1))) blk.getRelative(1,-1,-1).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(1,-1,0))) blk.getRelative(1,-1,0).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(1,-1,1))) blk.getRelative(1,-1,1).setType(Material.AIR);
						
						if (Tools.isEarthbendable(blk.getRelative(0,-1,-1))) blk.getRelative(0,-1,-1).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(0,-1,0))) blk.getRelative(0,-1,0).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(0,-1,1))) blk.getRelative(0,-1,1).setType(Material.AIR);
						
						if (Tools.isEarthbendable(blk.getRelative(-1,-1,-1))) blk.getRelative(-1,-1,-1).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(-1,-1,0))) blk.getRelative(-1,-1,0).setType(Material.AIR);
						if (Tools.isEarthbendable(blk.getRelative(-1,-1,1))) blk.getRelative(-1,-1,1).setType(Material.AIR);
						
					}
				}
			}
			instances.remove(player);
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
		
		
		return false;
	}
}
