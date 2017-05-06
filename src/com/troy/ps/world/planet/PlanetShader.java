package com.troy.ps.world.planet;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.*;

public class PlanetShader extends ShaderProgram {

	private static final MyFile VERT_FILE = new MyFile("/com/troy/ps/world/planet/planet.vert"), FRAG_FILE = new MyFile("/com/troy/ps/world/planet/planet.frag");

	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");

	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");

	protected UniformMatrix modelMatrix = new UniformMatrix("modelMatrix");
	
	protected UniformVec3 lightPos = new UniformVec3("lightPos");
	
	protected UniformVec3 lightColor = new UniformVec3("lightColor");
	
	protected UniformVec3 ambientLighting = new UniformVec3("ambientLighting");
	
	protected UniformBoolean enableLighting = new UniformBoolean("enableLighting");

	public PlanetShader() {
		super(VERT_FILE, FRAG_FILE, "position", "color", "normal");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, lightPos, lightColor, ambientLighting, enableLighting);
	}

}
