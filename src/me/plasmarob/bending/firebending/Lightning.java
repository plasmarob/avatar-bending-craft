package me.plasmarob.bending.firebending;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class Lightning extends BendingForm {

	private LightningGenEffect lightningEffect;
	private Player player;
	
	public static ConcurrentHashMap<Player, BendingForm> instances = new ConcurrentHashMap<Player, BendingForm>();
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
	
	public Lightning(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
			lightningEffect = new LightningGenEffect(Bending.getEffectManager(), player);
			lightningEffect.start();
		}
		
		
		
		if (instances.containsKey(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			LightningGenEffect le = ((Lightning)instances.get(player)).lightningEffect;
			double theta = le.getAngle();
			
			if (theta < (Math.PI/8.0) || theta > (Math.PI*3))
				player.getWorld().strikeLightning(player.getLocation());
			else
			{
				double angle = theta;
				while (angle > Math.PI)
					angle -= Math.PI;
				if (angle < (Math.PI/8.0) || Math.PI - angle < (Math.PI/8.0))
				{
					bit = new BlockIterator(player,40);
					while (bit.hasNext())
					{
						next = bit.next();
						nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
						nearEntities.remove(player);
						if (nearEntities.size() > 0)
						{
							for (Entity e : nearEntities)
							{
								((Damageable)e).damage(10*theta, player);
								e.setFireTicks(80);
								player.getWorld().createExplosion(e.getLocation().getX(),
																e.getLocation().getY(),
																e.getLocation().getZ(), 1.0f, true, true);
								player.getWorld().playSound(e.getLocation(), Sound.AMBIENCE_THUNDER, 4f, 0.8f);
							}
							LightningEffect bolt = new LightningEffect(Bending.getEffectManager(), player, next.getLocation());
							bolt.start();
							break;
						}
						else if (next.getType() != Material.AIR)
						{
							
							if (next.getType() == Material.SAND
									|| next.getType() == Material.SANDSTONE)
							{
								HashSet<Block> blocks = new HashSet<Block>();
								blocks.add(next);
								for (int i = 0; i < 3; i++)
								{
									HashSet<Block> tmp = new HashSet<Block>();
									for (Block b : blocks)
									{
										tmp.add(b.getRelative(BlockFace.UP));
										tmp.add(b.getRelative(BlockFace.DOWN));
										tmp.add(b.getRelative(BlockFace.NORTH));
										tmp.add(b.getRelative(BlockFace.SOUTH));
										tmp.add(b.getRelative(BlockFace.EAST));
										tmp.add(b.getRelative(BlockFace.WEST));
									}
									blocks.addAll(tmp);
								}
								for (Block b : blocks)
								{
									if (b.getType() == Material.SAND || b.getType() == Material.SANDSTONE)
										b.setType(Material.GLASS);
									else
										b.breakNaturally();
								}
								player.getWorld().createExplosion(next.getLocation().getX(),
										next.getLocation().getY(),
										next.getLocation().getZ(), 0.0f, true, true);
							}
							else
								player.getWorld().createExplosion(next.getLocation().getX(),
										next.getLocation().getY(),
										next.getLocation().getZ(), 1.0f, true, true);

							LightningEffect bolt = new LightningEffect(Bending.getEffectManager(), player, next.getLocation());
							bolt.start();
							player.getWorld().playSound(next.getLocation(), Sound.AMBIENCE_THUNDER, 6f, 0.8f);
							break;
						}
					}
				}
				else
					player.getWorld().strikeLightning(player.getLocation());
				le.cancel();
				instances.remove(player);
			}
		}
		
	}
	
	@Override
	public boolean progress() {
		
		
		
		
		if (!player.isSneaking())
		{
			player.getWorld().strikeLightning(player.getLocation());
			/*
			double theta = lightningEffect.getAngle();
			
			if (theta < (Math.PI/8.0))
				player.getWorld().strikeLightning(player.getLocation());
			else
			{
				double angle = theta;
				while (angle > Math.PI)
					angle -= Math.PI;
				if (angle < (Math.PI/8.0) || Math.PI - angle < (Math.PI/8.0))
				{
					bit = new BlockIterator(player,40);
					while (bit.hasNext())
					{
						next = bit.next();
						nearEntities = Tools.getMobsAroundPoint(next.getLocation(), 2);
						nearEntities.remove(player);
						if (nearEntities.size() > 0)
						{
							for (Entity e : nearEntities)
							{
								((Damageable)e).damage(10*theta, player);
								e.setFireTicks(80);
								player.getWorld().createExplosion(e.getLocation().getX(),
																e.getLocation().getY(),
																e.getLocation().getZ(), 1.0f, true, true);
							}
							LightningEffect bolt = new LightningEffect(Bending.getEffectManager(), player, next.getLocation());
							bolt.start();
							break;
						}
						else if (next.getType() != Material.AIR)
						{
							//player.getWorld().strikeLightning(next.getLocation());
							player.getWorld().createExplosion(next.getLocation().getX(),
															next.getLocation().getY(),
															next.getLocation().getZ(), 1.0f, true, true);
							LightningEffect bolt = new LightningEffect(Bending.getEffectManager(), player, next.getLocation());
							bolt.start();
							break;
						}
					}
				}
				else
					player.getWorld().strikeLightning(player.getLocation());
				
				
			}
			*/
			lightningEffect.cancel();
			instances.remove(player);
		}
		
		return false;
	}

}
