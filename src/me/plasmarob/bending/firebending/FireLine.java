package me.plasmarob.bending.firebending;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
//import org.bukkit.util.Vector;

import me.plasmarob.bending.AbstractBendingForm;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.util.Tools;

public class FireLine extends AbstractBendingForm {

	@SuppressWarnings("deprecation")
	public FireLine(Player player)
	{
		if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			if (player.isSneaking())
			{	
				bit = new BlockIterator(player, 3);
				next = bit.next();
				next = bit.next();
				
				Location tempLoc = player.getEyeLocation().clone();
				tempLoc.setYaw(tempLoc.getYaw()-24);
				for (int i = 0; i < 7; i++)
				{
					FallingBlock fb = player.getWorld().spawnFallingBlock(next.getLocation(), Material.FIRE, (byte) 0);
					fb.setDropItem(false);
					fb.setVelocity(tempLoc.getDirection().clone());
					tempLoc.setYaw(tempLoc.getYaw()+8);
				}
			}
			else
			{
				bit = new BlockIterator(player, 3);
				next = bit.next();
				next = bit.next();
				FallingBlock fb = player.getWorld().spawnFallingBlock(next.getLocation(), Material.FIRE, (byte) 0);
				fb.setDropItem(false);
				fb.setVelocity(player.getEyeLocation().clone().getDirection().multiply(1.4));
			}
		}
	}
	
	
	@Override
	public boolean progress() {
		// TODO Auto-generated method stub
		return false;
	}

}
