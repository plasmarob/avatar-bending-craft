package me.plasmarob.bending.firebending;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.plasmarob.bending.Bending;
import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class FireHeat extends AbstractBendingForm{

	
	@SuppressWarnings("deprecation")
	public FireHeat(Player player)
	{
		this.player = player;
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			//instances.put(player, this);
			new FireCloudEffect(Bending.getEffectManager(), player.getEyeLocation().add(-0.5,0,-0.5)).start();
			new SmokeEffect(Bending.getEffectManager(), player.getEyeLocation().add(-0.5,1,-0.5)).start();
			new FireCloudEffect(Bending.getEffectManager(), player.getLocation().add(-0.5,0,-0.5)).start();
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.3f, 0.7f);
			
			HashSet<Block> burningPlaces = new HashSet<Block>();
			
			HashSet<Block> blocks = new HashSet<Block>();
			blocks.add(player.getEyeLocation().getBlock());
			for (int i = 0; i < 7; i++)
			{
				HashSet<Block> tmp = new HashSet<Block>();
				for (Block b : blocks)
				{
					tmp.add(b.getRelative(BlockFace.UP));
					tmp.add(b.getRelative(BlockFace.DOWN));
					tmp.add(b.getRelative(BlockFace.NORTH));
					tmp.add(b.getRelative(BlockFace.SOUTH));
					tmp.add(b.getRelative(BlockFace.EAST));
					tmp.add(b.getRelative(BlockFace.WEST));
				}
				blocks.addAll(tmp);
			}
			for (Block b : blocks)
			{
				if (b.getType() == Material.TORCH)
				{
					new FireCloudEffect(Bending.getEffectManager(), b.getLocation()).start();
					burningPlaces.add(b);
				}
				if (b.getTypeId() == 62)
				{
					Tools.say(player,b.getData());
					Block t = b;
					switch (b.getData())
					{
						case 2:
							t = b.getRelative(BlockFace.NORTH);
							break;
						case 3:
							t = b.getRelative(BlockFace.SOUTH);
							break;
						case 4:
							t = b.getRelative(BlockFace.WEST);
							break;
						case 5:
							t = b.getRelative(BlockFace.EAST);
					}
					new FireCloudEffect(Bending.getEffectManager(), t.getLocation()).start();
					burningPlaces.add(t);
				}
				if (b.getType() == Material.SNOW)
					b.setType(Material.AIR);
			}
			
			
			Block topCorner = player.getEyeLocation().getBlock().getRelative(-1,1,-1);
			Block t;
			for (int x = 0; x < 3; x++)
			{
				for (int z = 0; z < 3; z++)
				{
					for (int y = 0; y < 4; y++)
					{
						t = topCorner.getRelative(x,-y,z);
						if (t.getType() == Material.ICE || t.getType() == Material.SNOW_BLOCK)
						{
							t.setType(Material.WATER);
							t.setData((byte) 1);
						}
						if (t.getType() == Material.WATER || t.getType() == Material.STATIONARY_WATER)
						{
							new FireSteamEffect(Bending.getEffectManager(), t.getLocation()).start();
							t.setType(Material.AIR);
							t.setData((byte) 0);
						}
					}
				}
			}
			
			for (Block b : burningPlaces)
			{
				// TODO: get this centered
				nearEntities = Tools.getEntitiesAroundPoint(b.getLocation(), 2);
				nearEntities.remove(player);
				if (nearEntities.size() > 0)
				{
					for (Entity e : nearEntities)
						e.setFireTicks(40);
				}
			}
		}
		else if (Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			//ItemStack items = player.getItemInHand();
			for (int i = 1; i < 10; i++)
			{
				ItemStack items = player.getInventory().getItem(i);
				if (items == null)
					continue;
				if (items.getType().equals(Material.RAW_BEEF))
					{	items.setType(Material.COOKED_BEEF); player.setItemInHand(items); }
				else if (items.getType().equals(Material.PORK))
				{	items.setType(Material.GRILLED_PORK); player.setItemInHand(items); }
				else if (items.getType().equals(Material.MUTTON))
				{	items.setType(Material.COOKED_MUTTON); player.setItemInHand(items); }
				else if (items.getType().equals(Material.RABBIT))
				{	items.setType(Material.COOKED_RABBIT); player.setItemInHand(items); }
				else if (items.getType().equals(Material.RAW_CHICKEN))
				{	items.setType(Material.COOKED_CHICKEN); player.setItemInHand(items); }
				else if (items.getType().equals(Material.RAW_FISH) 
						&& ((int)items.getData().getData()) < 2) //fish+salmon, not puffer or clown
				{	items.setType(Material.COOKED_FISH); player.setItemInHand(items); }
				else if (items.getType().equals(Material.POTATO_ITEM))
				{	items.setType(Material.BAKED_POTATO); player.setItemInHand(items); }
				else if (items.getType().equals(Material.STICK))
				{	items.setType(Material.TORCH); player.setItemInHand(items); }
			}
			player.getWorld().playSound(player.getLocation(),Sound.ENTITY_GHAST_SHOOT,0.3f, 0.7f);
		}
	}
	
	@Override
	public boolean progress() {
		// TODO Auto-generated method stub
		return false;
	}

}
