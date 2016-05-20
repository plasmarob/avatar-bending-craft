package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class FireWaveEffect extends Effect{
	
	double counter;
	float yaw;
	float yawStart;
	float yawEnd;
	float tempYaw;
	double speed;
	private float radius = 1.5f;
	static final ParticleEffect flame = ParticleEffect.FLAME;
	Random rand;
	Location location;
	static Vector blank = new Vector();
	
	public FireWaveEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		location.add(0,-.4,0);
		this.location = location;
		setLocation(location);
		type = EffectType.REPEATING;
		iterations = 3;
		period = 1;
		counter = 0;
		speed = 0.15;
		
		yaw = location.getYaw();
		yawStart = yaw - 50.0f;
		yawEnd = yawStart + 100;
		
		rand = new Random();
	}

	@Override
	public void onRun() {

		for (int i = 0; i < 3; i++)
		{	
			radius += speed;
			
			yaw = yawStart;
			while (yaw < yawEnd)
			{
				radius += rand.nextFloat() % 0.06f - 0.03f;
				
				yaw += 2;
				tempYaw = yaw + (rand.nextInt() % 4 - 2);
				if (tempYaw > 0)
					tempYaw -= 360;
				if (tempYaw <= -360)
					tempYaw += 360;
				
				int pitch = rand.nextInt() % 6 - 3;
				location.setPitch(location.getPitch() + pitch);
				location.setYaw(tempYaw);
				Vector vector = location.getDirection().multiply(radius);
				location.add(vector);
				flame.display(blank, 0, location, visibleRange);
				location.subtract(vector);
				location.setPitch(location.getPitch() - pitch);
			}	
		}
		speed -= 0.01;
	}
}
