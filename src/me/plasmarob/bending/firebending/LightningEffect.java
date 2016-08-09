package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
import me.plasmarob.bending.util.Tools;

public class LightningEffect extends Effect {
	
	static final ParticleEffect flame = ParticleEffect.CRIT_MAGIC;

	Player player;
	Vector blank = new Vector();
	Location target;
	public LightningEffect(EffectManager effectManager, Player player, Location target) {
		super(effectManager);
		this.player = player;
		this.target = target;
		setEntity(player);

		type = EffectType.INSTANT;
		iterations = 1;
		period = 1;
	}

	@Override
	public void onRun() {
		for (int i = 0; i < 10; i++)
			bolt(player.getLocation(), target, 10, 1);
	}

	
	public void bolt(Location loc1, Location loc2, double s, int scale)
	{
		Location currentLoc = loc1;
		Vector v = Tools.getDirection(loc1, loc2);
		Vector seg = v.multiply(1.0/(s));
		Random rnd = new Random();
		
		Location nextLoc = currentLoc.clone();
		for (int i = 0; i < s; i++)
		{
			nextLoc.setX(currentLoc.getX() + seg.getX() + ((rnd.nextDouble()-0.5)*2*scale));
			nextLoc.setY(currentLoc.getY() + seg.getY() + ((rnd.nextDouble()-0.5)*2*scale));
			nextLoc.setZ(currentLoc.getZ() + seg.getZ() + ((rnd.nextDouble()-0.5)*2*scale));
			
			showSegment(currentLoc, nextLoc);
			
			currentLoc = nextLoc.clone();
		}
	}


	private void showSegment(Location loc1, Location loc2) {
		
		Vector v = Tools.getDirection(loc1, loc2);
		
		int count = (int) (v.length() / 0.05);
		for (int i = 0; i < count; i++)
		{
			loc1 = loc1.add(v.multiply(0.5));
			flame.display(blank, 0, loc1, visibleRange);
		}
		
		
	}
	
	// Function bolt(bx1,by1,bx2,by2,s,bscale,bred,bgreen,bblue)
		// Color bred,bgreen,bblue
		// x=bx2
		// y=by2
		// xstep=(bx1-bx2)/s
		// ystep=(by1-by2)/s
		// LockBuffer BackBuffer()
		// For i=1 To s-1
			// r1=Rnd(-bscale,bscale)
			// r2=Rnd(-bscale,bscale)
			// x2=(x+xstep)+r1
			// y2=(y+ystep)+r2
			// Line x,y,x2,y2
			//x=x2
			//y=y2
		// Next
		// Line x,y,bx1,by1
		// UnlockBuffer BackBuffer()
	// End Function

}
