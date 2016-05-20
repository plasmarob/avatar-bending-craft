package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class SmokeEffect extends Effect{
	Location location;
	static final ParticleEffect flame = ParticleEffect.SMOKE_LARGE;
	static Vector blank = new Vector();
	Random rand = new Random();
	int density = 30;
	
	public SmokeEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = 1;
		period = 1;
		this.location = location;
		setLocation(location);
	}

	@Override
	public void onRun() {
		for (int i = 0; i < density; i++)
		{
			Location tLoc = location.clone().add(rand.nextFloat()*1.1, rand.nextFloat()*1.1, rand.nextFloat()*1.1);
			flame.display(blank, 0, tLoc, visibleRange);
		}
	}
}
