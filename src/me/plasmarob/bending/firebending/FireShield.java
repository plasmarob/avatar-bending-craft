package me.plasmarob.bending.firebending;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class FireShield extends AbstractBendingForm {
	private Player player;
	
	Random rand = new Random();
	private ArrayList<FallingBlock> flames = new ArrayList<FallingBlock>();
	private ArrayList<Integer> flameTimer = new ArrayList<Integer>();
	int flameTime = 0;
	Location tempLoc;
	
	public static ConcurrentHashMap<Player, AbstractBendingForm> instances = new ConcurrentHashMap<Player, AbstractBendingForm>();
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
	public FireShield(Player player)
	{
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
			this.player = player;
			Location playerLoc = player.getEyeLocation().clone();
			playerLoc.setPitch(0);
			Location tempLoc;
			for (int i = 0; i < 100; i++)
			{
				tempLoc = player.getEyeLocation().clone();
				tempLoc.setPitch( (rand.nextInt() % 45) - 35);
				tempLoc.setYaw( (rand.nextInt() % 360) - 360 );
				Vector tempVec = tempLoc.getDirection().normalize().multiply(5);	
				FallingBlock fb = player.getWorld().spawnFallingBlock(
						tempLoc.add(tempVec), Material.FIRE, (byte)0 );
				flameTimer.add(0);
				fb.setDropItem(false);
				flames.add(fb);
			}
		}
		else if (instances.containsKey(player))
		{
			if(Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			{
				((FireShield)instances.get(player)).launch();
				instances.remove(player);
			}
			else
				((FireShield)instances.get(player)).end();
				
		}
	}
	
	public void launch()
	{
		bit = new BlockIterator(player, 5);
		while (bit.hasNext())
			next = bit.next();
		next = next.getRelative(BlockFace.UP);
		next = next.getRelative(BlockFace.UP);
		for(FallingBlock fb : flames)
		{	
			fb.setVelocity(
				Tools.getDirection(fb.getLocation().clone(), next.getLocation().clone())
				.normalize().multiply(1.5));
		}
	}
	
	public void end()
	{
		for(FallingBlock fb : flames)
		{
			new FireCloudEffect(Bending.getEffectManager(), fb.getLocation()).start();
			new SmokeEffect(Bending.getEffectManager(), fb.getLocation()).start();
			fb.remove();
		}
		flames.clear();
		instances.remove(player);
	}
	
	public boolean progress() {
		for(FallingBlock fb : flames)
		{	
			tempLoc = player.getEyeLocation().clone();
			double dist = Tools.getDirection(player.getEyeLocation().clone(), fb.getLocation().clone()).length();
			tempLoc.setDirection(Tools.getDirection(player.getEyeLocation().clone(), fb.getLocation().clone()));
			float yaw = tempLoc.getYaw()+90;
			yaw += (dist - 2)*10;
			
			Vector dir = Tools.getDirection(player.getEyeLocation().clone(), fb.getLocation().clone());
			tempLoc.setPitch(-1.5f);	
			nearEntities = Tools.getMobsAroundPoint(fb.getLocation(), 2);
			nearEntities.remove(player);
			if (nearEntities.size() > 0)
			{
				for (Entity e : nearEntities)
				{
					((Damageable)e).damage(4, player);
					e.setFireTicks(80);
					e.setVelocity(dir.normalize().multiply(0.5));
				}
			}
			tempLoc.setYaw(yaw);
			fb.setVelocity(tempLoc.getDirection());
		}
		
		if (!player.isSneaking())
		{
			end();
		}
		return false;
	}
}
