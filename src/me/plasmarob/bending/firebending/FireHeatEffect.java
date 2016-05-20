package me.plasmarob.bending.firebending;

import org.bukkit.Location;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;

public class FireHeatEffect extends Effect {

	public FireHeatEffect(EffectManager effectManager, Location location) {
		super(effectManager);
		setLocation(location);
	}

	@Override
	public void onRun() {
		// TODO Auto-generated method stub

	}

}
