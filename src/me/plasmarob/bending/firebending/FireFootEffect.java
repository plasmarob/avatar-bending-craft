package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class FireFootEffect extends Effect{
		
		Location location;
		static final ParticleEffect flame = ParticleEffect.FLAME;
		static final ParticleEffect smoke = ParticleEffect.SMOKE_NORMAL;
		static Vector blank = new Vector();
		Random rand = new Random();
		int density = 25;
		Player player;
				
		public FireFootEffect(EffectManager effectManager, Player player) {
			super(effectManager);
			//location.add(0,-.4,0);
			
			//double theta = location.getPitch()*Math.PI/180;
			this.player = player;
			type = EffectType.INSTANT;
			iterations = 1;
			period = 1;
			//counter = 0;
			//speed = 0.75;

			
			//location.setYaw(location.clone().getYaw() - 90);
			//location.getDirection().multiply(5);
			setEntity(player);
		}

		@Override
		public void onRun() {
			
			Location tloc = player.getEyeLocation().clone();
			tloc.setYaw(tloc.getYaw() + 90);
			Location loc1 = player.getLocation().add(tloc.getDirection().normalize().multiply(0.3));
			Location loc2 = player.getLocation().subtract(tloc.getDirection().normalize().multiply(0.3));
			flame.display(blank, 0, loc1, visibleRange);
			flame.display(blank, 0, loc2, visibleRange);
			
		}
		
	}
