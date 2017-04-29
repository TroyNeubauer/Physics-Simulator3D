package com.troy.ps.world.planet;

import java.util.*;

import com.troyberry.math.*;
import com.troyberry.noise.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

public class Planet {

	private Vao mesh;

	private Vector3f position;
	private Vector3f velocity;
	private Vector3f rotation, rotationVelocity;
	
	private Quaternion q = new Quaternion();

	private float radius, mass;

	public Planet(Vector3f position, float radius, float mass) {
		this(position, new Vector3f(), new Vector3f(), new Vector3f(), radius, mass);
	}

	public Planet(Vector3f position, Vector3f velocity, Vector3f rotation, Vector3f rotationVelocity, float radius, float mass) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.rotationVelocity = rotationVelocity;
		this.radius = radius;
		this.mass = mass;
		this.mesh = generateBaseSphere(6, radius);
		q.length();
		}

	public void update() {
		this.position.add(Vector3f.scale(velocity, Window.getFrameTimeSeconds()));
		this.rotation.add(Vector3f.scale(rotationVelocity, Window.getFrameTimeSeconds()));
		System.out.println(rotation);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getRotationVelocity() {
		return rotationVelocity;
	}

	public void setRotationVelocity(Vector3f rotationVelocity) {
		this.rotationVelocity = rotationVelocity;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public static List<Vector3f> getVertices() {
		return vertices;
	}

	public static void setVertices(List<Vector3f> vertices) {
		Planet.vertices = vertices;
	}

	public float getRadius() {
		return radius;
	}

	public void setMesh(Vao mesh) {
		this.mesh = mesh;
	}

	private static List<Vector3f> vertices = new ArrayList<Vector3f>();

	public static int add(Vector3f vec, float length) {
		vec.normaliseWithMultiplier(length);
		int index = vertices.size();
		vertices.add(vec);
		return index;
	}

	/**
	 * Generates a VAO containing the vertex positions of a sphere
	 * @param recursionCount - How many recursions should be done
	 * @param length - the radius of the sphere
	 * @return The VAO containing the sphere.
	 */
	public static Vao generateBaseSphere(int recursionLevel, float length) {
		vertices.clear();
		Vao vao = Vao.create();

		float t = (1.0f + (float) Math.sqrt(5.0)) / 2.0f;

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
		List<Vector3i> faces = new ArrayList<Vector3i>();

		// 5 faces around point 0
		faces.add(new Vector3i(0, 11, 5));
		faces.add(new Vector3i(0, 5, 1));
		faces.add(new Vector3i(0, 1, 7));
		faces.add(new Vector3i(0, 7, 10));
		faces.add(new Vector3i(0, 10, 11));

		// 5 adjacent faces 
		faces.add(new Vector3i(1, 5, 9));
		faces.add(new Vector3i(5, 11, 4));
		faces.add(new Vector3i(11, 10, 2));
		faces.add(new Vector3i(10, 7, 6));
		faces.add(new Vector3i(7, 1, 8));

		// 5 faces around point 3
		faces.add(new Vector3i(3, 9, 4));
		faces.add(new Vector3i(3, 4, 2));
		faces.add(new Vector3i(3, 2, 6));
		faces.add(new Vector3i(3, 6, 8));
		faces.add(new Vector3i(3, 8, 9));

		// 5 adjacent faces 
		faces.add(new Vector3i(4, 9, 5));
		faces.add(new Vector3i(2, 4, 11));
		faces.add(new Vector3i(6, 2, 10));
		faces.add(new Vector3i(8, 6, 7));
		faces.add(new Vector3i(9, 8, 1));

		for (int i = 0; i < recursionLevel; i++) {
			List<Vector3i> newFaces = new ArrayList<Vector3i>();
			for (Vector3i triangle : faces) {
				Vector3f p1 = vertices.get(triangle.x);
				Vector3f p2 = vertices.get(triangle.y);
				Vector3f p3 = vertices.get(triangle.z);
				Vector3f n1 = Vector3f.lerp(p1, p2, 0.5f);
				Vector3f n2 = Vector3f.lerp(p2, p3, 0.5f);
				Vector3f n3 = Vector3f.lerp(p3, p1, 0.5f);
				int p1loc = vertices.indexOf(p1);
				int p2loc = vertices.indexOf(p2);
				int p3loc = vertices.indexOf(p3);

				int n1loc = add(n1, length);
				int n2loc = add(n2, length);
				int n3loc = add(n3, length);

				newFaces.add(new Vector3i(p1loc, n1loc, n3loc));
				newFaces.add(new Vector3i(n3loc, n1loc, n2loc));
				newFaces.add(new Vector3i(n1loc, p2loc, n2loc));
				newFaces.add(new Vector3i(n3loc, n2loc, p3loc));
			}
			faces = newFaces;
		}

		List<Vector3f> newVertices = new ArrayList<Vector3f>(vertices.size());
		List<Vector3f> colors = new ArrayList<Vector3f>(vertices.size());
		double range = 10.0;
		double mult = 20.0;
		SimplexNoise noise = new SimplexNoise(range, 1.3, System.nanoTime());

		for (Vector3f vertex : vertices) {
			double value = noise.getNoise(vertex.x, vertex.y, vertex.z);
			value += range;
			value = Maths.clamp(0.0, range * 2, value);
			value /= (range * 2);
			double origionalValue = value;
			value /= mult;
			value += 0.9;

			newVertices.add(Vector3f.scale(vertex, (float) value));
			colors.add(new Vector3f(0.1f, (float)origionalValue, (float)origionalValue));
		}

		vertices = newVertices;

		vao.createIndexBuffer(ArrayUtil.toIntArrayFromVec3(faces));
		vao.createAttribute(0, ArrayUtil.toFloatArrayFromVec3(vertices), 3, false, "positions");
		vao.createAttribute(1, ArrayUtil.toFloatArrayFromVec3(colors), 3, false, "colors");
		return vao;
	}

	public Vao getMesh() {
		return mesh;
	}

	public void cleanUp() {
		mesh.delete();
	}

}
