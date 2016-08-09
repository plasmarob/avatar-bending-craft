package me.plasmarob.bending.airbending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

/**
 * Launches a blast of cloud particles from a person
 * @user AirBlade
 * @author plasmarob
 * @since 0.4.1
 */
public class GustEffect extends Effect{
	
	public ParticleEffect puff = ParticleEffect.CLOUD;
	
	public Location tempLocation;
	//private int counter;
	boolean thin;
	Location location;
	static Vector v;
	float x,y,z;
	Random rand;
	Location temp;
	
	public GustEffect(EffectManager effectManager, Location location, boolean thin) {
		super(effectManager);
		//location.add(location.getDirection().normalize().multiply(1));
		location.add(0,-.4,0);
		type = EffectType.REPEATING;
		iterations = 5;
		period = 1;
		//counter = 0;
		this.thin = thin;
		setLocation(location);
		this.location = location;
		v = location.clone().getDirection();
		temp = location.clone();
	}
	/*
	public GustEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		//location.add(location.getDirection().normalize().multiply(1));
		location.add(0,-.4,0);
		type = EffectType.REPEATING;
		iterations = 15;
		period = 1;
		//counter = 0;
		thin = true;
		setLocation(location);
		this.location = location;
	}
	*/

	@Override
	public void onRun() {
		//counter++;
		//float fac = 0.3f*counter;
		location.getWorld().playSound(location, Sound.ENTITY_HORSE_BREATHE,0.4f, 0.1f);
		
		//tempLocation = location.clone();
		//location.add(location.getDirection().normalize().multiply(1.5));
		
		//make the air gust stop at solid objects
		if (location.getBlock().getType() == Material.FIRE)
			location.getBlock().setType(Material.AIR);
		if (location.getBlock().getType() != Material.AIR)
			this.cancel();
		rand = new Random();
		
		
		for (int i = 0; i < 3; i++) {
			//tempLocation.add(tempLocation.getDirection().normalize().multiply(0.1));
			for (int j = 0; j < 5; j++) {
				//temp = tempLocation.clone();
				if (thin) {
					x = (rand.nextInt() % 3 - 1)/10.0f;
					y = (rand.nextInt() % 3 - 1)/10.0f;
					z = (rand.nextInt() % 3 - 1)/10.0f;
				} else {
					x = (rand.nextInt() % 9 - 5)/10.0f;
					y = (rand.nextInt() % 9 - 5)/10.0f;
					z = (rand.nextInt() % 9 - 5)/10.0f;
				}
				puff.display(v.normalize(), 3.0f, temp.clone().add(x,y,z), visibleRange);
			}
		}
	}
}
