package com.troy.ps.world.planet;

import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;

public class PlanetFace extends Face {

	private int recursionIndex;

	public PlanetFace(Vector3i face, int recursionIndex) {
		super(face);
		this.recursionIndex = recursionIndex;
	}
	
	

}
