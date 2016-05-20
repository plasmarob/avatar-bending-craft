package me.plasmarob.bending.airbending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LocationEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class GustEffect extends LocationEffect{
	
	public ParticleEffect puff = ParticleEffect.CLOUD;
	
	public Location tempLocation;
	//private int counter;
	boolean thin;
	Location location;
	static Vector blank = new Vector();
	
	public GustEffect(EffectManager effectManager, Location location, boolean thin) {
		super(effectManager, location);
		//location.add(location.getDirection().normalize().multiply(1));
		location.add(0,-.4,0);
		type = EffectType.REPEATING;
		iterations = 15;
		period = 1;
		//counter = 0;
		this.thin = thin;
		setLocation(location);
		this.location = location;
	}
	public GustEffect(EffectManager effectManager, Location location) {
		super(effectManager, location);
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

	@Override
	public void onRun() {
		//counter++;
		//float fac = 0.3f*counter;
		location.getWorld().playSound(location, Sound.HORSE_BREATHE,0.4f, 0.1f);
		tempLocation = location.clone();
		location.add(location.getDirection().normalize().multiply(1.5));
		
		//make the air gust stop at solid objects
		if (location.getBlock().getType() == Material.FIRE)
			location.getBlock().setType(Material.AIR);
		if (location.getBlock().getType() != Material.AIR)
			this.cancel();
		
		float x,y,z;
		Random rand = new Random();
		Location temp;
		for (int i = 0; i < 3; i++)
		{
			tempLocation.add(tempLocation.getDirection().normalize().multiply(0.1));
			
			for (int j = 0; j < 5; j++)
			{
				temp = tempLocation.clone();
				if (thin)
				{
					x = (rand.nextInt() % 3 - 1)/10.0f;
					y = (rand.nextInt() % 3 - 1)/10.0f;
					z = (rand.nextInt() % 3 - 1)/10.0f;
				}
				else
				{
					x = (rand.nextInt() % 9 - 5)/10.0f;
					y = (rand.nextInt() % 9 - 5)/10.0f;
					z = (rand.nextInt() % 9 - 5)/10.0f;
				}
				puff.display(blank, 0, temp.add(x,y,z), visibleRange);
			}
		}
	}
}
