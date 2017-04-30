package test;

import com.troyberry.math.*;

public class Triangle {

	protected int[] tVertices = new int[3];

	/** Triangle normal. Assumed to be normalised on creation. */
	protected Vector3d normal = new Vector3d();

	/**
	 * Constructor: New triangular face from three individual vertex indices.
	 */
	public Triangle(int newP1Index, int newP2Index, int newP3Index) {
		tVertices[0] = newP1Index;
		tVertices[1] = newP2Index;
		tVertices[2] = newP3Index;
	}

	/**
	 * Constructor: New triangular face from array of vertex indices.
	 */
	public Triangle(int[] newPoints) {
		// Check that we have the right number of vertices
		assert newPoints.length == 3 : "Error: Triangle requires *three* vertex indices.";

		// Set up the new triangle
		tVertices = newPoints;
	}

	public Triangle(Triangle tri) {
		this(tri.getPoints());
		normal = new Vector3d(tri.getNormal());
	}

	/**
	 * Update the normal vector of this face with a new vector.
	 */
	protected void updateNormal(Vector3d newNormal) {
		normal = newNormal;
	}

	/**
	 * Flip the face normal if you want it to point the other way.
	 */
	protected void flipNormal() {
		normal.negate();
	}

	public Vector3d getNormal() {
		return normal;
	}

	public int getP1() {
		return tVertices[0];
	}

	public int getP2() {
		return tVertices[1];
	}

	public int getP3() {
		return tVertices[2];
	}

	public int[] getPoints() {
		return tVertices;
	}
}
