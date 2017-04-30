package test;

import java.nio.*;
import java.util.*;

import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.util.*;

public class SphereMesh {

	private static final double t = (1.0 + Math.sqrt(5.0)) / 2.0;

	/** The actual locations (3D coordinates) of all mesh vertices */
	protected ArrayList<Vertex> vertices;

	/** List of faces, each face stores the indices of the vertices which compose that face. */
	protected ArrayList<Triangle> faces;

	private ArrayList<Vector3f> colors;

	/**
	 * Temporary cache of edge midpoints used during subdivision to ensure vertices are not duplicated.
	 */
	private Hashtable<Long, Integer> midPointsCache;

	/**
	 * Number of subdivision and smoothing iterations to perform.
	 */
	private int recursionThreshold;

	private double radius;

	/**
	 * Main constructor. Generates the sphere on the origin with the specified radius
	 * @param center	The desired center coordinates of the sphere in 3D space.
	 * @param radius	The desired sphere radius.
	 * @param subdivisionRecursions	Desired number of subdivision and smoothing iterations
	 * 								(note, no. of faces increases by a factor of 4 for each iteration).
	 */
	public SphereMesh(double radius, int subdivisionRecursions) {
		this.vertices = new ArrayList<Vertex>();
		this.faces = new ArrayList<Triangle>();
		this.recursionThreshold = subdivisionRecursions;
		this.radius = radius;
		createMesh();
		cleanUp(false);
	}

	public Vao getEntireMesh() {
		Vao vao = Vao.create();
		IntBuffer indic = IntBuffer.allocate(faces.size() * 3);
		for (Triangle t : faces) {
			indic.put(t.getP1());
			indic.put(t.getP2());
			indic.put(t.getP3());
		}

		FloatBuffer verts = FloatBuffer.allocate(vertices.size() * 3);
		for (Vertex v : vertices) {
			verts.put((float) v.location.x);
			verts.put((float) v.location.y);
			verts.put((float) v.location.z);
		}
		colors.clear();
		for (int i = 0; i < vertices.size(); i++)
			colors.add(new Vector3f(1, 1, 1));

		vao.createIndexBuffer(indic);
		vao.createAttribute(0, verts, 3, false, "positions");
		vao.createAttribute(1, ArrayUtil.toFloatArrayFromVec3f(colors), 3, false, "colors");

		return vao;
	}

	/**
	 * Defines the vertices and faces for a geodesic sphere.
	 * Initially defines an icosahedron, which is then recursively subdivided and smoothed.
	 */
	protected void createMesh() {

		/*
		 * Add vertices of Icosahedron to main vertex list
		 */
		addVertex(new Vector3d(-1, t, 0, radius));
		addVertex(new Vector3d(1, t, 0, radius));
		addVertex(new Vector3d(-1, -t, 0, radius));
		addVertex(new Vector3d(1, -t, 0, radius));

		addVertex(new Vector3d(0, -1, t, radius));
		addVertex(new Vector3d(0, 1, t, radius));
		addVertex(new Vector3d(0, -1, -t, radius));
		addVertex(new Vector3d(0, 1, -t, radius));

		addVertex(new Vector3d(t, 0, -1, radius));
		addVertex(new Vector3d(t, 0, 1, radius));
		addVertex(new Vector3d(-t, 0, -1, radius));
		addVertex(new Vector3d(-t, 0, 1, radius));

		/*
		 * Add faces to main triangle list
		 */
		// 5 faces around point 0
		addTriangle(0, 11, 5);
		addTriangle(0, 5, 1);
		addTriangle(0, 1, 7);
		addTriangle(0, 7, 10);
		addTriangle(0, 10, 11);

		// 5 adjacent faces
		addTriangle(1, 5, 9);
		addTriangle(5, 11, 4);
		addTriangle(11, 10, 2);
		addTriangle(10, 7, 6);
		addTriangle(7, 1, 8);

		// 5 faces around point 3
		addTriangle(3, 9, 4);
		addTriangle(3, 4, 2);
		addTriangle(3, 2, 6);
		addTriangle(3, 6, 8);
		addTriangle(3, 8, 9);

		// 5 adjacent faces
		addTriangle(4, 9, 5);
		addTriangle(2, 4, 11);
		addTriangle(6, 2, 10);
		addTriangle(8, 6, 7);
		addTriangle(9, 8, 1);

		/*
		 * Subdivision of the icosahedron to an approximate sphere.
		 * For each level of recursion, a temporary list of faces is created which overwrites
		 * the old list upon completion of the subdivision step.
		 * New vertices are appended to the full list. Midpoint vertices are stored with a key
		 * combined from their two 'parent' vertices' indices, thus ensuring no duplication.
		 */
		for (int i = 0; i < this.recursionThreshold; i++) {
			ArrayList<Triangle> tempTriangles = new ArrayList<Triangle>();
			midPointsCache = new Hashtable<Long, Integer>(faces.size() * 2, 0.8f);

			for (Triangle t : faces) {
				// Create the midpoints of the three faces.
				int a = getMiddle(t.getP1(), t.getP2());
				int b = getMiddle(t.getP2(), t.getP3());
				int c = getMiddle(t.getP3(), t.getP1());

				// Create the four new triangles which will replace the original.
				tempTriangles.add(new Triangle(t.getP1(), a, c));
				tempTriangles.add(new Triangle(t.getP2(), b, a));
				tempTriangles.add(new Triangle(t.getP3(), c, b));
				tempTriangles.add(new Triangle(a, b, c));
			}
			// Update the triangle list of the actual mesh.
			faces = tempTriangles;

			midPointsCache.clear();
		}

		// Calculate normals.
		for (Triangle t : faces) {
			computeNormal(t);
		}
	}

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
		if (midPointsCache.containsKey(key)) return midPointsCache.get(key);

		// Otherwise, create the midpoint.
		Vector3d middle = Vector3d.addAndSetLength((vertices.get(p1)).location, (vertices.get(p2)).location, 1.0);

		// Add the midpoint to the mesh vertices.
		int i = addVertex(middle);

		// Cache the index of the new midpoint.
		midPointsCache.put(key, i);

		return i;
	}

	/**
	 *  Add a vertex to the vertex list (based on x,y,z coordinates).
	 */
	public int addVertex(double newX, double newY, double newZ) {
		vertices.add(new Vertex(newX, newY, newZ));
		return (vertices.size() - 1);
	}

	/**
	 *  Add a vertex to the vertex list (using a Vector3d).
	 */
	public int addVertex(Vector3d p) {
		return addVertex(p.x, p.y, p.z);
	}

	/**
	 * Add a triangular face to the face list.
	 * Parameters are the indices in the vertex list of the three corner points of the triangle.
	 * @param v1 Index in list 'vertices' of face vertex 1.
	 * @param v2 Index in list 'vertices' of face vertex 2.
	 * @param v3 Index in list 'vertices' of face vertex 3.
	 */
	public void addTriangle(int v1, int v2, int v3) {
		Triangle t = new Triangle(v1, v2, v3);
		addTriangle(t);
	}

	/**
	 * Add an existing triangle to the face list
	 * @param t The BSimTriangle to be added 
	 */
	public void addTriangle(Triangle t) {
		computeNormal(t);
		faces.add(t);
	}

	/**
	 * Compute which faces index each vertex, and store this as a list parameter in each vertex object.
	 *  
	 * Face connectivity from a vertex should be a useful parameter when doing space subdivision etc.
	 */
	protected void calcVertexFaces() {
		int[] tvs = new int[3];

		// For each face of the mesh, check which vertices it uses.
		for (int i = 0; i < faces.size(); i++) {
			tvs = (faces.get(i)).getPoints();

			// If one of the three vertices does not index this face yet, add the face index to the list.
			for (int n = 0; n < 3; n++) {
				Vertex v = vertices.get(tvs[n]);
				if (!(v.faces.contains(i))) {
					v.faces.add(i);
				}
			}
		}

		// Trim the face index list of each vertex as we will not be using them further.
		for (Vertex v : vertices) {
			v.faces.trimToSize();
		}
	}

	/**
	 * Trim down the arrayLists, and compute vertex-face connectivity.
	 * Minimises storage and hopefully increases efficiency (unless we will be changing these lists later).
	 * @param stats (if true, print mesh statistics after clean-up is done.)
	 */
	protected void cleanUp(boolean stats) {
		vertices.trimToSize();
		faces.trimToSize();

		calcVertexFaces();

		if (stats) printStats();
	}

	/**
	 * Compute the normal vector of a face.
	 */
	public void computeNormal(Triangle t) {
		Vector3d p1 = new Vector3d(vertices.get(t.tVertices[0]).location);
		Vector3d p2 = new Vector3d(vertices.get(t.tVertices[1]).location);
		Vector3d p3 = new Vector3d(vertices.get(t.tVertices[2]).location);

		Vector3d v1 = Vector3d.subtract(p2, p1);
		Vector3d v2 = Vector3d.subtract(p3, p1);

		Vector3d newNormal = Vector3d.cross(v1, v2);

		newNormal.normalise();

		t.updateNormal(newNormal);
	}

	/**
	 * Compute all normals of the mesh
	 */
	public void computeNormals() {
		for (Triangle t : faces) {
			computeNormal(t);
		}
	}

	/**
	 * Flip normals of all faces
	 */
	public void flipNormals() {
		for (Triangle t : faces) {
			t.flipNormal();
		}
	}

	/**
	 * Flip normals of selected faces
	 * @param normalsList Array of integer indices corresponding to the faces we wish to flip.
	 */
	public void flipNormals(int[] faceList) {
		for (int i = 0; i < faceList.length; i++) {
			faces.get(i).flipNormal();
		}
	}

	/**
	 * Scale mesh on arbitrary point
	 * @param scaleFactor 	The factor by which the mesh will be scaled 
	 * 						(1.0 = no scaling, 2.0 = double size, 0.5 = half size, etc.)
	 * @param scaleOn		The point from which the mesh will be scaled.
	 */
	public void scale(double scaleFactor, Vector3d scaleOn) {
		for (Vertex v : vertices) {
			Vector3d current = new Vector3d(v.location);
			Vector3d scaleVec = Vector3d.subtract(current, scaleOn);

			scaleVec.scale(scaleFactor - 1.0);

			v.location.add(scaleVec);
		}
	}

	/**
	 * Scale mesh on origin (0, 0, 0).
	 * @param scaleFactor	The factor by which the mesh will be scaled.
	 */
	public void scale(double scaleFactor) {
		scale(scaleFactor, new Vector3d(0, 0, 0));
	}

	/**
	 * Compute the (unweighted) average center coordinate of all mesh vertices.
	 * @return
	 */
	public Vector3d averagedCentreOfMesh() {
		double xTotal = 0;
		double yTotal = 0;
		double zTotal = 0;

		// Sum x, y, z coords
		for (Vertex v : vertices) {
			Vector3d vLoc = v.location;

			xTotal += vLoc.x;
			yTotal += vLoc.y;
			zTotal += vLoc.z;
		}

		// Scale coords by reciprocal of total number of vertices 
		Vector3d centerPos = new Vector3d(xTotal, yTotal, zTotal);
		centerPos.scale(1 / vertices.size());

		return centerPos;
	}

	/**
	 * Translate the whole mesh so that it is centerd on a new point in 3d space.
	 * @param newLocation The location on which the mesh will be centerd.
	 */
	public void translateAbsolute(Vector3d newLocation) {
		// Current average center of mesh
		Vector3d currentLocation = averagedCentreOfMesh();

		// Translation vector: new_loc - current_loc
		Vector3d translation = Vector3d.subtract(newLocation, currentLocation);

		// Move all vertices by translation vector
		translate(translation);
	}

	/**
	 * Translate the mesh in an arbitrary direction.
	 * @param translation The vector by which all vertices are translated
	 */
	public void translate(Vector3d translation) {
		for (Vertex v : vertices) {
			v.location.add(translation);
		}
	}

	// Getters
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public Vertex getVertex(int i) {
		return vertices.get(i);
	}

	public ArrayList<Triangle> getFaces() {
		return faces;
	}

	public Triangle getFace(int i) {
		return faces.get(i);
	}

	public Vector3d getVertCoords(int vertIndex) {
		return vertices.get(vertIndex).location;
	}

	/**
	 * Get the vertex coordinates of a given triangle
	 * @param t
	 * @param i Index of the vertex for which to get coordinates (0, 1, 2)
	 * @return
	 */
	public Vector3d getVertCoordsOfTri(Triangle t, int i) {
		int index = t.getPoints()[i];
		return vertices.get(index).location;
	}

	/**
	 * Compute the coordinates of the center of a triangle
	 * @param t
	 * @return
	 */
	public Vector3d getTCentre(Triangle t) {
		double x, y, z;
		Vector3d a = vertices.get(t.getP1()).location;
		Vector3d b = vertices.get(t.getP2()).location;
		Vector3d c = vertices.get(t.getP3()).location;

		x = (a.x + b.x + c.x) / 3;
		y = (a.y + b.y + c.y) / 3;
		z = (a.z + b.z + c.z) / 3;

		return (new Vector3d(x, y, z));
	}

	/**
	 * Print mesh statistics (face vertices, vertex coords, vertex faces, normals...)
	 */
	public void printStats() {
		int n = 0;
		System.out.println("Face | Vertex indices");
		for (Triangle t : faces) {
			System.out.print(n + " | ");
			for (Integer i : t.getPoints()) {
				System.out.print(i.toString() + ", ");
			}
			System.out.println();
			n++;
		}
		System.out.println();
		n = 0;

		System.out.println("Vertex | (x, y, z)");
		for (Vertex v : vertices) {
			System.out.print(n + " | ");
			System.out.print(v.location.toString());
			System.out.println();
			n++;
		}
		System.out.println();
		n = 0;

		System.out.println("Vertex | Face list");
		for (Vertex v : vertices) {
			System.out.print(n + " | ");
			for (Integer i : v.faces) {
				System.out.print(i.toString() + ", ");
			}
			System.out.println();
			n++;
		}
		System.out.println();
		n = 0;

		System.out.println("Face | Normal");
		for (Triangle t : faces) {
			System.out.print(n + " | ");
			System.out.print(t.getNormal());
			System.out.println();
			n++;
		}
		System.out.println();
		n = 0;
	}
}
