package com.troy.ps.world.planet;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.*;

public class PlanetShader extends ShaderProgram {

	private static final MyFile VERT_FILE = new MyFile("/com/troy/ps/world/planet/planet.vert"),
			FRAG_FILE = new MyFile("/com/troy/ps/world/planet/planet.frag");

	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformMatrix modelMatrix = new UniformMatrix("modelMatrix");

	protected UniformVec3 lightPos = new UniformVec3("lightPos");
	protected UniformVec3 lightColor = new UniformVec3("lightColor");

	protected UniformSampler planetTexture = new UniformSampler("planetTexture");
	
	protected UniformBoolean disableLighting = new UniformBoolean("disableLighting");

	public PlanetShader() {
		super(VERT_FILE, FRAG_FILE, "position", "color", "textureCoords", "normal");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, lightPos, lightColor, planetTexture, disableLighting);
	}

}
