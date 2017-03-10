package com.troy.ps.world;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class World {
	
	private Vao vao;

	public World() {
		
		float[] initPos = new float[OpenCLManager.MAX_PARTICLES * 4];
		float posRange = 100;
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES / 2; i++) {
			// distribute the particles in a random circle around z axis
			/**
			float distance = Maths.randRange(0.001f, 500.5f);
			float x = distance * Maths.sinFloat(i / (float)OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 2);
			float y = distance * Maths.cosFloat(i / (float)OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 2);
			initPos[i * 4 + 0] = x;
			initPos[i * 4 + 1] = y;
			initPos[i * 4 + 2] = -1000.0f;
			initPos[i * 4 + 3] = 0.0f;
			*/
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange) + 200;
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = -1000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		
		for (int i = OpenCLManager.MAX_PARTICLES / 2; i < OpenCLManager.MAX_PARTICLES; i++) {
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange) - 200;
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = -1000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		float range = 500.0f;
		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f vec = new Vector3f(initPos[i * 4 + 0], initPos[i * 4 + 1], 0);
			Vector3f copy = new Vector3f(vec);
			vec.rotate(new Vector3f(0, 0, 1), (float)Math.toRadians(90));
			vec.scale(0.135f);
			float scale = copy.length() / 200;
			scale = Math.max(1, scale);
			
			initVel[i * 4 + 0] = vec.x / scale;
			initVel[i * 4 + 1] = vec.y / scale;
			initVel[i * 4 + 2] = 0;
			initPos[i * 4 + 3] = 0.0f;
		}
		
		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initColor[i * 4] = Maths.clamp(0, 1, ((initPos[i * 4 + 1] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 1] = 0.2f;
			initColor[i * 4 + 2] = 0.4f;
			initColor[i * 4 + 3] = 1.0f;
		}
		initColor[0] = 0.2f;
		initColor[1] = 1.0f;
		initColor[2] = 0.5f;
		initColor[3] = 1.0f;
		
		
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
