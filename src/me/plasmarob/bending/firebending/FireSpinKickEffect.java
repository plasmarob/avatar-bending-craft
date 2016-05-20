package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class FireSpinKickEffect extends Effect{
	
	double counter;
	float yaw;
	float yawStart;
	float yawEnd;
	float tempYaw;
	double speed;
	private float radius = 1.8f;
	private float radiusInit = radius;
	static final ParticleEffect flame = ParticleEffect.FLAME;
	Random rand;
	static Vector blank = new Vector();
	Location location;
	
	public FireSpinKickEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		this.location = location;
		location.add(0,.6f,0);
		
		type = EffectType.REPEATING;
		iterations = 3;
		period = 1;
		counter = 0;
		speed = 0.2;
		
		yaw = location.getYaw();
		yawStart = yaw - 50.0f;
		yawEnd = yawStart + 100;
		
		rand = new Random();
		setLocation(location);
	}

	@Override
	public void onRun() {
		
		for (int i = 0; i < 2; i++)
		{
				
			radiusInit += speed;
			
			yaw = yawStart;
			
			for (int y = 0; y < 360; y+=4)
			{
				radius = radiusInit + rand.nextFloat() % 0.3f;
				
				//int pitch = rand.nextInt() % 6 - 3;
				location.setPitch(0);
				location.setYaw(y);
				Vector vector = location.getDirection().multiply(radius);
				location.add(vector);
				flame.display(blank, 0, location, visibleRange);
				location.subtract(vector);
				location.setPitch(0);
			}
		}
		speed -= 0.01;
	}
}
