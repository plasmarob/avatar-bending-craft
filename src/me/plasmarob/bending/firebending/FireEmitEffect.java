package me.plasmarob.bending.firebending;

import java.util.Random;



import org.bukkit.Location;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class FireEmitEffect extends Effect{
	
	static final ParticleEffect flame = ParticleEffect.FLAME;
	static final ParticleEffect smoke = ParticleEffect.SMOKE_LARGE;
	BlockIterator bit;
	private Vector vec;
	static Vector blank = new Vector();
	private Location loc;
	double mult = 0.15;
	int c = 16;
	Random r;
	double x,y,z;
	double b = 1;
	int downward = 1;
	int emitType = 1;
	
	/*
	public FireEmitEffect(EffectManager em, Location l, double m, int i, int n) {
		
		this( em, l );
		
		mult = m;
		iterations = i;
		c = n;
	}
	*/
	
	public FireEmitEffect(EffectManager effectManager, Location location, int etype) {
		super(effectManager);
		emitType = etype;
		this.loc = location;
		
		if (emitType == 2)
		{
			loc.add(0,-1.4,0);
			setLocation(loc);
			vec = loc.getDirection().setY(0);
			loc.add(vec.normalize().multiply(1.5));
			downward = 0;
		}
		else
		{
			loc.add(0,-.3,0);
			setLocation(loc);
			vec = loc.getDirection();
			loc.add(vec.normalize().multiply(1.5));
		}
		
		type = EffectType.REPEATING;
		iterations = 2;
		period = 1;
		r = new Random();
	}
	

	
	@Override
	public void onRun() {
		for (int i = 0; i < c; i++)
		{
			loc.add(vec.normalize().multiply(mult));
			mult -= 0.01;
			for (int t = 0; t < 4; t++)
			{
				x = (r.nextDouble()-0.5)*0.1*b;
				y = (r.nextDouble()-0.5*downward)*0.1*b*(0.5+0.5*downward);
				z = (r.nextDouble()-0.5)*0.1*b;
				flame.display(new Vector(), 0, loc.clone().add(x,y,z), visibleRange);
			}
			smoke.display(blank, 0, loc.clone().add(x,y,z), visibleRange);
			b += 0.35;
		}
		
	}
}
