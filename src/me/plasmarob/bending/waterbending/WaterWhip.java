package me.plasmarob.bending.waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;


//TODO: improve range! center from player eye! more knockback!
public class WaterWhip extends AbstractBendingForm {

	double angle = 0;
	double yaw;
	double pitch;
	Location loc;
	Block block;
	Block block2;
	
	private ArrayList<Block> whip = new ArrayList<Block>();
	//private boolean isWhipping = false;
	private boolean isRotating = true;
	private BlockIterator whipIt;
	
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
	
	public WaterWhip(Player player)
	{
		this.player = player;
		block = player.getEyeLocation().getBlock(); // dummy value init
		block2 = player.getEyeLocation().getBlock(); // dummy value init
		if (Tools.lastKey(player) == 1)
		{
			
			bit = new BlockIterator(player, 5);
			Block next;
			while(bit.hasNext())
			{
				next = bit.next();
				if (next.getType() != Material.AIR)
				{
					if (next.getType() == Material.WATER || next.getType() == Material.STATIONARY_WATER || next.getType() == Material.ICE)
					{
						instances.put(player, this);
						player.getWorld().playSound(next.getLocation(),Sound.ENTITY_PLAYER_SWIM,0.2f, 1.5f);
					}
					return;	// skip using bucket
				}
			}
			if (player.getItemInHand().getType() == Material.WATER_BUCKET)
			{
				ItemStack is = new ItemStack(Material.BUCKET);
				player.setItemInHand(is);
				instances.put(player, this);
				player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_SWIM,0.2f, 1.5f);
			}
		}
		else if (Tools.lastKey(player) == 2 && instances.containsKey(player))
		{
			((WaterWhip)instances.get(player)).whip();
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_SWIM,0.2f, 1.5f);
		}
	}
	
	public void whip()
	{
		player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_SWIM,0.2f, 1.5f);
		double tmpAngle = angle % (2 * Math.PI);
		//if (player.getEyeLocation().getYaw() - loc.getYaw() > 0)
		if (tmpAngle > Math.PI && tmpAngle <= (3 * Math.PI / 2.0))
		{
			isRotating = false;
			Block next;
			BlockIterator bit = new BlockIterator(player, 7);
			next = bit.next();
			while(bit.hasNext())
				next = bit.next();
			Location tloc = this.block.getLocation().clone();
			tloc.setDirection(Tools.getDirection(block.getLocation(), next.getLocation()));
			whipIt = new BlockIterator(tloc, 0.0, 12);
		}
		else if (tmpAngle > (3 * Math.PI / 2.0) && tmpAngle < (Math.PI * 2.0))
		{
			//hit yourself
			instances.remove(player);
			player.damage(2.0, player);
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean progress() {
		
		if (!player.isSneaking())
		{
			instances.remove(player);
			if (player.getItemInHand().getType() == Material.BUCKET)
			{
				ItemStack is = new ItemStack(Material.WATER_BUCKET);
				player.setItemInHand(is);
			}
			block2.setType(Material.AIR);
			return false;
		}
		
		if (isRotating)
		{
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
			{
				block2.setType(Material.AIR);
				block2 = block;
			}
			
			angle += 0.2;
			//make a figure 8
			yaw = Math.sin(angle)*55;
			pitch = Math.sin(2*angle)*15;
			
			loc = player.getEyeLocation().clone();
			loc.setYaw((float) (loc.getYaw() +  yaw + 30));
			loc.setPitch((float) (loc.getPitch() +  pitch));
			bit = new BlockIterator(loc, 0.0, 3);
			block = bit.next();
			while (bit.hasNext())
				block = bit.next();
			if (block.getType() == Material.AIR)
			{
				block.setType(Material.WATER);
				block.setData((byte)1);
			}
		}
		else
		{
			for (int i = 0; i < 2; i++)
			{
				if (whipIt.hasNext())
				{
					player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_SWIM,0.2f, 1.5f);
					Block b = whipIt.next();
					if (b.getType() == Material.AIR)
					{
						b.setType(Material.WATER);
						b.setData((byte)5);
						whip.add(b);
						nearEntities = Tools.getMobsAroundPoint(b.getLocation().clone(), 1.5);
						nearEntities.remove(player);
						Vector throwPath = Tools.getDirection(player.getEyeLocation(), b.getLocation()).normalize();
						for (Entity entity : nearEntities) {
							entity.setVelocity(throwPath.multiply(2.5)); //should test this v.
							((Damageable)entity).damage(3, player);
						}
					}
					else
						break;
				}
				else
				{
					if (whip.size() > 0)
					{
						Block b = whip.get(whip.size()-1);
						b.setType(Material.AIR);
						whip.remove(b);
					}
					else
					{
						isRotating = true;
						whip.clear();
						break;
					}
				}
			}
		}
		
		return false;
	}
	
	Block getBlock()
	{
		return block;
	}
	Block getBlock2()
	{
		return block2;
	}
	boolean hasBlock(Block b)
	{
		return (whip.contains(b) || getBlock().equals(b) || getBlock2().equals(b));
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (((WaterWhip)instances.get(p)).hasBlock(block))
				return true;
			//if (instances.get(p).getBlock().equals(block))
		}
		return false;
	}
}
