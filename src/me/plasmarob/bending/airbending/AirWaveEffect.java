package me.plasmarob.bending.airbending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
/**
 * Launches a horizontal or vertical wave of cloud particles from a person
 * @user AirBlade
 * @author plasmarob
 * @since 0.4.1
 */
public class AirWaveEffect extends Effect{
		
		double counter;
		double speed;
		static final ParticleEffect cloud = ParticleEffect.CLOUD;
		Vector v;
		Random rand;
		
		Location currentLoc; 
		Location location;
		boolean isVert;
		Vector vec;
		Location tempLoc;
		
		Vector vec2;
		Location tempLoc1;
		Location tempLoc2;
		
		int width=20;
		
		public AirWaveEffect(EffectManager effectManager, Location location, boolean isVert) {
			super(effectManager);
			this.isVert = isVert;
			
			type = EffectType.REPEATING;
			iterations = 3;
			period = 1;
			counter = 0;
			speed = 0.75;
			
			v = location.clone().getDirection();
			
			if (!isVert) {
				location.setYaw(location.clone().getYaw() - 90);
				float pitch = location.getPitch();
				location.setPitch(0);
				vec = location.getDirection().normalize().multiply(0.3);
				location.add(vec);
				location.setYaw(location.clone().getYaw() + 90);
				location.setPitch(pitch);
				
				location.setYaw(location.clone().getYaw() + 90);
				pitch = location.getPitch();
				location.setPitch(0);
				vec2 = location.getDirection().normalize().multiply(0.3);
				location.add(vec2);
				location.setYaw(location.clone().getYaw() - 90);
				location.setPitch(pitch);
			} else {
				location.setPitch(location.clone().getPitch() - 90);
				Vector vec = location.getDirection().normalize().multiply(2);
				location.add(vec);
				location.setPitch(location.clone().getPitch() + 90);
			}
			setLocation(location);
			this.location = location;
		}

		@Override
		public void onRun() {
			
			//location.add(location.getDirection().normalize().multiply(0.9));		
			if (!isVert) {
				//Tools.say("Horiz");
				
				tempLoc1 = location.clone();
				tempLoc1.setYaw(location.getYaw());
				tempLoc1.setPitch(0);
				
				tempLoc2 = location.clone();
				tempLoc2.setYaw(location.getYaw());
				tempLoc2.setPitch(0);
				//Vector tempV = vec.clone();
				//tempV.setY(0);
				
				cloud.display(v.normalize(), 2.5f, tempLoc1, visibleRange);		
				for (int i = 0; i < width/2; i++) {
					tempLoc1.add(vec);			
					cloud.display(v.normalize(), 2.5f, tempLoc1, visibleRange);		
				}
				for (int i = 0; i < width/2; i++) {
					tempLoc2.add(vec2);			
					cloud.display(v.normalize(), 2.5f, tempLoc2, visibleRange);		
				}
				
				/*
				tempLoc = location.clone();
				tempLoc.setYaw(location.getYaw() - 90);
				tempLoc.setPitch(0);
				vec = tempLoc.getDirection().clone().normalize().multiply(-0.3);
				vec = vec.setY(0).normalize();
				for (int i = 0; i < width/2; i++) {
					tempLoc.add(vec);			
					cloud.display(v.normalize(), 2.5f, tempLoc, visibleRange);		
				}
				*/
				
			} else {
				
				vec = location.getDirection().normalize().multiply(0.9);
				location.add(vec);		
				tempLoc = location.clone();
				//location.add(location.getDirection().normalize().multiply(0.9));
				tempLoc.setPitch(tempLoc.getPitch() + 90);
				
				vec = tempLoc.getDirection().normalize().multiply(0.3);
				for (int i = 0; i < width; i++) {
					tempLoc.add(vec);			
					cloud.display(v.normalize(), 3f, tempLoc, visibleRange);		
				}
			}
			
		}

	}
