package me.plasmarob.bending.waterbending;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class LaunchedSnow extends AbstractBendingForm {

	public static ConcurrentHashMap<Integer, LaunchedSnow> instances = new ConcurrentHashMap<Integer, LaunchedSnow>();
	static int counter = 0;
	private int id = 0;
	
	Location location;
	BlockIterator bit;
	static final byte full = (byte)4; 
	List<Entity> nearEntities;
	Player player;
	Vector throwPath;
	FallingBlock launchedBlock;
	
	@SuppressWarnings("deprecation")
	public LaunchedSnow(Player player)
	{
		counter++;
		id = counter;
		this.player = player;
		instances.put(new Integer(id), this);
		
		bit = new BlockIterator(player, 3);
		next = bit.next();
		next = bit.next();
		throwPath = player.getEyeLocation().getDirection().clone().normalize();
		launchedBlock = player.getWorld().spawnFallingBlock(next.getLocation(), Material.SNOW_BLOCK, (byte) 0);
		launchedBlock.setDropItem(false);
		launchedBlock.setVelocity(throwPath.clone().multiply(2));
		throwPath.setY(throwPath.getY()+0.5); // make entity hit go up a little
		throwPath.normalize();
		throwPath = throwPath.multiply(0.5);
	}
	

	@Override
	public boolean progress() {	
		
		nearEntities = Tools.getEntitiesAroundPoint(launchedBlock.getLocation().clone(), 1.4);
		nearEntities.remove(player);
		for (Entity entity : nearEntities) {
			if ((entity instanceof Damageable))
			{
				entity.setVelocity(throwPath);
				((Damageable)entity).damage(2.0, player);
			}	
		}
		if (launchedBlock.isOnGround())
			instances.remove(id);

		return false;
	}
	
	public static void progressAll() {
		if (instances.size() > 0)
			for (Integer p : instances.keySet())
				instances.get(p).progress();
	}
	public static boolean progress(Integer p) {
		if (instances.get(p) != null)
			return instances.get(p).progress();
		return false;
	}
}