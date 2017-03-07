package com.troy.ps.main;

import com.troy.ps.renderer.*;
import com.troy.ps.world.*;
import com.troyberry.math.*;
import com.troyberry.opengl.util.*;

public class MasterRenderer {
	
	private PointRenderer pointRenderer;
	private Matrix4f projectionMatrix;
	
	public MasterRenderer(Window window) {
		pointRenderer = new PointRenderer();
		projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(window.getWidth(), window.getHeight(), 0.001f, 10000.0f, 70.0f);

	}

	public void render(World world) {
		pointRenderer.render(world.getVao(), projectionMatrix);
	}

	public void cleanUp() {
		
	}

}
