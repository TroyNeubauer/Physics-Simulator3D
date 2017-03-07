package com.troy.ps.renderer;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.MyFile;

public class PointShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("com/troy/ps/renderer", "point.vert");
	private static final MyFile FRAGMENT_SHADER = new MyFile("com/troy/ps/renderer", "point.frag");
	
	protected UniformMatrix projectionMatrix = new UniformMatrix("projection_matrix");
	protected UniformFloat pointSize = new UniformFloat("pointSize");

	public PointShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "position", "color");
		super.storeAllUniformLocations(projectionMatrix, pointSize);
	}
}
