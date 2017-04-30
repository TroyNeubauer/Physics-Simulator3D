package test;

import java.util.*;

import com.troyberry.math.*;

public class Vertex {
	
	/** The Cartesian coordinates of the vertex in 3-D space. */
	protected Vector3d location;
	
	/**
	 * List of the indices of the faces which use this vertex.
	 */
	protected ArrayList<Integer> faces = new ArrayList<Integer>(1);
	
	/**
	 * Constructor: create a new mesh vertex from three points; x,y,z
	 */
	public Vertex(double newX, double newY, double newZ){
		Vector3d newLocation = new Vector3d(newX,newY,newZ);
		location = newLocation;
	}
	
	/**
	 * Constructor: create a new mesh vertex from a Vector3d
	 */
	public Vertex(Vector3d newLocation){
		location = newLocation;
	}
}