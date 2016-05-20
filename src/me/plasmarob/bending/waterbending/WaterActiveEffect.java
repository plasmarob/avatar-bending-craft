package me.plasmarob.bending.waterbending;

//import java.util.ArrayList;
//import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;

public class WaterActiveEffect extends Effect {
	
	private Vector vec;
	private Location loc;
	Color colour;
	/*
	List<Player> players = new ArrayList<Player>();
	Color color = Color.BLUE;
	//float offsetX = (float)color.getRed() / 255;
	float offsetX = ((float)color.getRed() / 255 >= Float.MIN_NORMAL) ? (float)color.getRed() / 255 : Float.MIN_NORMAL;
	float offsetY = (float)color.getGreen() / 255;
	float offsetZ = (float)color.getBlue() / 255;
	*/
	
	public WaterActiveEffect(EffectManager effectManager, Player player, Color color) {
		super(effectManager);
		colour = color;
		this.loc = player.getEyeLocation();
		loc.add(0,-.4,0);
		setLocation(loc);
		vec = loc.getDirection();
		//players.add(player);
		type = EffectType.INSTANT;
		visibleRange = (float) 1.5;
		// required defaulter for color logic above
		//if (offsetX < Float.MIN_NORMAL)
	    //    offsetX = Float.MIN_NORMAL;
	}
	
	@Override
	public void onRun() {
		for (int i = 0; i < 3; i++)
		{
			loc.add(vec.normalize().multiply(0.15));
			//light ice blue color
			ParticleEffect.REDSTONE.display(null, loc, colour, visibleRange, 0f, 0f, 0f, 0, 1);
			//ParticleEffect.SPELL_MOB.display(null, loc, Color.FUCHSIA, visibleRange, 0f, 0f, 0f, 0, 1);
			//ParticleEffect.REDSTONE.display(offsetX, offsetY, offsetZ, 0, 1, loc, players);
			//public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players)
		}	
	}
}