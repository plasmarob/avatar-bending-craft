package me.plasmarob.bending.waterbending;

import java.util.concurrent.ConcurrentHashMap;

import me.plasmarob.bending.Bending;
import me.plasmarob.bending.BendingForm;
import me.plasmarob.bending.BendingPlayer;
import me.plasmarob.bending.PlayerAction;
import me.plasmarob.bending.Tools;

import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterBlast extends BendingForm {

	public static ConcurrentHashMap<Player, WaterBlast> instances = new ConcurrentHashMap<Player, WaterBlast>();
	
	private int timer = 0;
	
	//private int type; // 0=water, 1=snow, 2=ice
	//private Material mat;
	Block tmp;
	Vector throwPath;
	
	Player player;
	public WaterBlast(Player player)
	{
		this.player = player;
		if (!instances.containsKey(player) && 
				Tools.lastKey(player) == PlayerAction.SNEAK_ON.val())
		{
			instances.put(player, this);
		}
		else if (Tools.lastKey(player) == PlayerAction.LEFT_CLICK.val())
		{
			if (instances.containsKey(player))
			{
				instances.get(player).tryShoot();
			}
			else
			{
				BendingPlayer bp = BendingPlayer.getBendingPlayer(player);
				if ( bp != null)
					bp.toggleIce();
			}
		}
	}
	

	private void tryShoot() {
		
		if (timer < 20)
			return;
		Block playerAt = player.getLocation().getBlock();
		Block playerBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		if (Tools.isSnow(playerAt.getType()) ||
				Tools.isSnow(playerBelow.getType()))
		{
			new LaunchedSnow(player);
			timer = 0;
		}
		else if (Tools.isWater(playerAt.getType()) ||
				Tools.isWater(playerBelow.getType()))
		{
			if (BendingPlayer.isBender(player) 
				&& BendingPlayer.getBendingPlayer(player).getIceBool())
				new LaunchedIce(player);
			else
				new LaunchedWater(player);
			
			timer = 0;
		}	
		else
		{
			/*
			Biome biome = player.getLocation().getBlock().getBiome();
			if (biome.equals(Biome.DESERT) ||
					biome.equals(Biome.DESERT_HILLS) ||
					biome.equals(Biome.DESERT_MOUNTAINS) ||
					biome.equals(Biome.) ||		
					)
					*/
		}
	}


	@Override
	public boolean progress() {		
		
		timer++;
		if (timer >= 20)
		{
			if  (Tools.isSnow(player.getLocation().getBlock().getType()) ||
					Tools.isSnow(player.getLocation().getBlock().getRelative(0,-1,0).getType()) ) 
				new WaterActiveEffect(Bending.getEffectManager(), player, Color.WHITE).start();
			else if  (Tools.isWater(player.getLocation().getBlock().getType()) ||
					Tools.isWater(player.getLocation().getBlock().getRelative(0,-1,0).getType()) ) 
			{
				if (BendingPlayer.getBendingPlayer(player).getIceBool())
					new WaterActiveEffect(Bending.getEffectManager(), player, Color.fromBGR(255, 158, 127)).start();
				else
					new WaterActiveEffect(Bending.getEffectManager(), player, Color.BLUE).start();
			}
				
			
		}
			
		if (!player.isSneaking())
			instances.remove(player);
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
}