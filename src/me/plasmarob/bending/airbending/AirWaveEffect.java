package me.plasmarob.bending.airbending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class AirWaveEffect extends Effect{
		
		double counter;
		float yaw;
		float yawStart;
		float yawEnd;
		float tempYaw;
		double speed;
		static final ParticleEffect cloud = ParticleEffect.CLOUD;
		
		Random rand;
		
		Location currentLoc; 
		Location location;
		static Vector blank = new Vector();
		int x = 0;
		
		public AirWaveEffect(EffectManager effectManager, Location location) {
			super(effectManager);
			//location.add(0,-.4,0);
			
			type = EffectType.REPEATING;
			iterations = 20;
			period = 1;
			counter = 0;
			speed = 0.75;
			
			location.setYaw(location.clone().getYaw() - 90);
			float pitch = location.getPitch();
			location.setPitch(0);
			Vector vec = location.getDirection().normalize().multiply(2.5);
			location.add(vec);
			location.setYaw(location.clone().getYaw() + 90);
			location.setPitch(pitch);
			setLocation(location);
			this.location = location;
		}

		@Override
		public void onRun() {

			
			
			//location.setYaw(location.clone().getYaw() + 90);
			
			Vector vec = location.getDirection().normalize().multiply(0.9);
			location.add(vec);	
			
			Location tempLoc = location.clone();
			tempLoc.setYaw(tempLoc.clone().getYaw() + 90);
			tempLoc.setPitch(0);
			vec = tempLoc.getDirection().normalize().multiply(0.3);
			for (int i = 0; i < 20; i++)
			{
				tempLoc.add(vec);
				cloud.display(blank, 0, tempLoc, visibleRange);
			}
			
			
			//location.subtract(vec);
			

		}

	}