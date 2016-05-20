package me.plasmarob.bending.waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.BendingManager;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class IceCrawler extends BendingForm {

	public static ConcurrentHashMap<Player, BendingForm> instances = new ConcurrentHashMap<Player, BendingForm>();
	Location loc;
	Vector dir;
	int timer = 0;
	int relativeHeight = 0;
	boolean straight = false;
	
	// TODO need to make icecrawler melt, change it to using Integer instead of Player, 
	// TODO no blokbreak, store them
	// TODO: consider using click, and have sneak melt
	public IceCrawler(Player player)
	{
		if (!instances.contains(player) && Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			//find the player's eye dir
			this.player = player;
			loc = player.getEyeLocation().clone();
			dir = loc.getDirection().clone();
			//flatten the Y component
			dir.setY(0).normalize();
			//apply result to the block stood on and make a BlockIterator with it
			next = player.getLocation().add(0,-1,0).getBlock();
			loc = next.getLocation().clone();
			loc.setDirection(dir);
			bit = new BlockIterator(loc, 0.0, 10);
			next = bit.next();
			
			if (next.getType() != Material.AIR)
			{
				if (!Tools.isIce(next.getType()))
					next.breakNaturally();
				next.setType(Material.PACKED_ICE);
				instances.put(player, this);
			}
			
		}
	}
	
	@Override
	public boolean progress() {
		
		if (!player.isSneaking())
		{
			instances.remove(player);
			return false;
		}
			
		timer++;
		if (timer % 2 != 0)
			return false;
		
		if (bit.hasNext())
		{
			next = bit.next().getRelative(0, relativeHeight,0);
			Block newBlock = next;
			
			
			
			
			// once we've gone off an edge, keep going
			if (straight == true)
			{
				if (!Tools.isIce(next.getType()))
					next.breakNaturally();
				next.setType(Material.PACKED_ICE);
			}
			// if it's air we look for downhill
			else if (next.getType() == Material.AIR)
			{
				Block tmp = next.getRelative(BlockFace.DOWN);
				if (tmp.getType() == Material.AIR)
				{
					straight = true;
					next.setType(Material.PACKED_ICE);
				}
				else
				{
					if (!Tools.isIce(tmp.getType()))
						tmp.breakNaturally();
					tmp.setType(Material.PACKED_ICE);
					relativeHeight--;
					newBlock = tmp;
				}
			}
			else if (Tools.showsBelow(next.getRelative(BlockFace.UP)))
			{
				if (!Tools.isIce(next.getType()))
					next.breakNaturally();
				next.setType(Material.PACKED_ICE);
			}
			else if (Tools.showsBelow(next.getRelative(0,2,0)))
			{
				Block tmp = next.getRelative(BlockFace.UP);
				if (!Tools.isIce(tmp.getType()))
					tmp.breakNaturally();
				tmp.setType(Material.PACKED_ICE);
				newBlock = tmp;
				relativeHeight++;
			}
			else // else we STOP
			{
				instances.remove(player);
				return false;
			}
			
			
			nearEntities = Tools.getMobsAroundPoint(newBlock.getRelative(BlockFace.UP).getLocation().clone(), 1.5);
			nearEntities.remove(player);
			PotionEffect p_slow = new PotionEffect(PotionEffectType.SLOW, 600, 7, false, false);
			PotionEffect p_jump = new PotionEffect(PotionEffectType.JUMP, 600, 253, false, false);
			for (Entity entity : nearEntities) {
				if (entity instanceof Player)
				{
					Block freezeblk = entity.getLocation().getBlock();
					freezeblk.breakNaturally();
					freezeblk.setType(Material.ICE);
					BendingManager.paralyzeIce.put(freezeblk, (Player)entity);
					instances.remove(player);
					break;
				}
				else if (entity instanceof LivingEntity)
				{
					Block freezeblk = entity.getLocation().getBlock();
					freezeblk.breakNaturally();
					freezeblk.setType(Material.ICE);
					//((Damageable)entity).damage(1, player);
					((LivingEntity) entity).addPotionEffect(p_slow);
					((LivingEntity) entity).addPotionEffect(p_jump);
					instances.remove(player);
					break;
				}
				//entity.setVelocity(throwPath.multiply(2.5)); //should test this v.
				//((Damageable)entity).damage(3, player);
			}
			
		}
		else
		{
			instances.remove(player);
			return false;
		}
		
		
		
		return false;
	}
	// End of progress() loop
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

}
