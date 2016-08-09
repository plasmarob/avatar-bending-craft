package me.plasmarob.bending.earthbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.util.Tools;

public class EarthPillar {
	public static ConcurrentHashMap<Player, EarthPillar> instances = new ConcurrentHashMap<Player, EarthPillar>();
	private Player player;
	int distance = 0;
	
	BlockIterator bit;
	Block next;
	Block base;
	Block previous;
	Location baseLocation;
	boolean decided;
	BlockFace side;
	List<Entity> nearEntities;
	Material baseMat;
	int nearbyBendables = 0;
	
	
	int phase = 0; //which step of the calc we are in
	int progress = 0;
	
	boolean launchPlayerInstead = false;
	
	
	//private int tick = 0;
	/*
	int tx = player.getLocation().getChunk().getX();
	int tz = player.getLocation().getChunk().getZ();
	player.getWorld().regenerateChunk(tx, tz);
*/

// LIGHTNING player.getWorld().strikeLightningEffect(player.getLocation());
	
	private ArrayList<Block> pillars = new ArrayList<Block>();
	private ArrayList<Integer> bendingBlockCounts = new ArrayList<Integer>();
	private ArrayList<Location> baseLocs = new ArrayList<Location>();
	private ArrayList<Material> baseMats = new ArrayList<Material>();
	
	public EarthPillar(Player player)
	{
		if (!instances.containsKey(player))
		{
			if(Tools.lastKey(player) == 1)
			{
				instances.put(player, this);
				this.player = player;
			}
		}
		else
		{
			instances.get(player).recieve();
		}
	}
	
	public void recieve()
	{
		if (Tools.lastKey(player) == 2 && player.isSneaking())
		{
			bit = new BlockIterator(player, 10);
			next = player.getEyeLocation().getBlock();	//so we can set previous
			while(bit.hasNext())
			{
				distance++;
				previous = next.getLocation().getBlock();
				next = bit.next();
				
				
				if (distance > 3)
				{
					nearEntities = Tools.getEntitiesAroundPoint(next.getRelative(0,-1,0).getLocation().clone(), 1);
					nearEntities.addAll( Tools.getEntitiesAroundPoint(next.getLocation().clone(), 1) );
					nearEntities.remove(player);
					for (Entity entity : nearEntities) 
					{
						//Tools.say(player, entity.toString());
						Block blk = entity.getLocation().getBlock().getRelative(0,1,0);
						for (int i = 0; i < 3; i++)
						{
							blk = blk.getRelative(0,-1,0);
							side = BlockFace.UP;
							if(Tools.isCrushable(blk))
								continue;
							
							
							if (Tools.isEarthbendable(blk) || blk.getType() == Material.SNOW)
							{
								if (blk.getType() == Material.SNOW)
									blk = blk.getRelative(BlockFace.DOWN);
								
								base = blk;
								baseMat = base.getType();
								baseLocation = base.getLocation().clone();
								//side = base.getFace(previous);
								if (!pillars.contains(base))
								{
									pillars.add(base);
									baseLocs.add(baseLocation);
									baseMats.add(baseMat);
									//Tools.say(player, pillars.size());
									return;
								}
							}
						}			
					}
				}
				
				/////////////////////////////
				
				if (!Tools.isCrushable(next))
				{
					if (Tools.isEarthbendable(next))
					{
						base = next;
						//previous.setType(Material.GLASS);
						baseMat = base.getType();
						baseLocation = base.getLocation().clone();
						side = base.getFace(previous);
						
						if (!pillars.contains(base))
						{
							pillars.add(base);
							baseLocs.add(baseLocation);
							baseMats.add(baseMat);
							//Tools.say(player, pillars.size());
						}
						
						//Tools.say(player, base.getFace(previous).toString());
						
					}
					//Tools.say(player,distance);
					break;
				}
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
		
		if (phase == 0)
		{
			distance = 3;
			bit = new BlockIterator(player, distance);
			while(bit.hasNext())
			{
				next = bit.next();
				if (!Tools.isCrushable(next))
					break;
			}
		
			if (pillars.size() > 0)
			{
				
				Location newLoc = pillars.get(pillars.size() - 1).getLocation();
				//Tools.say(player, Tools.lastKey(player));
				if (Tools.lastKey(player) == 0)
				{	
					if( Tools.locationsMatch(newLoc, next.getLocation()))
					{
						pillars.clear();
						instances.remove(player);
						return false;
					}
					
					//Tools.say(player,"here");
					phase = 1;
					for (Block b : pillars)
					{
						player.getWorld().playSound(b.getLocation(),Sound.ENTITY_SILVERFISH_STEP, 1f, 0.1f);
						bendingBlockCounts.add(Tools.countNearbyEarthBendables(b, 3));
					}
				}
			}
		}
		else if (phase == 1)
		{
			progress++;
			if (launchPlayerInstead) {
				phase = 2;
				return false;
			}
			if (pillars.size() == 0)
			{
				instances.remove(player);
				return false;
			}
			for (int i = 0; i < pillars.size(); i++)
			{
				Block b = pillars.get(i);
				b = b.getRelative(side);
				pillars.set(i,  b);
				
				if (progress > 3 || !Tools.isCrushable(b) || bendingBlockCounts.get(i) < 20)
				{
					pillars.remove(b);
					bendingBlockCounts.remove(i);
					baseLocs.remove(i);
					baseMats.remove(i);
					i--;
				}
				else
				{
					
					nearEntities = Tools.getMobsAroundPoint(b.getLocation().clone().add(0.5,0,0.5), 1);
					if(nearEntities.contains(player))
					{
						// player.setVelocity(Tools.getDirection(baseLocs.get(i).add(0.0,0,0.0), player.getLocation()).normalize().multiply(2));
						player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(5));
						launchPlayerInstead = true;
					}
					nearEntities.remove(player);
					for (Entity entity : nearEntities) {
						entity.setVelocity(Tools.getDirection(baseLocs.get(i).add(0.5,0,0.5), entity.getLocation()).normalize().multiply(1.2));		
						((Damageable) entity).damage(2.0, player);
					}
					
					if (baseMats.get(i) == Material.GRASS
							|| baseMats.get(i) == Material.CROPS)
					{
						b.setType(Material.DIRT);
					}
					else if (baseMats.get(i) == Material.SANDSTONE 
							|| baseMats.get(i) == Material.RED_SANDSTONE
							|| baseMats.get(i) == Material.GRAVEL 
							|| baseMats.get(i) == Material.CLAY
							|| baseMats.get(i) == Material.HARD_CLAY
							)
					{
						b.setType(baseMats.get(i));
					}
					else if (baseMats.get(i) == Material.STAINED_CLAY
							|| baseMats.get(i) == Material.DIRT
							|| baseMats.get(i) == Material.STONE
							|| baseMats.get(i) == Material.SAND)
					{
						b.setType(baseMats.get(i));
						b.setData(baseLocs.get(i).getBlock().getData());
					}
					else
						b.setType(Material.COBBLESTONE);
					player.getWorld().playSound(b.getLocation(),Sound.BLOCK_GRAVEL_PLACE, 1f, 0.1f);
					
					nearbyBendables -= 20;
				}
			}
			
				
			
			
			/*
			base = base.getRelative(side);
				
			if (progress > 3 || !Tools.isCrushable(base) || nearbyBendables < 20)
				instances.remove(player);
			else
			{
				
				nearEntities = Tools.getEntitiesAroundPoint(base.getLocation().clone().add(0.5,0,0.5), 1);
				for (Entity entity : nearEntities) {
					entity.setVelocity(Tools.getDirection(baseLocation.add(0.5,0,0.5), entity.getLocation()).normalize());		
				}
				if (baseMat == Material.DIRT || baseMat == Material.GRASS
						|| baseMat == Material.CROPS)
				{
					base.setType(Material.DIRT);
				}
				else if (baseMat == Material.SAND )
					base.setType(Material.SAND);
				else if (baseMat == Material.SANDSTONE )
					base.setType(Material.SANDSTONE);
				else
					base.setType(Material.COBBLESTONE);
				player.getWorld().playSound(base.getLocation(),Sound.DIG_GRAVEL, 1f, 0.1f);
				
				nearbyBendables -= 20;
			}
			*/
		}
		else if (phase < 5) {
			player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(4));
			phase++;
		}
		else {
			instances.remove(player);
		}
		
		
		return false;
	}
	
	
	
	
	
}
