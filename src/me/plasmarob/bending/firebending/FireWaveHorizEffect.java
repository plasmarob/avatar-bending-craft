package me.plasmarob.bending.firebending;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
import me.plasmarob.bending.util.Tools;

public class FireWaveHorizEffect extends Effect{
		
		Double[][] R_mat = new Double[][] {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
	    static final Double[][] I_mat = new Double[][] {{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}};
	    static Double[][] K_mat = new Double[][] {{1.0,2.0,3.0},{4.0,5.0,6.0},{7.0,8.0,9.0}};
		
		double counter;
		float yaw;
		float yawStart;
		float yawEnd;
		float tempYaw;
		double speed;
		static final ParticleEffect flame = ParticleEffect.FLAME;
		Random rand;
		
		Location location;
		int angle = 0;
		
		Location currentLoc; 
		int x = 0;
		
		static Vector blank = new Vector();
		//static Vector blank = new Vector();
		
		
		Location tempLoc;
		
		
		
		
		public FireWaveHorizEffect(EffectManager effectManager, Location location) {
			super(effectManager);
			//location.add(0,-.4,0);
			
			double theta = location.getPitch()*Math.PI/180;
			
			type = EffectType.REPEATING;
			iterations = 10;
			period = 1;
			counter = 0;
			speed = 0.75;

			/*
			location.setYaw(location.clone().getYaw() - 90);
			float pitch = location.getPitch();
			location.setPitch(0);
			Vector vec = location.getDirection().normalize().multiply(2.5);
			location.add(vec);
			location.setYaw(location.clone().getYaw() + 90);
			location.setPitch(pitch);
			this.location = location;
			setLocation(location);
			*/
			
			//Tools.say(location.getDirection().toString());
			
			//get the axis unit vector K around which to rotate for horizontal
			Location unitTemp = location.clone();
			unitTemp.setYaw(unitTemp.getYaw()-90);
			Vector k = unitTemp.getDirection();
			k.setY(0);
			k = k.normalize();
			//Tools.say(k.toString());
			
			//Tools.say(K_mat[0][0] + " " + K_mat[0][1] + " " + K_mat[0][2]);
			K_mat = getCrossMatrix(k);
			//Tools.say(K_mat[0][0] + " " + K_mat[0][1] + " " + K_mat[0][2]);
			
			// Rodrigues' rotation formula
			R_mat = add( I_mat , multiply(Math.sin(theta),K_mat) , multiply(1-Math.cos(theta),K_mat) );
			
			Tools.say(R_mat[0][0] + " " + R_mat[0][1] + " " + R_mat[0][2]);
			Tools.say(R_mat[1][0] + " " + R_mat[1][1] + " " + R_mat[1][2]);
			Tools.say(R_mat[2][0] + " " + R_mat[2][1] + " " + R_mat[2][2]);
			
			//location.setYaw(location.clone().getYaw() - 90);
			this.location = location;
			//location.getDirection().multiply(5);
			setLocation(location);
			
			tempLoc = location.clone();
			tempLoc.setPitch(0);
			tempLoc.setYaw(location.getYaw()-45);
		}

		@Override
		public void onRun() {
			

			for (int i = 0; i < 9; i++)
			{
				Vector v = multiply(R_mat, tempLoc.clone().getDirection().normalize()).multiply(5);
				Location tLoc = location.clone().add(v);
				
				tempLoc.setYaw(tempLoc.getYaw()+1);
				flame.display(blank, 0, tLoc, visibleRange);
			}
			/*
			//location.setYaw(location.clone().getYaw() + 90);	
			
			Vector vec = location.getDirection().normalize().multiply(0.9);
			location.add(vec);	
			
			Location tempLoc = location.clone();
			tempLoc.setYaw(tempLoc.clone().getYaw() + 90);
			tempLoc.setPitch(0);
			vec = tempLoc.getDirection().normalize().multiply(0.3);
			for (int i = 0; i < 20; i++)
			{
				tempLoc.add(vec);
				flame.display(blank, 0, tempLoc, visibleRange);
			}
			//location.subtract(vec);
			*/
		}
		
		public Double[][] getCrossMatrix(Vector k)
		{
			Double[][] mat = new Double[][] {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
			mat[0][0] = 0.0;
			mat[0][1] = -k.getZ();
			mat[0][2] = k.getY();
			mat[1][0] = k.getZ();
			mat[1][1] = 0.0;
			mat[1][2] = -k.getX();
			mat[2][0] = -k.getY();
			mat[2][1] = k.getX();
			mat[2][2] = 0.0;
			return mat.clone();
		}
		
		public Double[][] multiply (Double[][] A, Double[][] B)
		{
			Double[][] M = new Double[][] {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
			for (int i = 0; i < 3; i++)
	            for (int j = 0; j < 3; j++)
	                for (int k = 0; k < 3; k++)
	                    M[i][j] += A[i][k] * B[k][j];
			
			return M.clone();
		}
		
		public Double[][] multiply (double s, Double[][] M)
		{
			for (int i = 0; i < 3; i++)
	            for (int j = 0; j < 3; j++)
	                M[i][j] = s*M[i][j];	
			return M.clone();
		}
		
		public Vector multiply (Double[][] M, Vector v)
		{
			Vector ans = new Vector();
			ans.setX(v.getX()*M[0][0] + v.getY()*M[0][1] + v.getZ()*M[0][2]);
			ans.setY(v.getX()*M[1][0] + v.getY()*M[1][1] + v.getZ()*M[1][2]);
			ans.setZ(v.getX()*M[2][0] + v.getY()*M[2][1] + v.getZ()*M[2][2]);
			return ans.clone();
		}
		
		public Double[][] add (Double[][] A, Double[][] B)
		{
			Double[][] M = new Double[][] {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
			for (int i = 0; i < 3; i++)
	            for (int j = 0; j < 3; j++)
	                M[i][j] = A[i][j] + B[i][j];	
			return M.clone();
		}
		
		public Double[][] add (Double[][] A, Double[][] B, Double[][] C)
		{
			Double[][] M = new Double[][] {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
			for (int i = 0; i < 3; i++)
	            for (int j = 0; j < 3; j++)
	                M[i][j] = A[i][j] + B[i][j] + C[i][j];	
			return M.clone();
		}
		
	}
