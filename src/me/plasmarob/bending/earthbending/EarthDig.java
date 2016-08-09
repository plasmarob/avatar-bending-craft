package me.plasmarob.bending.earthbending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EarthDig extends AbstractBendingForm {

	public static ConcurrentHashMap<Player, EarthDig> instances = new ConcurrentHashMap<Player, EarthDig>();
	BlockIterator bit;
	Block next;
	int time = 0;
	@SuppressWarnings("deprecation")
	public EarthDig(Player player)
	{
		if (Tools.lastKey(player) != 2)
			return;
		
		

		
		bit = new BlockIterator(player, 4);
		while (bit.hasNext()) {
			next = bit.next();	
			if (Tools.isEarthbendable(next)) {
				
				
				if(!instances.containsKey(player))
					instances.put(player, this);
				
				if (player.isSneaking() && next.getX() == player.getLocation().getBlockX() &&
						next.getZ() == player.getLocation().getBlockZ() &&
						next.getY() > player.getEyeLocation().getBlockY()) {
					
					player.setVelocity(new Vector(0,0.5,0));
					next.getWorld().spawnFallingBlock(next.getLocation(), next.getType(), next.getData());
					next.setType(Material.AIR);
					
					Block block = player.getLocation().getBlock().getRelative(0,0,0);
					/*
					if (block.getType() == Material.AIR){
						Material mat = player.getLocation().getBlock().getRelative(0,-1,0).getType();
						//blockMove(mat,block);
					}
					*/
				}
				else {
					next.breakNaturally();
				}
				break;
			}
		}
		
		if (player.isSneaking() && player.isOnGround()) {
			
			//Material mat = player.getLocation().getBlock().getRelative(0,-1,0).getType();
			//blockMove(mat,player.getLocation().getBlock());
			player.setVelocity(player.getVelocity().setY(0.5));
		}
	}
	
	private void blockMove(Material mat, Block block) {
		if (mat == Material.STONE
				|| mat == Material.DIRT
				|| mat == Material.COBBLESTONE
				|| mat == Material.SANDSTONE
				){
			block.setType(mat);
		} else if (mat == Material.DIAMOND_ORE
				|| mat == Material.IRON_ORE
				|| mat == Material.REDSTONE_ORE
				|| mat == Material.GLOWING_REDSTONE_ORE
				|| mat == Material.EMERALD_ORE
				|| mat == Material.COAL_ORE
				|| mat == Material.GOLD_ORE
				|| mat == Material.GRAVEL
				|| mat == Material.SAND
				|| mat == Material.BEDROCK
				){
			block.setType(Material.COBBLESTONE);
		}
	}
	
	public boolean progress() {
		
		player.setVelocity(player.getVelocity().setY(0.5));
		time++;
		if (time >= 20)
			instances.remove(player);
		return false;
	}
}
