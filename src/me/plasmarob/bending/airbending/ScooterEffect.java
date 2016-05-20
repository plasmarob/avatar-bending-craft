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
	//public ParticleEffect particle = ParticleEffect.CRIT_MAGIC;
	//public ParticleEffect particle = ParticleEffect.INSTANT_SPELL;
	//public ParticleEffect particle = ParticleEffect.SPELL;
	public ParticleEffect particle = ParticleEffect.SPELL_MOB;
	
	Entity entity;
	static Vector blank = new Vector();
	Color colour = Color.fromBGR(236, 231, 170);
	
	/**
	* Radius of the shield
	*/
	public int radius = 1;
	
	/**
	* Particles to display
	*/
	public int particles = 12;
	
	/**
	* Set to false for a half-sphere and true for a complete sphere
	*/
	public boolean sphere = true;
	
	public ScooterEffect(EffectManager effectManager, Entity entity) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = 500;
		period = 1;
		this.entity = entity;
		setEntity(entity);
	}
	
	
	@Override
	public void onRun() {
		Location location;
		location = entity.getLocation().add(0,-1,0);
		for (int i = 0; i < particles; i++) {
			
			float pitch = (90-(180*((2*((int)(i/2)+1)-1)/(1.0f*particles))));
			location.setPitch(pitch);

			

			
			for (int j = 0; j < 2; j++)
			{
				//use i to make different angle for each height
				location.setYaw(((i%2)*180) + (i*45) +  (System.currentTimeMillis()) % 360 + (j*16));
				Vector vector = location.getDirection().multiply(radius);
				location.add(vector);
				//particle.display(blank, 0, location, visibleRange);//, 0, 0, 0, 1, 0);
				particle.display(null, location, colour, visibleRange, 0f, 0f, 0f, 0, 1);
				//blue.display(location, visibleRange);
				location.subtract(vector);
			}
		}
	}

}