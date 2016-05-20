package me.plasmarob.bending.airbending;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class TwisterEffect extends Effect{
	
	//static final ParticleEffect flame = ParticleEffect.FLAME;
	static final ParticleEffect particle = ParticleEffect.SPELL_MOB;
	static final ParticleEffect cloud = ParticleEffect.CLOUD;
	
	private Vector vec;
	Random rand;
	boolean direction;
	Location location;
	static Vector blank = new Vector();
	Color colour = Color.fromBGR(236, 231, 170);
	
	public TwisterEffect(EffectManager effectManager, Location location, boolean direction) {
		super(effectManager);
		location.add(0,-2,0);
		
		Location tempLoc = location.clone();
		tempLoc.setPitch(0);
		vec = tempLoc.getDirection().normalize().multiply(0.1);
	
	
		visibleRange = 128;
		type = EffectType.REPEATING;
		iterations = 300;
		period = 1;
		rand = new Random();
		this.direction = direction;
		setLocation(location);
		this.location = location;
	}
	
	/**
	* Radius of the top
	*/
	public int radius = 3;
	
	/**
	* Particles to display
	*/
	public int particles = 50;
	
	@Override
	public void onRun() {
		location.add(vec);
		for (int i = 0; i < particles; i++) 
		{		
			location.setPitch(-70 + (new Random(i).nextInt() % 20) );
			//location.setYaw( ((i%2)*180) + (i*45) + (System.currentTimeMillis() / 5) % 720);
			if (direction)
				location.setYaw(  (i*90) - (System.currentTimeMillis() / 5) % 360);
			else
				location.setYaw(  (i*90) + (System.currentTimeMillis() / 5) % 360);
			Vector vector = location.getDirection().multiply(i/5 + rand.nextFloat());
		
			location.add(vector);
				particle.display(null, location, colour, visibleRange, 0f, 0f, 0f, 0, 1);
				cloud.display(blank, 0, location, visibleRange);
			location.subtract(vector);
		}
		
	}

	public void setDirection(Location dir) {
		Location tempLoc = dir.clone();
		tempLoc.setPitch(0);
		vec = tempLoc.getDirection().normalize().multiply(0.15);
	}
}