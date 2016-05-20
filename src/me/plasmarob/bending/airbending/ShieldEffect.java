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

public class ShieldEffect extends Effect {
	/**
	* ParticleType of spawned particle
	*/
	public ParticleEffect particle = ParticleEffect.SPELL_MOB;

	// ParticleEffect.ANGRY_VILLAGER - lightning cloud float up
	// ParticleEffect.BUBBLE - XX BROKE XX
	// ParticleEffect.CLOUD - white puffs;
	// ParticleEffect.CRIT - brown sparkles
	// ParticleEffect.DEPTH_SUSPEND - XX BROKE XX
	// ParticleEffect.DRIP_LAVA - dark lava drips and falls
	// ParticleEffect.DEPTH_WATER - blue water drips and falls
	// ParticleEffect.ENCHANTMENT_TABLE - white falling random letters
	// ParticleEffect.EXPLODE - larger CLOUD white puffs;
	// ParticleEffect.FIREWORKS_SPARK - dripping white spark
	// ParticleEffect.FLAME - fire
	// ParticleEffect.HAPPY_VILLAGER - green sparkles
	// ParticleEffect.HEART - animal heart
	// ParticleEffect.HUGE_EXPLOSION - gigantic semicircle
	// ParticleEffect.INSTANT_SPELL - white sparkle going up
	
	// ParticleEffect.LAVA - bouncing flying lava sparks
	// ParticleEffect.MAGIC_CRIT - teal circle 
	// ParticleEffect.MOB_SPELL - black spiral
	// ParticleEffect.MOB_SPELL_AMBIENT - black spiral's ghost
	
	// ParticleEffect.SPELL - small white spiral
	// ParticleEffect.SPLASH - bouncing blue squares
	// ParticleEffect.SUSPEND - XX BROKE XX
	// ParticleEffect.TOWN_AURA - tiny gray square 
	// ParticleEffect.WAKE - tiny blue square triplets
	// ParticleEffect.WITCH_MAGIC - ascending purple sparkles
	
	/**
	* Radius of the shield
	*/
	public int radius = 2;
	
	/**
	* Particles to display
	*/
	public int particles = 40;
	Entity entity;
	static Vector blank = new Vector();
	Color colour = Color.fromBGR(236, 231, 170);
	Location location;
	
	
	/**
	* Set to false for a half-sphere and true for a complete sphere
	*/
	public boolean sphere = true;
	
	public ShieldEffect(EffectManager effectManager, Entity entity) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = 500;
		period = 1;
		
		setEntity(entity);
		this.entity = entity;
	}
	
	@Override
	public void onRun() {
		
		location = entity.getLocation().add(0,1,0);
		for (int i = 0; i < particles; i++) {
			
			//time = System.currentTimeMillis() / 6;
			//double y = (radius - 2*((i+1)/(1.0*particles) * radius));
			//location = entity.getLocation().add(2*Math.sin(time) * (radius-Math.abs(y)), y, 2*Math.cos(time)*(radius-Math.abs(y)));
			
			
			float pitch = (90-(180*((2*((int)(i/2)+1)-1)/(1.0f*particles))));
			location.setPitch(pitch);
			//location.setPitch(-75);
			location.setYaw( ((i%2)*180) + (i*45) + (System.currentTimeMillis() / 15) % 360);
			
			Vector vector = location.getDirection().multiply(radius);
			//Vector vector = RandomUtils.getRandomVector().multiply(radius);
			//if (!sphere)
				//vector.setY(Math.abs(vector.getY()));
			//VectorUtils.rotateAroundAxisY(vector, (location.getPitch()) * MathUtils.degreesToRadians);
			//location = entity.getLocation();
			//if (location.getPitch() > -45)
				location.add(vector);
			//else
			
			particle.display(null, location, colour, visibleRange, 0f, 0f, 0f, 0, 1);
			//particle.display(entity.getLocation(), 16, 2,2,2, 24345456f, 1);
			location.subtract(vector);
		}
	}

}