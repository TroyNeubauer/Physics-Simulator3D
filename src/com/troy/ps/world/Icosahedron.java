package com.troy.ps.world;


import java.util.*;

import com.troyberry.logging.*;
import com.troyberry.logging.Timer;
import com.troyberry.math.*;
import com.troyberry.noise.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.util.*;

public class Icosahedron {

	private static final double t = (1.0 + Math.sqrt(5.0)) / 2.0;

	private List<Vector3f> vertices;
	private List<Vector3i> indices;
	private List<Vector3f> colors;

	private List<Vao> givenVaos;
	
	private long seed;
	
	private SimplexNoise noise;

	public Icosahedron(int initalRecursion, double radius, long seed) {
		this.vertices = new ArrayList<Vector3f>(getVertexCount(initalRecursion));
		this.indices = new ArrayList<Vector3i>(20 * Maths.pow(4, initalRecursion));
		this.givenVaos = new ArrayList<Vao>();
		this.seed = seed;
		noise = new SimplexNoise(10.0, 1.1, seed);
		Timer t = new Timer();
		setup(initalRecursion, (float)radius);
		t.stop();
		System.out.println("gen time " + t.getTime());
	}
	
	public double getAdd(SimplexNoise noise, double x, double y, double z, double maxDistance) {
		double value = noise.getNoise(x, y, z);// It will be in +- this range
		double range = noise.getLargestFeature() * noise.getPersistence();// Get a noise value
		value += range;//Change from -range-range to 0-2 * range
		value /= 2.0;//Change from 0-2 * range to 0-range
		value /= range;//Change from 0-range to 0-1
		value *= maxDistance;//Change from 0-1 to 0-maxDistance
		return value;//Return the finalized value
	}

	public int add(Vector3f vec, float length) {
		vec.normaliseWithMultiplier(length);
		int index = vertices.size();
		vertices.add(vec);
		return index;
	}

	private void setup(int initalRecursion, float length) {
		add(new Vector3f(-1, t, 0), length);
		add(new Vector3f(1, t, 0), length);
		add(new Vector3f(-1, -t, 0), length);
		add(new Vector3f(1, -t, 0), length);

		add(new Vector3f(0, -1, t), length);
		add(new Vector3f(0, 1, t), length);
		add(new Vector3f(0, -1, -t), length);
		add(new Vector3f(0, 1, -t), length);

		add(new Vector3f(t, 0, -1), length);
		add(new Vector3f(t, 0, 1), length);
		add(new Vector3f(-t, 0, -1), length);
		add(new Vector3f(-t, 0, 1), length);

		// create 20 triangles of the icosahedron

		// 5 indices around point 0
		indices.add(new Vector3i(0, 11, 5));
		indices.add(new Vector3i(0, 5, 1));
		indices.add(new Vector3i(0, 1, 7));
		indices.add(new Vector3i(0, 7, 10));
		indices.add(new Vector3i(0, 10, 11));

		// 5 adjacent indices 
		indices.add(new Vector3i(1, 5, 9));
		indices.add(new Vector3i(5, 11, 4));
		indices.add(new Vector3i(11, 10, 2));
		indices.add(new Vector3i(10, 7, 6));
		indices.add(new Vector3i(7, 1, 8));

		// 5 indices around point 3
		indices.add(new Vector3i(3, 9, 4));
		indices.add(new Vector3i(3, 4, 2));
		indices.add(new Vector3i(3, 2, 6));
		indices.add(new Vector3i(3, 6, 8));
		indices.add(new Vector3i(3, 8, 9));

		// 5 adjacent indices 
		indices.add(new Vector3i(4, 9, 5));
		indices.add(new Vector3i(2, 4, 11));
		indices.add(new Vector3i(6, 2, 10));
		indices.add(new Vector3i(8, 6, 7));
		indices.add(new Vector3i(9, 8, 1));

		for (int i = 0; i < initalRecursion; i++) {
			List<Vector3i> newindices = new ArrayList<Vector3i>();
			for (Vector3i triangle : indices) {
				Vector3f p1 = vertices.get(triangle.x);
				Vector3f p2 = vertices.get(triangle.y);
				Vector3f p3 = vertices.get(triangle.z);
				Vector3f n1 = Vector3f.lerp(p1, p2, 0.5f);
				Vector3f n2 = Vector3f.lerp(p2, p3, 0.5f);
				Vector3f n3 = Vector3f.lerp(p3, p1, 0.5f);

				int n1loc = add(n1, length);
				int n2loc = add(n2, length);
				int n3loc = add(n3, length);

				newindices.add(new Vector3i(triangle.x, n1loc, n3loc));
				newindices.add(new Vector3i(n3loc, n1loc, n2loc));
				newindices.add(new Vector3i(n1loc, triangle.y, n2loc));
				newindices.add(new Vector3i(n3loc, n2loc, triangle.z));
			}
			indices = newindices;
		}

		List<Vector3f> newVertices = new ArrayList<Vector3f>(vertices.size());
		colors = new ArrayList<Vector3f>(vertices.size());
		double range = 10.0;
		double mult = 20.0;
		SimplexNoise noise = new SimplexNoise(range, 1.1, new Random().nextLong());

		for (Vector3f vertex : vertices) {
			double value = noise.getNoise(vertex.x / 100.0, vertex.y / 100.0, vertex.z / 100.0);
			value += range;
			value = Maths.clamp(0.0, range * 2, value);
			value /= (range * 2);
			double origionalValue = value;
			value /= mult;
			value += 0.9;

			newVertices.add(Vector3f.scale(vertex, (float) value));
			colors.add(new Vector3f(0.1f, (float) origionalValue, (float) origionalValue));
		}

		vertices = newVertices;
	}
	
	public Vao getEntireMesh() {
		Vao vao = Vao.create();
		givenVaos.add(vao);
		vao.createIndexBuffer(ArrayUtil.toIntArrayFromVec3(indices));
		vao.createAttribute(0, ArrayUtil.toFloatArrayFromVec3f(vertices), 3, false, "positions");
		vao.createAttribute(1, ArrayUtil.toFloatArrayFromVec3f(colors), 3, false, "colors");
		
		return vao;
	}

	public void cleanUp() {
		for (Vao vao : givenVaos) {
			vao.delete();
		}
	}

	public static int getVertexCount(int recursion) {
		switch (recursion) {
		case 0:
			return 12;
		case 1:
			return 72;
		case 2:
			return 312;
		case 3:
			return 1272;
		case 4:
			return 5112;
		case 5:
			return 20472;
		case 6:
			return 81912;
		case 7:
			return 327672;

		}
		return 10000;
	}

}
