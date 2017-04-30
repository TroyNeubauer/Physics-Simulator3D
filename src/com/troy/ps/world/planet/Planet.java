package com.troy.ps.world.planet;

import java.util.*;

import com.troy.ps.main.*;
import com.troy.ps.world.*;
import com.troyberry.logging.Timer;
import com.troyberry.math.*;
import com.troyberry.noise.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;
import com.troyberry.util.Interpolation.*;

public class Planet extends Body {

	private static CustomKeyFrameManager<Range> planetRadiusToMountainHeightMultaplier = new CustomKeyFrameManager<Range>(InterpolationType.COSINE);

	private static Vector3f color = new Vector3f(1, 0, 1);
	private static Vector3f color2 = new Vector3f(0, 0, 1);

	static {
		planetRadiusToMountainHeightMultaplier.addFrame(Double.MIN_VALUE, new Range(0.0, Constants.ONE_MILLIMETER));
		planetRadiusToMountainHeightMultaplier.addFrame(Constants.ONE_KILOMETER, new Range(0.0, Constants.ONE_CENTIMETER));
		planetRadiusToMountainHeightMultaplier.addFrame(Constants.ONE_HUNDRED_KILOMETERS, new Range(0.0, Constants.ONE_METER * 2.0));
		planetRadiusToMountainHeightMultaplier.addFrame(Constants.ONE_THOUSAND_KILOMETERS, new Range(0.0, Constants.ONE_METER * 10.0));
		planetRadiusToMountainHeightMultaplier.addFrame(Constants.TEN_THOUSAND_KILOMETERS, new Range(0.0, Constants.ONE_METER * 20.0));
	}

	private static final double t = (1.0 + Math.sqrt(5.0)) / 2.0;

	private List<Vector3d> vertices;
	private List<Vector3i> indices;
	private List<Vector3f> colors;

	private List<Vao> givenVaos;

	private long seed;

	private SimplexNoise noise;
	private Vao mesh;

	private float radius, rockeyness, maxMountain;

	public Planet(Vector3f position, Vector3f velocity, Vector3f rotation, Vector3f rotationVelocity, long seed, int initalRecursion) {
		super(position, velocity, rotation, rotationVelocity);
		this.radius = Maths.randRange(Constants.TEN_KILOMETERS, Constants.ONE_THOUSAND_KILOMETERS * 30.0f);//between 10KM and 15,000KM
		this.radius = 10;
		this.rockeyness = Maths.randRange(0.5f, 1.6f);

		this.vertices = new ArrayList<Vector3d>(getVertexCount(initalRecursion));
		this.indices = new ArrayList<Vector3i>(20 * Maths.pow(4, initalRecursion));
		this.givenVaos = new ArrayList<Vao>();
		this.seed = seed;

		this.maxMountain = Math.abs(Maths.randRangeFloat(planetRadiusToMountainHeightMultaplier.getValue(radius)));
		System.out.println("radius " + Constants.getDistanceText(radius) + ", max Mt." + Constants.getDistanceText(maxMountain) + ", rockeyness " + rockeyness);
		this.noise = new SimplexNoise(maxMountain, rockeyness, seed);

		Timer t = new Timer();
		setup(initalRecursion, (float) radius);
		t.stop();
		System.out.println("gen time " + t.getTime());
		this.mesh = getEntireMesh();
	}

	public void update(float delta) {
		this.position.add(Vector3f.scale(velocity, delta));
		this.rotation.add(Vector3f.scale(rotationVelocity, delta));
	}

	public Vector3f[] findSutableSpawnLocation() {
		Vector3f[] result = new Vector3f[2];
		double a = Maths.randRange(-Math.PI, Math.PI);
		double b = Maths.randRange(-Math.PI / 2.0, Math.PI / 2.0);
		result[1] = new Vector3f(radius * Maths.cosFloat(b) * Maths.sinFloat(a), radius * Maths.cosFloat(b) * Maths.cosFloat(a), radius * Maths.sinFloat(b));
		result[0] = Vector3f.add(result[1], position);
		result[0] = Vector3f.addLength(result[0], Constants.ONE_METER);
		result[1].normalise();
		return result;
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

	public int add(Vector3d vec) {
		int index = vertices.size();
		vertices.add(vec);
		return index;
	}

	private void setup(int initalRecursion, double length) {
		add(new Vector3d(-1, t, 0).setLength(length));
		add(new Vector3d(1, t, 0).setLength(length));
		add(new Vector3d(-1, -t, 0).setLength(length));
		add(new Vector3d(1, -t, 0).setLength(length));

		add(new Vector3d(0, -1, t).setLength(length));
		add(new Vector3d(0, 1, t).setLength(length));
		add(new Vector3d(0, -1, -t).setLength(length));
		add(new Vector3d(0, 1, -t).setLength(length));

		add(new Vector3d(t, 0, -1).setLength(length));
		add(new Vector3d(t, 0, 1).setLength(length));
		add(new Vector3d(-t, 0, -1).setLength(length));
		add(new Vector3d(-t, 0, 1).setLength(length));

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
				Vector3d p1 = vertices.get(triangle.x);
				Vector3d p2 = vertices.get(triangle.y);
				Vector3d p3 = vertices.get(triangle.z);

				Vector3d n1 = Vector3d.addAndSetLength(p1, p2, length);
				Vector3d n2 = Vector3d.addAndSetLength(p2, p3, length);
				Vector3d n3 = Vector3d.addAndSetLength(p3, p1, length);

				int n1loc = add(n1);
				int n2loc = add(n2);
				int n3loc = add(n3);

				newindices.add(new Vector3i(triangle.x, n1loc, n3loc));
				newindices.add(new Vector3i(n3loc, n1loc, n2loc));
				newindices.add(new Vector3i(n1loc, triangle.y, n2loc));
				newindices.add(new Vector3i(n3loc, n2loc, triangle.z));
			}
			indices = newindices;
		}

		List<Vector3d> newVertices = new ArrayList<Vector3d>(vertices.size());
		colors = new ArrayList<Vector3f>(vertices.size());
		double range = 10.0;
		double mult = 20.0;

		for (Vector3d vertex : vertices) {
			double value = noise.getNoise(vertex.x / 1000.0, vertex.y / 1000.0, vertex.z / 1000.0);
			value += range;
			value = Maths.clamp(0.0, range * 2, value);
			value /= (range * 2);
			double origionalValue = value;

			origionalValue /= 2.0;
			origionalValue += 0.25;

			//newVertices.add(Vector3d.scale(vertex, (float) value));
			colors.add(color2);
		}

		//vertices = newVertices;
	}

	public void reGenerate(Vector3d position, double minDotProduct) {
		for (int i = 0; i < indices.size(); i++) {
			Vector3i triangle = indices.get(i);
			Vector3d p1 = vertices.get(triangle.x);
			Vector3d p2 = vertices.get(triangle.y);
			Vector3d p3 = vertices.get(triangle.z);
			int count = 0;
			if (Vector3d.dot(p1, position) >= minDotProduct) count++;
			if (Vector3d.dot(p2, position) >= minDotProduct) count++;
			if (Vector3d.dot(p3, position) >= minDotProduct) count++;
			if (count >= 0) {
				Vector3d n1 = Vector3d.addAndSetLength(p1, p2, radius);
				Vector3d n2 = Vector3d.addAndSetLength(p2, p3, radius);
				Vector3d n3 = Vector3d.addAndSetLength(p3, p1, radius);
				indices.remove(i);

				int n1loc = add(n1);
				int n2loc = add(n2);
				int n3loc = add(n3);

				indices.add(i++, new Vector3i(triangle.x, n1loc, n3loc));
				indices.add(i++, new Vector3i(n3loc, n1loc, n2loc));
				indices.add(i++, new Vector3i(n1loc, triangle.y, n2loc));
				indices.add(i, new Vector3i(n3loc, n2loc, triangle.z));
			}
		}
		for (int i = colors.size(); i < vertices.size(); i++)
			colors.add(color);
		this.mesh = getEntireMesh();
	}

	private List<Vector3i> getFaces(Vector3f direction, double radius) {
		List<Vector3i> result = new ArrayList<Vector3i>();
		return result;
	}

	private Vao getEntireMesh() {
		Vao vao = Vao.create();
		givenVaos.add(vao);
		vao.createIndexBuffer(ArrayUtil.toIntArrayFromVec3(indices));
		vao.createAttribute(0, ArrayUtil.toFloatArrayFromVec3d(vertices), 3, false, "positions");
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

	public float getRadius() {
		return radius;
	}

	public Vao getMesh() {
		return mesh;
	}

}
