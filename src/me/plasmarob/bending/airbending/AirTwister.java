package me.plasmarob.bending.airbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirTwister {

	public static ConcurrentHashMap<Integer, AirTwister> instances = new ConcurrentHashMap<Integer, AirTwister>();
	private Player player;
	public static int count = 0;
	private int id;
	List<Entity> nearEntities;
	float yaw;
	float tempYaw;
	int accumulator = 0;
	boolean twisterMade = false;
	int weird = 0;
	int delay = 0;
	Location dir;
	TwisterEffect teffect;
	
	public AirTwister(Player player)
	{
		if (Tools.lastKey(player) == 2 || Tools.lastKey(player) == 0 )
			return;
		
		if (Tools.lastKey(player) == 1)
		{
			this.player = player;
			count++;
			id = count;
			instances.put(id, this);
			yaw = player.getEyeLocation().getYaw();
		}
		else
			instances.remove(id);
	}
	
	public static void progressAll() {
		if (instances.size() > 0)
			for (Integer p : instances.keySet())
				instances.get(p).progress();
	}

	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		if (!player.isSneaking() )
		{
			if (!twisterMade || teffect.isDone())
			{
				instances.remove(id);
				return false;
			}
			else if (dir == null)
			{
				dir = player.getLocation();
				teffect.setDirection(dir);
				Block b = player.getLocation().getBlock().getRelative(-2,0,-2);
				for (int i = 0; i < 5; i++)
				{
					for (int j = 0; j < 5; j++)
					{
						Block bk = b.getRelative(i,0,j);
						if (bk.getType() == Material.SAND || bk.getType() == Material.GRAVEL)
						{
							Material mat = bk.getType();
							byte dat = bk.getData();
							bk.setType(Material.AIR );
							FallingBlock fb = bk.getWorld().spawnFallingBlock(bk.getLocation(), mat, dat);
							Vector v = Tools.getDirection(player.getLocation(), bk.getLocation());
							Location temp = player.getLocation().clone();
							temp.setDirection(v);
							temp.setPitch(-75);
							
							fb.setVelocity(temp.getDirection().normalize().multiply(1));
							//b.setType(Material.STONE);
						}
						bk = b.getRelative(i,-1,j);
						if (bk.getType() == Material.SAND || bk.getType() == Material.GRAVEL)
						{
							Material mat = bk.getType();
							byte dat = bk.getData();
							bk.setType(Material.AIR);
							FallingBlock fb = bk.getWorld().spawnFallingBlock(bk.getLocation(), mat, dat);
							Vector v = Tools.getDirection(player.getLocation(), bk.getLocation());
							Location temp = player.getLocation().clone();
							temp.setDirection(v);
							temp.setPitch(-75);
							
							fb.setVelocity(temp.getDirection().normalize().multiply(1));
							//b.setType(Material.STONE);
						}
					}
				}
			}
		}
		delay++;
		if (delay % 2 != 0)
			return false;
		
		float tempYaw = yaw;
		if (!twisterMade)
		{
			yaw = player.getEyeLocation().getYaw();
			//Tools.say(player, tempYaw + " " + yaw);
			/*
			if (Math.abs(tempYaw - yaw) < .001)
			{
				weird++;
				Tools.say(weird);
			}
			*/
			
			if ((tempYaw > yaw && tempYaw - yaw > 5) || yaw - tempYaw > 300)
				accumulator++;
			else if ((yaw > tempYaw && yaw - tempYaw > 5) || tempYaw - yaw > 300)
				accumulator--;
			else
				accumulator = 0;
			
			if (Math.abs(accumulator) > 10)
			{
				twisterMade = true;
				teffect = new TwisterEffect(Bending.getEffectManager(), player.getLocation().clone(), accumulator > 0);
				teffect.start();
			}
		}
		
		
		if (teffect != null && !teffect.isDone())
		{
			Location where = teffect.location;
			//where.getBlock().setType(Material.GLASS);
			
			nearEntities = Tools.getEntitiesAroundPoint(where, 9.5);
			nearEntities.remove(player);
			
			
			for (int i = 0; i < nearEntities.size(); i++)
			{
				Entity e = nearEntities.get(i);
				if (Math.abs(e.getLocation().getX() - where.getX()) > 4 ||
					Math.abs(e.getLocation().getZ() - where.getZ()) > 4	||
						(where.getY() - e.getLocation().getY() > 2 ||
						e.getLocation().getY() - where.getY() > 10)
						)
				{
					nearEntities.remove(i);
					i--;
				}
			}
			Location loc;
			for (Entity entity : nearEntities) {
				loc = entity.getLocation();
				Vector v = Tools.getDirection(entity.getLocation(), where);
				loc.setDirection(v);
				//loc.setYaw(loc.getYaw()+120);
				loc.setPitch(-10);
				entity.setVelocity(loc.getDirection().clone().normalize().multiply(0.9));
			}
		}

		
	
		
		return false;
	}
}
