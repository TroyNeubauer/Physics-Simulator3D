package com.troy.ps.renderer;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class PointShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("com/troy/ps/renderer", "point.vert");
	private static final MyFile FRAGMENT_SHADER = new MyFile("com/troy/ps/renderer", "point.frag");
	
	protected UniformMatrix projectionMatrix = new UniformMatrix("projection_matrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("view_matrix");

	public PointShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "position", "offset", "color", "radius");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix);
	}
}
