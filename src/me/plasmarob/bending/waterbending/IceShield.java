package me.plasmarob.bending.waterbending;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

public class IceShield extends BendingForm {

	public static ConcurrentHashMap<Player, BendingForm> instances = new ConcurrentHashMap<Player, BendingForm>();
	int timer = 0;
	
	int appearStage = 0;
	Entity foundArrow;
	HashSet<Block> shieldBlocks = new HashSet<Block>();
	int currentSlot;
	Vector dir;
	
	public IceShield(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player))
		{
			if (player.getItemInHand() != null)
			{
				if (player.getItemInHand().getType() == Material.WATER_BUCKET)
				{
					instances.put(player, this);
					currentSlot = player.getInventory().getHeldItemSlot();
				}
				if (player.getItemInHand().getType() == Material.BUCKET)
				{
					bit = new BlockIterator(player, 20);
					while (bit.hasNext())
					{
						next = bit.next();
						if (Tools.isOfWater(next))
						{
							instances.put(player, this);
							currentSlot = player.getInventory().getHeldItemSlot();
							player.setItemInHand(new ItemStack(Material.WATER_BUCKET));
							break;
						}
					}
				}
			}
		}
		else
		{
			((IceShield)instances.get(player)).timer = 0;
			
			if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
			{
				currentSlot = ((IceShield)instances.get(player)).currentSlot;
				if (player.getInventory().getItem(currentSlot).getType() == Material.WATER_BUCKET)
				{
					((IceShield)instances.get(player)).appearStage = 1;
					((IceShield)instances.get(player)).dir = player.getEyeLocation().getDirection();
					
					ItemStack is = new ItemStack(Material.BUCKET);
					player.getInventory().setItem(currentSlot, is);
				}
			}
			else if (Tools.lastKey(player) == PlayerAction.SNEAK_OFF.val())
			{
				((IceShield)instances.get(player)).appearStage = 10;
			}
		}
		
		
			
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean progress() {
		timer++;
		if (timer > 600) // 20*30
		{
			for (Block b : shieldBlocks)
			{
				b.setType(Material.WATER);
				b.setData((byte)2);
			}
			shieldBlocks.clear();
			instances.remove(player);
		}
		if (appearStage == 10)
		{
			if (shieldBlocks.size() > 0 && player.getItemInHand() != null &&
					player.getItemInHand().getType() == Material.BUCKET)
			{
				ItemStack is = new ItemStack(Material.WATER_BUCKET);
				player.setItemInHand(is);
			}
			
			
			for (Block b : shieldBlocks)
			{
				b.setType(Material.WATER);
				b.setData((byte)2);
			}
			shieldBlocks.clear();
			appearStage = 0;
			
		}
		
		
		if (appearStage == 0)
		{
			
			if (player.getInventory().getItem(currentSlot) == null ||
					player.getInventory().getItem(currentSlot).getType() != Material.WATER_BUCKET)
			{
				for (Block b : shieldBlocks)
					b.setType(Material.AIR);	
				shieldBlocks.clear();
				instances.remove(player);
				return false;
			}
			
			nearEntities = Tools.getEntitiesAroundPoint(player.getLocation(), 20);
			new WaterActiveEffect(Bending.getEffectManager(), player, Color.fromBGR(255, 158, 127)).start();
			for (Entity e : nearEntities)
			{
				if (e instanceof Arrow && e.getVelocity().length() > 0.7 && !((Arrow)e).isOnGround())
				{
					double arrowAngle = 360 - e.getLocation().getYaw(); // Arrow angle is backwards
					double playerAngle = player.getEyeLocation().getYaw();
					
					arrowAngle += 180; // rotate by 180 to check player-arrow diff
					while (arrowAngle < 0)
						arrowAngle += 360;
					while (arrowAngle >= 360)
						arrowAngle -= 360;

					//find unconditional shortest distance between angles
					int d = ((int)Math.abs(arrowAngle - playerAngle)) % 360;
					int r = d > 180 ? 360 - d : d;
					
					//if they're in almost opposite directions
					if ( r < 90 )
					{
						if (player.getInventory().getItem(currentSlot).getType() == Material.WATER_BUCKET)
						{
							foundArrow = e;
							appearStage = 1;
							dir = Tools.getDirection(player.getEyeLocation(), foundArrow.getLocation());
							player.getInventory().setItem(currentSlot, new ItemStack(Material.BUCKET));
						}
					}
				}
			}
		}
		
		
		
		if (appearStage == 2)
		{
			for (Block b : shieldBlocks)
			{
				b.setType(Material.ICE);
			}
			appearStage = 3;
		}
		if (appearStage == 1)
		{
			Location pLoc = player.getEyeLocation().clone();
			pLoc.setDirection(dir.clone());

			for (Block b : shieldBlocks)
				b.setType(Material.AIR);
			shieldBlocks.clear();
			
			for (int y = -20; y <= 20; y+=5)
			{
				for (int p = -20; p <= 20; p+=5)
				{
					pLoc.setDirection(dir.clone());
					pLoc.setYaw(pLoc.getYaw()+y);
					pLoc.setPitch(pLoc.getPitch()+p);
					bit = new BlockIterator(pLoc, 0.0, 2);
					while(bit.hasNext())
						next = bit.next(); 
					if (Tools.waterBreaks(next) || Tools.isOfWater(next))
					{
						next.setType(Material.WATER);
						shieldBlocks.add(next);	
					}
				}
			}
			appearStage = 2;
		}
		
		return false;
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


	public boolean hasBlock(Block block) {
		return shieldBlocks.contains(block);
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (((IceShield)instances.get(p)).hasBlock(block))
				return true;
		}
		return false;
	}

}
