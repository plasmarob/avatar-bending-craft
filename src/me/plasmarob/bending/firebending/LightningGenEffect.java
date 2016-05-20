package me.plasmarob.bending.firebending;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class LightningGenEffect extends Effect {
	
	static final ParticleEffect flame = ParticleEffect.CRIT_MAGIC;
	double angle = 0;
	float yaw;
	float pitch;
	Player player;
	Vector blank = new Vector();
	
	public LightningGenEffect(EffectManager effectManager, Player player) {
		super(effectManager);
		this.player = player;
		setEntity(player);

		type = EffectType.REPEATING;
		iterations = 360;
		period = 1;
	}

	
	public double getAngle()
	{
		return angle;
	}
	
	@Override
	public void onRun() {
		
		angle += 0.05;
		yaw = player.getEyeLocation().getYaw() + (float)Math.sin(angle)*65;
		pitch = (float)Math.sin(2*angle)*30;
		Location loc = player.getEyeLocation().clone();
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		Vector v = loc.getDirection().normalize();
		
		flame.display(blank, 0, loc.add(v.getX(),v.getY(),v.getZ()), visibleRange);
	}

	

}
