package me.plasmarob.bending.firebending;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class CombustionEffect extends Effect {
	
	//static final ParticleEffect flame = ParticleEffect.CLOUD;
	
	private Vector vec;
	private Location loc;
	static Vector v = new Vector(0,0,0);
	
	public CombustionEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		
		this.loc = location;
		loc.add(0,-.4,0);
		setLocation(loc);
		vec = loc.getDirection();
		
		type = EffectType.REPEATING;
		iterations = 27;
		period = 1;
	}
	
	@Override
	public void onRun() {
		for (int i = 0; i < 12; i++)
		{
			loc.add(vec.normalize().multiply(0.15));
			//flame.display(v, 0, loc, visibleRange);
			ParticleEffect.REDSTONE.display(null, loc, Color.GRAY, visibleRange, 0f, 0f, 0f, 0, 1);
			//ParticleEffect.SPELL_MOB.display(null, loc, Color.FUCHSIA, visibleRange, 0f, 0f, 0f, 0, 1);
		}	
	}
}