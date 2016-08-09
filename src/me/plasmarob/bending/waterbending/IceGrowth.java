package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.List;
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

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.BendingManager;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class IceGrowth extends AbstractBendingForm {

	public static ConcurrentHashMap<Player, AbstractBendingForm> instances = new ConcurrentHashMap<Player, AbstractBendingForm>();
	Location loc;
	Vector dir;
	int timer = 0;
	int relativeHeight = 0;
	int topHeight = 0;
	boolean straight = false;
	int blockNum = 0;
	int yInit;
	Block original;
	ArrayList<BlockIterator> blits = new ArrayList<BlockIterator>();
	int inAir = 0;
	boolean blockLeft = false;
	Vector throwDir;
	List<Entity> frozenEntities = new ArrayList<Entity>();
	List<Entity> possibleEntities = new ArrayList<Entity>();
	
	// TODO need to make icecrawler melt, change it to using Integer instead of Player, 
	// TODO no blokbreak, store them
	public IceGrowth(Player player)
	{
		if (!instances.contains(player) && Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			//find the player's eye dir
			this.player = player;
			loc = player.getEyeLocation().clone();
			dir = loc.getDirection().clone();
			//flatten the Y component
			dir.setY(0).normalize();
			//apply result to the block stood on and make a BlockIterator with it
			next = player.getLocation().getBlock();
			loc = next.getLocation().clone();
			loc.setDirection(dir);
			
			
			Location throwLoc = loc.clone();
			throwLoc.setPitch(-45);
			throwDir = throwLoc.getDirection().clone().normalize();
			loc.setYaw(loc.getYaw()-12f);
			for (int i = 0; i < 7; i++)
			{
				blits.add(new BlockIterator(loc.clone(), 0.0, 12));
				loc.setYaw(loc.getYaw()+4f);
			}
			
			original = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (Tools.isOfWater(original) || Tools.isOfWater(original.getRelative(BlockFace.UP)))
			{
				instances.put(player, this);
				yInit = original.getY();
				
				possibleEntities.addAll(Tools.getMobsAroundPoint(player.getLocation(), 15));
			}
			
			
		}
	}
	
	@Override
	public boolean progress() {
		
		
		blockLeft = false;
		for (BlockIterator bit : blits)
		{
			if (bit.hasNext())
			{
				blockLeft = true;
				next = bit.next();
				
				if (next.getType() == Material.AIR &&
					next.getRelative(0,-1,0).getType() == Material.AIR &&
					next.getRelative(0,-2,0).getType() == Material.AIR )
					inAir++;
				
				blockNum = (int) Math.abs(Tools.getDirection(original.getLocation(), next.getLocation()).length());
				topHeight = (int) Math.ceil(Math.pow(blockNum/4.0, 2)) - 1;
				Block newBlock = next.getRelative(0, topHeight, 0);
				
				int tries = 5;
				for (int i = -1; i < topHeight; i++)
				{
					if (tries-- <= 0)
						break;
					if (Tools.waterBreaks(newBlock))
					{
						newBlock.setType(Material.PACKED_ICE);
						newBlock = newBlock.getRelative(0, -1, 0);
					}
					else
						break;
				}
				
				while (tries-- > 0 && Tools.waterBreaks(newBlock))
				{
					newBlock.setType(Material.PACKED_ICE);
					newBlock = newBlock.getRelative(0, -1, 0);
				}
				
				if (inAir >= 3) // run it out if it's been over an edge
				{
					while (bit.hasNext())
						next = bit.next();
				}
			}
		}

		
		nearEntities = Tools.getEntitiesAroundFromList(player.getLocation(), 20, possibleEntities);
		nearEntities.remove(player);
		for (int i = 0; i < nearEntities.size(); i++)
		{
			Entity e = nearEntities.get(i);
			if (e instanceof LivingEntity)
			{
				LivingEntity le = (LivingEntity)e;
				Block tb1 = le.getEyeLocation().getBlock();
				Block tb2 = le.getLocation().getBlock();
				
				if (tb1.getType() == Material.PACKED_ICE)
					tb1.setType(Material.ICE);
				if (tb2.getType() == Material.PACKED_ICE)
					tb2.setType(Material.ICE);
				
				if ( !(tb1.getType() == Material.ICE || tb2.getType() == Material.ICE))
				{
					nearEntities.remove(i);
					i--;
				}
				else //found
				{
					if (le instanceof Player)
					{
						Block freezeblk = le.getLocation().getBlock();
						freezeblk.breakNaturally();
						freezeblk.setType(Material.ICE);
						BendingManager.paralyzeIce.put(freezeblk, (Player)le);
					}
					else
					{
						//Block freezeblk = entity.getLocation().getBlock();
						//freezeblk.breakNaturally();
						//freezeblk.setType(Material.ICE);
						
						//((Damageable)entity).damage(1, player);
						//((LivingEntity) entity).addPotionEffect(p_slow);
						//((LivingEntity) entity).addPotionEffect(p_jump);
						le.setVelocity(throwDir.multiply(2)); 
						frozenEntities.add(le);
					}
				}
			}
			else
			{
				nearEntities.remove(i);
				i--;
			}
		}
		
		if (!blockLeft)
		{
			PotionEffect p_slow = new PotionEffect(PotionEffectType.SLOW, 600, 7, false, false);
			PotionEffect p_jump = new PotionEffect(PotionEffectType.JUMP, 600, 253, false, false);
			for (Entity e : frozenEntities)
			{
				((LivingEntity) e).addPotionEffect(p_slow);
				((LivingEntity) e).addPotionEffect(p_jump);
				e.setVelocity(new Vector(0,0,0));
				
				iceAround(((LivingEntity) e).getEyeLocation().getBlock());
				iceAround(((LivingEntity) e).getLocation().getBlock());
				//((LivingEntity) e).getEyeLocation().getBlock().setType(Material.ICE);
				//((LivingEntity) e).getLocation().getBlock().setType(Material.ICE);
			}
			instances.remove(player);
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
	
	public void iceAround(Block b)
	{
		b.getRelative(0,0,0).setType(Material.ICE);
		b.getRelative(0,1,0).setType(Material.ICE);
		b.getRelative(0,-1,0).setType(Material.ICE);
		
		b.getRelative(-1,0,-1).setType(Material.ICE);
		b.getRelative( 1,0,-1).setType(Material.ICE);
		b.getRelative(-1,0, 1).setType(Material.ICE);
		b.getRelative( 1,0, 1).setType(Material.ICE);
		
		b.getRelative(-1,-1,-1).setType(Material.ICE);
		b.getRelative( 1,-1,-1).setType(Material.ICE);
		b.getRelative(-1,-1, 1).setType(Material.ICE);
		b.getRelative( 1,-1, 1).setType(Material.ICE);
		
		b.getRelative( 0,-1,-1).setType(Material.ICE);
		b.getRelative( 0,-1, 1).setType(Material.ICE);
		b.getRelative(-1,-1, 0).setType(Material.ICE);
		b.getRelative( 1,-1, 0).setType(Material.ICE);
		
		b.getRelative( 0, 0,-1).setType(Material.ICE);
		b.getRelative( 0, 0, 1).setType(Material.ICE);
		b.getRelative(-1, 0, 0).setType(Material.ICE);
		b.getRelative( 1, 0, 0).setType(Material.ICE);
		
		b.getRelative( 0, 1,-1).setType(Material.ICE);
		b.getRelative( 0, 1, 1).setType(Material.ICE);
		b.getRelative(-1, 1, 0).setType(Material.ICE);
		b.getRelative( 1, 1, 0).setType(Material.ICE);
		/*
		for (int x = -1; x < 2; x++)
		{
			for (int y = -1; y < 1; y++)
			{
				for (int z = -1; z < 2; z++)
				{
					if (Tools.waterBreaks(b.getRelative(x,y,z)))
						b.getRelative(x,y,z).setType(Material.ICE);
				}
			}
		}
		*/
		
	}

}
