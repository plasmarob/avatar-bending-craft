package me.plasmarob.bending.earthbending;

import me.plasmarob.bending.Tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class FallingReaction {
	
	@SuppressWarnings("deprecation")
	public FallingReaction(Block block)
	{
		while (Tools.isEarthbendable(block) || !Tools.isCrushable(block))
		{
			Material mat = block.getType();
			byte dat = block.getData();
			block.setType(Material.AIR);
			block.getWorld().spawnFallingBlock(block.getLocation(), mat, dat);
			//block.getWorld().spawnFallingBlock(block.getLocation(), Material.FIRE, dat);
			block = block.getRelative(BlockFace.UP);
		}
	}
}
