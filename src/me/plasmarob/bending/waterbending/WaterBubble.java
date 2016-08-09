package me.plasmarob.bending.waterbending;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WaterBubble extends AbstractBendingForm {
	
	public static ConcurrentHashMap<Player, WaterBubble> instances = new ConcurrentHashMap<Player, WaterBubble>();
	HashSet<Block> blocks = new HashSet<Block>();
	HashSet<Block> tmp = new HashSet<Block>();
	Block b;
	Block t;
	
	public WaterBubble (Player player)
	{
		this.player = player;
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			if (!instances.containsKey(player))
			{
				instances.put(player, this);
			}
			else
			{
				instances.get(player).clearBlocks();
				instances.remove(player);
			}
		}
	}

	void clearBlocks()
	{
		for (Block bk : blocks)
		{
			bk.setType(Material.WATER);
		}
		blocks.clear();
	}

	void checkBlocks()
	{
		clearBlocks();
		tmp.clear();
		b = player.getEyeLocation().getBlock();
		for (int x = -2; x <= 2; x++)
		{
			for (int y = -2; y <= 2; y++)
			{
				for (int z = -2; z <= 2; z++)
				{
					tmp.add(b.getRelative(x,y,z));
				}
			}
		}
		
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				tmp.add(b.getRelative(x,y,3));
				tmp.add(b.getRelative(x,y,-3));
			}
		}
		for (int x = -1; x <= 1; x++)
		{
			for (int z = -1; z <= 1; z++)
			{
				tmp.add(b.getRelative(x,3,z));
				tmp.add(b.getRelative(x,-3,z));
			}
		}
		for (int y = -1; y <= 1; y++)
		{
			for (int z = -1; z <= 1; z++)
			{
				tmp.add(b.getRelative(3,y,z));
				tmp.add(b.getRelative(-3,y,z));
			}
		}
		
		for (Block bk : tmp)
		{
			if (Tools.isWater(bk.getType()))
			{
				bk.setType(Material.AIR);
				blocks.add(bk);
			}
		}
		
	}
	

	@Override
	public boolean progress() {
		checkBlocks();
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
	
	private boolean hasBlock(Block block)
	{
		if (blocks != null && blocks.contains(block))
			return true;
		return false;
	}
	public static boolean blockHeld(Block block)
	{
		for (Player p : instances.keySet())
		{
			if (instances.get(p).hasBlock(block))
				return true;
		}
		return false;
	}
}
