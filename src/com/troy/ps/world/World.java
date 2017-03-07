package com.troy.ps.world;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class World {
	
	private Vao vao;

	public World() {
		
		float[] initPos = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			// distribute the particles in a random circle around z axis
			float distance = Maths.randRange(0.001f, 500.5f);
			float x = distance * Maths.sinFloat(i / (float)OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 2);
			float y = distance * Maths.cosFloat(i / (float)OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 2);
			initPos[i * 4 + 0] = x;
			initPos[i * 4 + 1] = y;
			initPos[i * 4 + 2] = -1000.0f + Maths.randRange(-200f, 200f);
			initPos[i * 4 + 3] = 0.0f;
		}
		float range = 50.0f;
		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f vec = new Vector3f(initPos[i * 3 + 0], 0, initPos[i * 3 + 1]);
			vec.rotate(new Vector3f(0, 1, 0), (float)Math.toRadians(90));
			vec.scale(0.1f);
			
			initVel[i * 4 + 0] = Maths.randRange(-range, range);
			initVel[i * 4 + 1] = Maths.randRange(-range, range);
			initVel[i * 4 + 2] = Maths.randRange(-range / 10, range / 10);
			initPos[i * 4 + 3] = 0.0f;
			/**
			initVel[i * 3 + 0] = vec.x;
			initVel[i * 3 + 1] = vec.z;
			initVel[i * 3 + 2] = 0;
			*/
		}
		
		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initColor[i * 4] = 0.1f;
			initColor[i * 4 + 1] = 0.5f;
			initColor[i * 4 + 2] = 1.0f;
			initColor[i * 4 + 3] = 1.0f;
			if(i == 1 || i == 2) {
				initColor[i * 4] = 1.0f;
				initColor[i * 4 + 1] = 0;
				initColor[i * 4 + 2] = 1.0f;
				initColor[i * 4 + 3] = 1.0f;
			}
		}
		
		
		vao = Vao.create();
		Vbo posVbo = vao.createAttribute(0, initPos, 4);
		Vbo colorVbo = vao.createAttribute(1, initColor, 4);
		vao.setIndexCount(OpenCLManager.MAX_PARTICLES);
		
		OpenCLManager.setMem(posVbo, colorVbo, initVel);
		OpenCLManager.setWorld(this);
	}

	public Vao getVao() {
		return vao;
	}

}
