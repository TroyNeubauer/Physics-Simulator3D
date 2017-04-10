package com.troy.ps.world;

import java.util.*;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class World {

	private Vao vao;

	public World() {
		//createGalaxy(4, 1000, 1000, 1.01f, -1000, 1000 / 20, 10);
		createPlaneTest();
	}

	//                                 4            1000            50                    1.0f          -1000           50	
	private void createGalaxy(int arms, float radius, float cyclanderRadius, float groupScale, float baseZ, float distIncrement, float zError) {
		float[] initPos = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int arm = 0; arm < arms; arm++) {
			List<Vector3f> points = new ArrayList<Vector3f>();
			float dist = 10;
			double baseAngle = arm * ((Math.PI * 2.0) / arms);
			for (double angle = baseAngle; angle < Math.PI * 2 + baseAngle; angle += (Math.PI * 2) / 20, dist += distIncrement) {
				float x = dist * Maths.sinFloat(angle);
				float y = dist * Maths.cosFloat(angle);
				points.add(new Vector3f(x, y, baseZ));
			}
			int totalForArm = OpenCLManager.MAX_PARTICLES / arms;
			int count = 0;
			while (count < totalForArm) {
				float distance = Maths.randRange(radius * 0.01f, radius);
				float angle = Maths.randRange(0, Maths.SHORT_PI * 2.0f);
				float x = distance * Maths.sinFloat(angle);
				float y = distance * Maths.cosFloat(angle);
				float z = Maths.randRange(-zError, zError) + baseZ;

				boolean inside = false;
				for (int i = 1; i < points.size(); i++) {
					Vector3f min = points.get(i - 1);
					Vector3f max = points.get(i);
					float d = Maths.distanceToSegment(min, max, new Vector3f(x, y, z));
					if (d < (cyclanderRadius * (Maths.pow(groupScale, count)))) {
						inside = true;
						break;
					}
				}

				if (inside) {//Inside area
					initPos[(count + totalForArm * arm) * 4 + 0] = x;
					initPos[(count + totalForArm * arm) * 4 + 1] = y;
					initPos[(count + totalForArm * arm) * 4 + 2] = z;
					initPos[(count + totalForArm * arm) * 4 + 3] = 0.0f;
					count++;
				}
			}

			points.clear();
		}

		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initVel[i * 4 + 0] = 0.0f;
			initVel[i * 4 + 1] = 0.0f;
			initVel[i * 4 + 2] = 0.0f;
			initVel[i * 4 + 3] = 0.0f;
		}

		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f color = Temp2Color.getColor(Maths.randRange(1000, 10000));
			initColor[i * 4] = color.x;
			initColor[i * 4 + 1] = color.y;
			initColor[i * 4 + 2] = color.z;
			initColor[i * 4 + 3] = 1.0f;
		}

		float[] initRadius = new float[OpenCLManager.MAX_PARTICLES];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initRadius[i] = Maths.randRange(1.0f, 50.0f);
		}

		vao = SphereGenerator.generateSphere(2);
		vao.bind();
		Vbo posVbo = vao.createAttribute(1, initPos, 4, true);
		Vbo colorVbo = vao.createAttribute(2, initColor, 4, true);
		Vbo radiusVbo = vao.createAttribute(3, initRadius, 1, true);

		OpenCLManager.setMem(posVbo, colorVbo, radiusVbo, initVel);
		OpenCLManager.setWorld(this);
		System.out.println("all done!");
	}

	private void createPlaneTest() {

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
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) - 3000.0f;
			initPos[i * 4 + 3] = 0.0f;

		}

		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 1; i < OpenCLManager.MAX_PARTICLES / 4 * 2; i++) {

			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 4;
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) - 1000.0f;
			initPos[i * 4 + 3] = 0.0f;

		}

		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 2; i < OpenCLManager.MAX_PARTICLES / 4 * 3; i++) {

			initPos[i * 4 + 0] = Maths.randRange(-posRange * 30, posRange * 30);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 30;
			initPos[i * 4 + 2] = Maths.randRange(-zRange, zRange) - 2000.0f;
			initPos[i * 4 + 3] = 0.0f;

		}

		for (int i = OpenCLManager.MAX_PARTICLES / 4 * 3; i < OpenCLManager.MAX_PARTICLES / 4 * 4; i++) {

			initPos[i * 4 + 0] = Maths.randRange(-posRange, posRange);
			initPos[i * 4 + 1] = Maths.randRange(-posRange, posRange) * 30;
			initPos[i * 4 + 2] = Maths.randRange(-posRange * 30, posRange * 30) - 2000.0f;
			initPos[i * 4 + 3] = 0.0f;

		}
		float range = 500.0f;
		float[] initVel = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			Vector3f vec = new Vector3f(initPos[i * 4 + 0], initPos[i * 4 + 1], 0);
			Vector3f copy = new Vector3f(vec);
			vec.rotate(new Vector3f(0, 0, 1), (float) Math.toRadians(90));
			vec.scale(0.035f);
			float scale = copy.length() / 200;
			scale = Math.max(1, scale);

			initVel[i * 4 + 0] = vec.x / scale * 0;
			initVel[i * 4 + 1] = vec.y / scale * 0;
			initVel[i * 4 + 2] = 0;
			initVel[i * 4 + 3] = 0.0f;
		}

		float[] initColor = new float[OpenCLManager.MAX_PARTICLES * 4];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
			initColor[i * 4] = Maths.clamp(0, 1, ((initPos[i * 4 + 1] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 1] = Maths.clamp(0, 1, ((initPos[i * 4 + 0] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 2] = 1 - Maths.clamp(0, 1, ((initPos[i * 4 + 0] / posRange) + 1.0f) / 2.0f);
			initColor[i * 4 + 3] = 0.5f;
		}

		float[] radius = new float[OpenCLManager.MAX_PARTICLES];
		for (int i = 0; i < OpenCLManager.MAX_PARTICLES; i++) {
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
