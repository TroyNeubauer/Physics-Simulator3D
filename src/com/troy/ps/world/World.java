package com.troy.ps.world;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class World {
	
	private Vao vao;

	public World() {
		
		float[] initPos = new float[OpenCLManager.MAX_PARTICLES * 4];
		float posRange = 60;
		float zRange = 100;
		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 0; i < OpenCLManager.MAX_PARTICLES / 4 * 1; i++) {
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
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) -3000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		
		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 1; i < OpenCLManager.MAX_PARTICLES / 4 * 2; i++) {
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) -1000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		
		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 2; i < OpenCLManager.MAX_PARTICLES / 4 * 3; i++) {
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange * 30, posRange * 30);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 30;
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) -2000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		
		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 3; i < OpenCLManager.MAX_PARTICLES / 4 * 4; i++) {
			
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 30;
			initPos[i * 4 + 2] = Maths.randRange(-posRange * 30, posRange * 30) -2000.0f;
			initPos[i * 4 + 3] = 0.0f;
			
		}
		float range = 500.0f;
		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f vec = new Vector3f(initPos[i * 4 + 0], initPos[i * 4 + 1], 0);
			Vector3f copy = new Vector3f(vec);
			vec.rotate(new Vector3f(0, 0, 1), (float)Math.toRadians(90));
			vec.scale(0.035f);
			float scale = copy.length() / 200;
			scale = Math.max(1, scale);
			
			initVel[i * 4 + 0] = vec.x / scale * 0;
			initVel[i * 4 + 1] = vec.y / scale * 0;
			initVel[i * 4 + 2] = 0;
			initPos[i * 4 + 3] = 0.0f;
		}
		
		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initColor[i * 4] = Maths.clamp(0, 1, ((initPos[i * 4 + 1] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 1] = Maths.clamp(0, 1, ((initPos[i * 4 + 0] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 2] = 1 - Maths.clamp(0, 1, ((initPos[i * 4 + 0] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 3] = 0.5f;
		}
		
		float[] radius = new float[OpenCLManager.MAX_PARTICLES];
		for(int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			radius[i] = Maths.randRange(1.0f, 5.0f);
		}
		
		
		vao = SphereGenerator.generateSphere(2);
		vao.bind();
		Vbo posVbo = vao.createAttribute(1, initPos, 4, true);
		Vbo colorVbo = vao.createAttribute(2, initColor, 4, true);
		Vbo radiusVbo = vao.createAttribute(3, radius, 1, true);
		
		OpenCLManager.setMem(posVbo, colorVbo, radiusVbo, initVel);
		OpenCLManager.setWorld(this);
	}

	public Vao getVao() {
		return vao;
	}

}
