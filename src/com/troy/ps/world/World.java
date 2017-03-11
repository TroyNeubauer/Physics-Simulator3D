package com.troy.ps.world;

import javax.xml.bind.attachment.*;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class World {

	private Vao vao;

	public World() {

		float[] initPos = new float[OpenCLManager.MAX_PARTICLES * 4];
		float posRange = 100;
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			// distribute the particles in a random circle around z axis

			float distance = Maths.randRange(0.001f, 500.5f);
			float x = distance * Maths.sinFloat(i / (float) OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 8);
			float y = distance * Maths.cosFloat(i / (float) OpenCLManager.MAX_PARTICLES * Maths.SHORT_PI * 8);
			initPos[i * 4 + 0] = x;
			initPos[i * 4 + 1] = y;
			initPos[i * 4 + 2] = -2000.0f;
			initPos[i * 4 + 3] = 0.0f;

			/*
			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = -2000.0f;
			initPos[i * 4 + 3] = 0.0f;
			*/

		}
		float range = 500.0f;
		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f vec = new Vector3f(initPos[i * 4 + 0], initPos[i * 4 + 1], 0);
			Vector3f copy = new Vector3f(vec);
			vec.rotate(new Vector3f(0, 0, 1), (float) Math.toRadians(90));
			vec.negate();
			vec.scale(0.015f);
			float scale = copy.length() / 200;
			scale = Math.max(1, scale);

			initVel[i * 4 + 0] = vec.x / scale;
			initVel[i * 4 + 1] = vec.y / scale;
			initVel[i * 4 + 2] = 0;
			initPos[i * 4 + 3] = 0.0f;
		}

		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initColor[i * 4] = 1.0f;
			initColor[i * 4 + 1] = 0.2f;
			initColor[i * 4 + 2] = 0.4f;
			initColor[i * 4 + 3] = 1.0f / 5.0f;

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
