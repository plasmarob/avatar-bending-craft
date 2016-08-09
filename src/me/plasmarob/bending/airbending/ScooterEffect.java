package me.plasmarob.bending.airbending;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
//import de.slikey.effectlib.util.RandomUtils;
//import de.slikey.effectlib.util.VectorUtils;

public class ScooterEffect extends Effect {
	/**
	* ParticleType of spawned particle
	*/
	public ParticleEffect particle = ParticleEffect.CRIT_MAGIC;
	//public ParticleEffect particle = ParticleEffect.INSTANT_SPELL;
	//public ParticleEffect particle = ParticleEffect.SPELL;
	//public ParticleEffect particle = ParticleEffect.SPELL_MOB;
	
	Entity entity;
	static Vector blank = new Vector();
	Color colour = Color.fromBGR(236, 231, 170);
	
	/**
	* Radius of the shield
	*/
	public float radius = 0.6f; //8
	
	/**
	* Particles to display
	*/
	public int particles = 15; //50
	
	/**
	* Set to false for a half-sphere and true for a complete sphere
	*/
	public boolean sphere = true;
	
	
	float pitch;
	Vector vector;
	
	public ScooterEffect(EffectManager effectManager, Entity entity) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = 5000;
		period = 1;
		this.entity = entity;
		setEntity(entity);
	}
	
	
	//@Override
	public void onRun2() {
		Location location;
		location = entity.getLocation().add(0,0,0);
		
		
		//for (int i = 0; i < 2; i++)
			
		
		//ParticleEffect.CRIT_MAGIC.display(v.normalize(), 3f, tempLoc, visibleRange);
		
		for (int i = 0; i < particles; i++) {
			
			float pitcht = (90-(180*((2*((int)(i/2)+1)-1)/(1.0f*particles))));
			location.setPitch(pitcht);

			

			
			for (int j = 0; j < 2; j++)
			{
				//use i to make different angle for each height
				location.setYaw(((i%2)*180) + (i*45) +  (System.currentTimeMillis()) % 360 + (j*16));
				Vector vectort = location.getDirection().multiply(radius);
				location.add(vectort);
				//particle.display(blank, 0, location, visibleRange);//, 0, 0, 0, 1, 0);
				particle.display(null, location, colour, visibleRange, 0f, 0f, 0f, 0, 1);
				//blue.display(location, visibleRange);
				location.subtract(vectort);
			}
		}
	}
	
	
	
	@Override
	public void onRun() {
		Location location;
		location = entity.getLocation().add(0,0,0);
		for (int i = 0; i < particles; i++) {
			
			//time = System.currentTimeMillis() / 6;
			//double y = (radius - 2*((i+1)/(1.0*particles) * radius));
			//location = entity.getLocation().add(2*Math.sin(time) * (radius-Math.abs(y)), y, 2*Math.cos(time)*(radius-Math.abs(y)));

			pitch = (90-(180*((2*((int)(i/2)+1)-1)/(1.0f*particles))));
			location.setPitch(pitch);
			//location.setPitch(-75);
			location.setYaw( ((i%2)*180) + (i*45) + (System.currentTimeMillis() / 3) % 360);
			
			vector = location.getDirection().multiply(radius);
			//Vector vector = RandomUtils.getRandomVector().multiply(radius);
			//if (!sphere)
				//vector.setY(Math.abs(vector.getY()));
			//VectorUtils.rotateAroundAxisY(vector, (location.getPitch()) * MathUtils.degreesToRadians);
			//location = entity.getLocation();
			//if (location.getPitch() > -45)
				location.add(vector);
			//else
			
			//particle.display(null, location, colour, visibleRange, 0f, 0f, 0f, 0, 1);
			particle.display(blank, 3f, location, visibleRange);
			//particle.display(entity.getLocation(), 16, 2,2,2, 24345456f, 1);
			location.subtract(vector);
		}
	}

}