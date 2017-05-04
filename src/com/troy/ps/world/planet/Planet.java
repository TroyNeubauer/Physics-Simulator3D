package com.troy.ps.world.planet;

import java.util.*;
import java.util.Map.*;

import com.troy.ps.main.*;
import com.troy.ps.world.*;
import com.troyberry.logging.Timer;
import com.troyberry.math.*;
import com.troyberry.noise.*;
import com.troyberry.opengl.mesh.*;
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

	/**
	 * Temporary cache of edge midpoints used during subdivision to ensure vertices are not duplicated.
	 */
	private Hashtable<Long, Integer> midPointsCache;

	private ArrayList<Vector3d> vertices;
	private ArrayList<Face> faces;
	private ArrayList<Vector3f> colors;
	private ArrayList<Vector3f> normals;

	private boolean dataNeedsUpdating = true;

	private ArrayList<Vao> givenVaos;

	private long seed;

	private SimplexNoise noise;
	private Vao mesh;

	private float radius, rockeyness, maxMountain;

	public Planet(Vector3f position, long seed, int initalRecursion) {
		this(position, new Vector3f(), new Vector3f(), new Vector3f(), seed, initalRecursion);
	}

	public Planet(Vector3f position, Vector3f velocity, Vector3f rotation, Vector3f rotationVelocity, long seed, int initalRecursion) {
		super(position, velocity, rotation, rotationVelocity);
		this.radius = Maths.randRange(Constants.TEN_KILOMETERS, Constants.ONE_THOUSAND_KILOMETERS * 30.0f);//between 10KM and 15,000KM
		this.rockeyness = Maths.randRange(0.5f, 1.6f);
		this.midPointsCache = new Hashtable<Long, Integer>(25 * Maths.pow(4, initalRecursion), 0.8f);
		this.vertices = new ArrayList<Vector3d>(getVertexCount(initalRecursion));
		this.faces = new ArrayList<Face>(20 * Maths.pow(4, initalRecursion));
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

		System.out.println(i);
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

		result[1] = new Vector3f(0, 1 * radius, 0);

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
		faces.add(new Face(new Vector3i(0, 11, 5)));
		faces.add(new Face(new Vector3i(0, 5, 1)));
		faces.add(new Face(new Vector3i(0, 1, 7)));
		faces.add(new Face(new Vector3i(0, 7, 10)));
		faces.add(new Face(new Vector3i(0, 10, 11)));

		// 5 adjacent indices 
		faces.add(new Face(new Vector3i(1, 5, 9)));
		faces.add(new Face(new Vector3i(5, 11, 4)));
		faces.add(new Face(new Vector3i(11, 10, 2)));
		faces.add(new Face(new Vector3i(10, 7, 6)));
		faces.add(new Face(new Vector3i(7, 1, 8)));

		// 5 indices around point 3
		faces.add(new Face(new Vector3i(3, 9, 4)));
		faces.add(new Face(new Vector3i(3, 4, 2)));
		faces.add(new Face(new Vector3i(3, 2, 6)));
		faces.add(new Face(new Vector3i(3, 6, 8)));
		faces.add(new Face(new Vector3i(3, 8, 9)));

		// 5 adjacent indices 
		faces.add(new Face(new Vector3i(4, 9, 5)));
		faces.add(new Face(new Vector3i(2, 4, 11)));
		faces.add(new Face(new Vector3i(6, 2, 10)));
		faces.add(new Face(new Vector3i(8, 6, 7)));
		faces.add(new Face(new Vector3i(9, 8, 1)));

		for (int i = 0; i < initalRecursion; i++) {
			for (int index = 0; index < faces.size();) {
				index = divide(index);
			}
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

	int i = 0;

	/**
	 * Create the middle vertex between two vertices if it doesn't already exist.
	 * Accesses BSimSphereMesh's hashed midpoint cache to ensure that vertices are
	 * not duplicated. Symmetry means that multiple faces will share midpoint
	 * vertices so this saves time and storage.
	 * @param p1 First vertex index.
	 * @param p2 Second vertex index.
	 */
	protected int getMiddle(int p1, int p2) {
		// Do we already have this midpoint stored from another face?
		boolean firstIsSmaller = p1 < p2;
		long smallerIndex = firstIsSmaller ? p1 : p2;
		long greaterIndex = firstIsSmaller ? p2 : p1;
		// Hashtable key composed of the two (ordered) vertex indices.
		Long key = (smallerIndex << 32) + greaterIndex;

		// If this midpoint is already cached then return its index.
		if (midPointsCache.containsKey(key)) {
			i++;
			return midPointsCache.get(key);
		}

		// Otherwise, create the midpoint.
		Vector3d middle = Vector3d.addAndSetLength(vertices.get(p1), vertices.get(p2), radius);

		// Add the midpoint to the mesh vertices.
		int i = add(middle);

		// Cache the index of the new midpoint.
		midPointsCache.put(key, i);

		return i;
	}

	/**
	 * Removes the old face at index then Adds the 4 new subdivided faces for the old face
	 * @param face The face to subdivide
	 * @param index The index to put the 4 new faces into
	 * @return The index after the new faces have been added
	 */
	public int divide(int index) {
		Vector3i face = faces.get(index).face;
		int n1loc = getMiddle(face.x, face.y);
		int n2loc = getMiddle(face.y, face.z);
		int n3loc = getMiddle(face.z, face.x);

		faces.remove(index);

		faces.add(index++, new Face(new Vector3i(face.x, n1loc, n3loc)));
		faces.add(index++, new Face(new Vector3i(n3loc, n1loc, n2loc)));
		faces.add(index++, new Face(new Vector3i(n1loc, face.y, n2loc)));
		faces.add(index++, new Face(new Vector3i(n3loc, n2loc, face.z)));

		return index;
	}

	public void reGenerate(Vector3d position, double minDistance, int times) {
		System.out.println("generating...");
		position = Vector3d.normalise(position);
		List<Face> localFaces = new ArrayList<Face>();
		for (Face face : faces) {
			System.out.println("checking face");
			double distance = Maths.minTriangleDistance(vertices.get(face.face.x), vertices.get(face.face.y), vertices.get(face.face.z), position);
			if (distance <= minDistance) {
				System.out.println("loading inital face");
				localFaces.add(face);
			}

		}
		for(int time = 0; time < times; time++) {
			for(int i = 0; i < localFaces.size(); i++) {
				Face face = localFaces.get(i);
				double distance = Maths.minTriangleDistance(vertices.get(face.face.x), vertices.get(face.face.y), vertices.get(face.face.z), position);
				if (distance <= minDistance) {
					int n1loc = getMiddle(face.face.x, face.face.y);
					int n2loc = getMiddle(face.face.y, face.face.z);
					int n3loc = getMiddle(face.face.z, face.face.x);

					localFaces.remove(i);

					localFaces.add(i++, new Face(new Vector3i(face.face.x, n1loc, n3loc)));
					localFaces.add(i++, new Face(new Vector3i(n3loc, n1loc, n2loc)));
					localFaces.add(i++, new Face(new Vector3i(n1loc, face.face.y, n2loc)));
					localFaces.add(i, new Face(new Vector3i(n3loc, n2loc, face.face.z)));
				}
			}
		}
		faces.addAll(localFaces);

		for (int i = colors.size(); i < vertices.size(); i++)
			colors.add(color);
		dataNeedsUpdating = true;
	}

	private List<Vector3i> getFaces(Vector3f direction, double radius) {
		List<Vector3i> result = new ArrayList<Vector3i>();
		return result;
	}

	private Vao getEntireMesh() {

		if (dataNeedsUpdating) calculateNormals();

		Vao vao = Vao.create();
		givenVaos.add(vao);
		vao.createIndexBuffer(LoaderUtils.createIndices(faces));
		vao.createAttribute(0, LoaderUtils.createDataBufferFloat(vertices), 3, false, "positions");
		vao.createAttribute(1, LoaderUtils.createDataBuffer(colors), 3, false, "colors");
		vao.createAttribute(2, LoaderUtils.createDataBuffer(normals), 3, false, "normals");

		return vao;
	}

	private void calculateNormals() {
		normals = new ArrayList<Vector3f>(vertices.size());
		for (int i = 0; i < vertices.size(); i++)
			normals.add(new Vector3f());

		for (Face face : faces) {
			Vector3f normal = Maths.calculateNormalFloat(vertices.get(face.face.x), vertices.get(face.face.y), vertices.get(face.face.z));
			normals.get(face.face.x).add(normal);
			normals.get(face.face.y).add(normal);
			normals.get(face.face.z).add(normal);
		}

		for (int i = 0; i < normals.size(); i++)
			normals.get(i).normalise();

		dataNeedsUpdating = false;
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

	public void compress() {
		this.mesh = getEntireMesh();
		vertices.trimToSize();
		faces.trimToSize();
		colors.trimToSize();
	}

}
