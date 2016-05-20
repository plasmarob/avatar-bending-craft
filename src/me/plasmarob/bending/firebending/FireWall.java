package me.plasmarob.bending.firebending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class FireWall extends BendingForm {

	private ArrayList<Block> sources = new ArrayList<Block>();
	//private ArrayList<FallingBlock> flames = new ArrayList<FallingBlock>();
	//public ConcurrentHashMap<Location, FallingBlock> locations = new ConcurrentHashMap<Location, FallingBlock>();
	public ArrayList<ConcurrentHashMap<Location, FallingBlock>> flameArray = new ArrayList<ConcurrentHashMap<Location, FallingBlock>>();
	
	//final Vector still = new Vector(0,0.081,0);
	final Vector up = new Vector(0,0.5,0);
	int timer = 0;
	int wrapDelay = 50;
	public boolean activating = false;
	public boolean deactivating = false;
	boolean active = false;
	int rowCount = 0;
	int currentRow = 0;
	int height = 2;
	int speed = 20;
	
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
	
	public FireWall(Player player)
	{
		if (!instances.containsKey(player))
		{
			if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
			{
				instances.put(player, this);
				this.player = player;
			}
		}
		else 
		{
			FireWall instance = ((FireWall) instances.get(player));
			if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			{
				if (!instance.getDeactivating())
					instance.setActivating(true);
			}
			else if (Tools.lastKey(player) == PlayerAction.SNEAK_OFF.val())
			{
				instance.setDeactivating(true);
			}
		}
		
	}
	
	
	public void setActivating(boolean activating)
	{
		this.activating = activating;
	}
	public boolean getDeactivating()
	{
		return deactivating;
	}
	public void setDeactivating(boolean deactivating)
	{
		this.deactivating = deactivating;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean progress() {
		
		if (activating)
		{
			if (timer == 0)
			{
				ConcurrentHashMap<Location, FallingBlock> flameMap = new ConcurrentHashMap<Location, FallingBlock>();
				for (Block b : sources)
				{
					if(Tools.firePasses(b))
					{
						FallingBlock fb = player.getWorld().spawnFallingBlock(b.getLocation().clone(), Material.FIRE, (byte) 0);
						fb.setDropItem(false);
						fb.setVelocity(up);
						flameMap.put(b.getLocation().clone(), fb);
					}
				}
				flameArray.add(flameMap);
				rowCount = flameArray.size();
				if (rowCount >= height)
				{
					activating = false;
					active = true;
				}
			}
			timer = (timer + 1) % speed;
		}
		else if (active)
		{
			if (timer == 0)
			{
				//Tools.say(flameArray.size());
				ConcurrentHashMap<Location, FallingBlock> flameMap = flameArray.get(currentRow);
				for (Location l : flameMap.keySet())
				{
					FallingBlock old = flameMap.get(l);
					
					old.remove();
					FallingBlock fb = player.getWorld().spawnFallingBlock(l.clone().add(0,0,0), Material.FIRE, (byte) 0);
					fb.setDropItem(false);
					fb.setVelocity(up);
					flameMap.replace(l, fb);
					/*
					old.teleport(l.clone().add(0.5,-0.5,0.5));
					old.setVelocity(up);
					*/
				}
				currentRow = (currentRow + 1) % rowCount;
			}
			timer = (timer + 1) % speed;
		}
		
		if (active || activating)
		{
			for (Block b : sources)
			{
				if(b.getType() == Material.AIR || b.getType() == Material.FIRE)
				{
					b.setType(Material.FIRE);
				}
			}
		}
		
		
		if (player.isSneaking())
		{
			if (!activating && !active)
			{
				bit = new BlockIterator(player, 20);
				while (bit.hasNext())
				{
					next = bit.next();
					if (!Tools.firePasses(next))
					{
						Block temp = next;
						for (int i = 0; i < 4; i++)
						{
							temp = temp.getRelative(BlockFace.UP);
							if (Tools.firePasses(temp))
							{
								if (!sources.contains(temp))
								{
									sources.add(temp);
									//Tools.say(player,"adding");
									if (sources.size() > 15) // THE WIDTH
										sources.remove(0);
								}	
								break;
							}
						}
						break;
					}
				}
			}
		}
		else
		{
			deactivating = true;
			instances.remove(player);
			for (ConcurrentHashMap<Location, FallingBlock> flameMap : flameArray)
			{
				flameMap.clear();
			}
			flameArray.clear();
			sources.clear();
		}
		return false;
	}
	

}
