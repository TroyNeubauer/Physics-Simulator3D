package com.troy.ps.main;

import com.troy.ps.renderer.*;
import com.troy.ps.world.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class MasterRenderer {

	private PointRenderer pointRenderer;
	private Matrix4f projectionMatrix;
	private Fbo multiSampleFbo;
	private Fbo outputFbo;
	float FOV = 70;

	public MasterRenderer(Window window) {
		pointRenderer = new PointRenderer();
		projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(window.getWidth(), window.getHeight(), 0.001f, 10000.0f, 100.0f);
		multiSampleFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), new Integer(4));
		outputFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Fbo.DEPTH_RENDER_BUFFER);
	}

	public void render(World world) {
		if(Keyboard.isKeyDown(Keyboard.KEY_0))FOV++;
		projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(Window.getInstance().getWidth(), Window.getInstance().getHeight(), 0.001f, 10000.0f, FOV);

		multiSampleFbo.clear();

		pointRenderer.render(world.getVao(), projectionMatrix);

		multiSampleFbo.resolveTo(outputFbo);
		outputFbo.bindFrameBuffer();
		outputFbo.resolveToScreen();
		outputFbo.unbindFrameBuffer();
	}

	public void cleanUp() {

	}

}
