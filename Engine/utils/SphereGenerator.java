package utils;

import java.util.*;

import com.troy.troyberry.math.Vector3f;
import com.troy.troyberry.math.Vector3i;
import com.troy.troyberry.util.ArrayUtil;

import openglObjects.Vao;

public class SphereGenerator {
	
	private static List<Vector3f> vertices = new ArrayList<Vector3f>();
	
	public static int add(Vector3f vec){
		if(!vertices.contains(vec)){
			vec.normalised();
			vertices.add(vec);
			return vertices.indexOf(vec);
		}
		return -1;
	}
	
	/**
	 * Generates a VAO containing the vertex positions of a sphere
	 * @param recursionCount - How many recursions should be done
	 * @param size - the radius of the sphere
	 * @return The VAO containing the sphere.
	 */
	public static Vao generateCube(int recursionLevel) {
		vertices.clear();
		Vao vao = Vao.create();
		
		float t = (1.0f + (float)Math.sqrt(5.0)) / 2.0f;
		
		add(new Vector3f(-1,  t,  0));
		add(new Vector3f( 1,  t,  0));
		add(new Vector3f(-1, -t,  0));
		add(new Vector3f( 1, -t,  0));

		add(new Vector3f( 0, -1,  t));
		add(new Vector3f( 0,  1,  t));
		add(new Vector3f( 0, -1, -t));
		add(new Vector3f( 0,  1, -t));

		add(new Vector3f( t,  0, -1));
		add(new Vector3f( t,  0,  1));
		add(new Vector3f(-t,  0, -1));
		add(new Vector3f(-t,  0,  1));
		
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

        for(int i = 0; i < recursionLevel; i++){
        	List<Vector3i> newFaces = new ArrayList<Vector3i>();
        	for(Vector3i triangle : faces){
        		Vector3f p1 = vertices.get(triangle.x);
        		Vector3f p2 = vertices.get(triangle.y);
        		Vector3f p3 = vertices.get(triangle.z);
        		Vector3f n1 = Vector3f.lerp(p1, p2, 0.5f);
        		Vector3f n2 = Vector3f.lerp(p2, p3, 0.5f);
        		Vector3f n3 = Vector3f.lerp(p3, p1, 0.5f);
        		int p1loc = vertices.indexOf(p1);
        		int p2loc = vertices.indexOf(p2);
        		int p3loc = vertices.indexOf(p3);
        		
        		int n1loc = add(n1);
        		int n2loc = add(n2);
        		int n3loc = add(n3);
        		
        		newFaces.add(new Vector3i(p1loc, n1loc, n3loc));
        		newFaces.add(new Vector3i(n3loc, n1loc, n2loc));
        		newFaces.add(new Vector3i(n1loc, p2loc, n2loc));
        		newFaces.add(new Vector3i(n3loc, n2loc, p3loc));
        	}
        	faces = newFaces;
        }
		
        vao.createIndexBuffer(ArrayUtil.toIntArrayFromVec3(faces));
		vao.createAttribute(0, ArrayUtil.toFloatArrayFromVec3(vertices), 3);
		
		vao.unbind();
		return vao;
	}

	private SphereGenerator() {
	}

}
