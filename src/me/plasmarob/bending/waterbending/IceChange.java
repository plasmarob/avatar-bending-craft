package me.plasmarob.bending.waterbending;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.BendingPlayer;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

//TODO: extends BendingForm
public class IceChange extends BendingForm {

	public static ConcurrentHashMap<Player, IceChange> instances = new ConcurrentHashMap<Player, IceChange>();
	private int range = 10;
	BendingPlayer bendingPlayer;
	boolean phase;
	boolean diving;
	int timer = 0;
	HashSet<Block> blocks = new HashSet<Block>();
	static List<BlockFace> faces;
	
	public IceChange(Player player)
	{
		bendingPlayer = BendingPlayer.getBendingPlayer(player.getUniqueId());
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
			bendingPlayer.toggleIce();
		
		if (!instances.contains(player))
		{
			instances.put(player, this);
			this.player = player;
			
			if (faces == null)
			{
				Tools.say(BlockFace.values().toString());
				faces = new LinkedList<BlockFace>();
				faces.add(BlockFace.UP);
				faces.add(BlockFace.DOWN);
				faces.add(BlockFace.NORTH);
				faces.add(BlockFace.SOUTH);
				faces.add(BlockFace.EAST);
				faces.add(BlockFace.WEST);
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
	
	//@SuppressWarnings("deprecation")
	public boolean progress() {
		
		if (!player.isSneaking())
		{
			instances.remove(player);
			return false;
		}
		
		// grow the body controlled
		timer++;
		if (timer % 20 == 0)
		{
			Block t;
			HashSet<Block> tmpSet = new HashSet<Block>();
			for (Block b : blocks)
			{
				if (phase)
				{
					for (BlockFace bf : faces)
					{
						t = b.getRelative(bf);
						if (Tools.isWater(t.getType()))
						{
							t.setType(Material.ICE);
							tmpSet.add(t);
						}
					}
				}
				else // !phase
				{
					for (BlockFace bf : faces)
					{
						t = b.getRelative(bf);
						if (Tools.isIce(t.getType()))
						{
							t.setType(Material.WATER);
							//t.setData((byte)4);
							tmpSet.add(t);
						}
					}
				}
			}
			blocks.addAll(tmpSet);
		}
		
		phase = bendingPlayer.getIceBool(); // T ice, F water
		diving = Tools.isWater(player.getEyeLocation().getBlock().getType());
		bit = new BlockIterator(player, range);
		while (bit.hasNext())
		{
			prev = next;
			next = bit.next();
			
			if (diving)	// below water
			{
				// pass through underwater unless a mob is hit
				if (Tools.isWater(next.getType()))
				{
					nearEntities = Tools.getMobsAroundPoint(next.getLocation().clone(), 1.5);
					nearEntities.remove(player);
					if (nearEntities.size() == 0)
						continue;
				}
				
				//freeze or thaw
				if (phase && !Tools.isIce(next.getType()))
				{
					prev.setType(Material.ICE);
					blocks.add(prev);
					break;
				}
				else if (!phase && Tools.isIce(next.getType()))
				{
					next.setType(Material.WATER);
					//next.setData((byte)4);
					blocks.add(next);
					break;
				}
			}
			else // above water
			{
				if (phase && Tools.isWater(next.getType()))
				{
					next.setType(Material.ICE);
					blocks.add(next);
					break;
				}
				if (!phase && Tools.isIce(next.getType()))
				{
					next.setType(Material.WATER);
					//next.setData((byte)4);
					blocks.add(next);
					break;
				}
				// don't pass through above water
				if (phase && (Tools.isWater(next.getType()) || Tools.isIce(next.getType())))
					break;
			}
		}
		return false;
	}
}
