package me.plasmarob.bending;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.plasmarob.bending.*;
import me.plasmarob.bending.airbending.*;
import me.plasmarob.bending.earthbending.*;
import me.plasmarob.bending.firebending.*;
import me.plasmarob.bending.waterbending.*;

/**
 * Bending manager, which manages moves each tick
 * 
 * @author      Robert Thorne <plasmarob@gmail.com>
 * @version     0.3                
 * @since       2014-10-05
 */
@SuppressWarnings("unused")
public class BendingManager implements Runnable {

	BendingManager()
	{
	}
	
	public static ConcurrentHashMap<Block, Entity> paralyzeIce = new ConcurrentHashMap<Block, Entity>();
	
	public void update()
	{
		

		
		
		// Fire
		FireBall.progressAll();
		FireJet.progressAll();
		FireEmit.progressAll();
		FireWave.progressAll();
		FireBeam.progressAll();
		FireSpinKick.progressAll();
		FireWall.progressAll();
		Lightning.progressAll();
		FireLaunch.progressAll();
		Combustion.progressAll();
		FireShield.progressAll();
		FireBlade.progressAll();
		FireCool.progressAll();
		FireBlast.progressAll();
		//FireHeat.progressAll();	// not needed ATM
		
		// Water
		WaterBlob.progressAll();
		WaterSplash.progressAll();
		WaterWhip.progressAll();
		IceChange.progressAll();
		IceCrawler.progressAll();
		WaterTsunami.progressAll();
		IceShield.progressAll();
		WaterBlade.progressAll();
		WaterExtinguish.progressAll();
		IceGrowth.progressAll();
		WaterBlast.progressAll();
			LaunchedSnow.progressAll();
			LaunchedIce.progressAll();
		WaterBubble.progressAll();
		WaterStreaming.progressAll();
		
		WaterTwister.progressAll();
			LaunchedWater.progressAll();
			TwisterBurst.progressAll();
			
		// Air
		AirBlade.progressAll();	
		AirGust.progressAll();
		AirScooter.progressAll();
		AirShield.progressAll();
		AirTwister.progressAll();
		
		
		// Earth
		EarthDig.progressAll();
		EarthPillar.progressAll();
		SandTarget.progressAll();
		EarthFissure.progressAll();
		EarthTsunami.progressAll();
		//EarthGrab.progressAll();
		EarthPush.progressAll(); // *
		EarthLaunch.progressAll();
		
		BendingPlayer.progressAll();
		
		if (paralyzeIce.size() > 0)
		{
			List<Block> list = Collections.list(paralyzeIce.keys());
			for (Block b : list)
			{
				
				if (paralyzeIce.get(b) instanceof LivingEntity)
				{
					LivingEntity le = (LivingEntity)paralyzeIce.get(b);
					if (!Tools.isIce(le.getLocation().getBlock().getType()) &&
						!Tools.isIce(le.getEyeLocation().getBlock().getType()))
					{
						paralyzeIce.remove(b);
					}
				}
				else if (b.getType() != Material.ICE)
				{
					paralyzeIce.remove(b);
				}
				
			}
			
		}
	}
	
	public void run()
	{
	    try {
	        update();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// called on server onDisable / stop / restart
	public static void removeAllBending()
	{
		WaterTwister.removeAll();
	}
	
	public static void removePlayer(Player p)
	{
		// Fire
				//FireBall.removePlayer(p);TODO: rewrite to extend BendingForm
				
		/*
				FireJet.removePlayer(p);
				FireEmit.removePlayer(p);
				FireWave.removePlayer(p);
				FireBeam.removePlayer(p);
				FireSpinKick.removePlayer(p);
				FireWall.removePlayer(p);
				Lightning.removePlayer(p);
				FireLaunch.removePlayer(p);
				Combustion.removePlayer(p);
				FireShield.removePlayer(p);
				FireBlade.removePlayer(p);
				FireCool.removePlayer(p);
				FireBlast.removePlayer(p);
				
				// Water
				WaterBlob.removePlayer(p);
				WaterWhip.removePlayer(p);
				IceChange.removePlayer(p);
				
				*/
				WaterTwister.removePlayer(p);
	}
	
}
