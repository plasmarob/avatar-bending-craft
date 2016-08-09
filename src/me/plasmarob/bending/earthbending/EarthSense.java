package me.plasmarob.bending.earthbending;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.util.Tools;

public class EarthSense extends AbstractBendingForm {

	public EarthSense(Player player) {
		
		if (!player.isSneaking())
			return;
		
		this.player = player;
		
		bit = new BlockIterator(player, 5);
		while(bit.hasNext())
		{
			next = bit.next();
			if (canFeel(next))
			{
				see(next);
				//next.breakNaturally();
				break;
			}
		}
	}
	
	@Override
	public boolean progress() {
		// TODO Auto-generated method stub
		return false;
	}

	
	@SuppressWarnings("deprecation")
	private void see(Block seed) {
		ArrayList<Block> list = new ArrayList<Block>();
		//ArrayList<Block> changed = new ArrayList<Block>();
	
		list.add(seed);
		glassify(seed);
		//changed.add(seed);
		
		/*
		for(Block b : Tools.getTouchingBlocks(seed)) {
			if (b.getType() == Material.STONE ||
					b.getType() == Material.COBBLESTONE ||
					b.getType() == Material.SANDSTONE ||
					b.getType() == Material.RED_SANDSTONE 
					) {
				
				list.add(b);
				
			}
		}
		*/
		
		for (int i = 0; i < 8; i++) {
			int size = list.size();	// pre-determine size, so it doesn't keep up (infinite loop)
			for (int n = 0; n < size; n++) {
				for(Block b : Tools.getTouchingBlocks(list.get(n))) {
					if (list.contains(b))
						continue;
					else if (canFeel(b)) {
						list.add(b);
						glassify(b);
					}
				}
			}
		}
		
		
		
		
		
		//player.sendBlockChange(player.getLocation().add(0,-1,0), Material.GLASS, (byte)0);
	}
	
	private boolean canFeel(Block b) {
		return (b.getType() == Material.STONE ||
				b.getType() == Material.COBBLESTONE ||
				b.getType() == Material.SANDSTONE ||
				b.getType() == Material.RED_SANDSTONE ||
				b.getType() == Material.OBSIDIAN ||
				b.getType() == Material.DIRT ||
				b.getType() == Material.GRASS ||
				b.getType() == Material.GRASS_PATH
				);
	}
	
	@SuppressWarnings("deprecation")
	private void glassify(Block b) {
		if (b.getType() == Material.STONE || b.getType() == Material.COBBLESTONE) {
			if (b.getData() == 0 || b.getData() >= 5)
				player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)8);
			else if (b.getData() == 1 || b.getData() == 2)
				player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)6);
			else if (b.getData() == 3 || b.getData() == 4)
				player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)0);
		} else if (b.getType() == Material.SANDSTONE) {
			player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)4);
		} else if (b.getType() == Material.RED_SANDSTONE) {
			player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)1);
		} else if (b.getType() == Material.OBSIDIAN) {
			player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)15);
		} else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS_PATH) {
			player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)12);
		} else if (b.getType() == Material.GRASS) {
			player.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte)13);
		}
	}
	
}







