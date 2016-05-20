package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class FireCloudEffect extends Effect{
		
		Location location;
		static final ParticleEffect flame = ParticleEffect.FLAME;
		static final ParticleEffect smoke = ParticleEffect.SMOKE_NORMAL;
		static Vector blank = new Vector();
		Random rand = new Random();
		int density = 25;
		
		public FireCloudEffect(EffectManager effectManager, Location location, int density) {
			super(effectManager);
			//location.add(0,-.4,0);
			density = this.density;
			//double theta = location.getPitch()*Math.PI/180;
			
			type = EffectType.REPEATING;
			iterations = 1;
			period = 1;
			//counter = 0;
			//speed = 0.75;

			
			//location.setYaw(location.clone().getYaw() - 90);
			this.location = location;
			//location.getDirection().multiply(5);
			setLocation(location);
		}
		
		public FireCloudEffect(EffectManager effectManager, Location location) {
			super(effectManager);
			//location.add(0,-.4,0);
			
			//double theta = location.getPitch()*Math.PI/180;
			
			type = EffectType.REPEATING;
			iterations = 1;
			period = 1;
			//counter = 0;
			//speed = 0.75;

			
			//location.setYaw(location.clone().getYaw() - 90);
			this.location = location;
			//location.getDirection().multiply(5);
			setLocation(location);
		}

		@Override
		public void onRun() {
			
			
			for (int i = 0; i < density; i++)
			{
				Location tLoc = location.clone().add(rand.nextFloat()*1.1, rand.nextFloat()*1.1, rand.nextFloat()*1.1);
				flame.display(blank, 0, tLoc, visibleRange);
			}
			Location tLoc = location.clone().add(rand.nextFloat()*1.1, rand.nextFloat()*1.1, rand.nextFloat()*1.1);
			smoke.display(blank, 0, tLoc, visibleRange);
		}
		
	}
