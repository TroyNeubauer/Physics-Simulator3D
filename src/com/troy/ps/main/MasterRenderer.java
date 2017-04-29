package com.troy.ps.main;

import com.troy.ps.renderer.*;
import com.troy.ps.world.*;
import com.troy.ps.world.planet.*;
import com.troyberry.math.*;
import com.troyberry.opengl.util.*;

public class MasterRenderer {

	private PointRenderer pointRenderer;
	private PlanetRenderer planetRenderer;
	private Fbo multiSampleFbo;
	private Fbo outputFbo;
	
	private Planet planet;

	public MasterRenderer(Window window) {
		planet = new Planet(new Vector3f(0, 0, -11), new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(0.5f, 2.0f, 0), 10.0f, 10 * 10 * 10);
		pointRenderer = new PointRenderer();
		planetRenderer = new PlanetRenderer();
		multiSampleFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), new Integer(4));
		outputFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Fbo.DEPTH_RENDER_BUFFER);
	}

	public void render(ICamera camera, Window window, World world) {
		multiSampleFbo.clear();
		//pointRenderer.render(world, camera);
		planetRenderer.render(planet, camera);
		
		multiSampleFbo.unbindFrameBuffer();
		multiSampleFbo.resolveTo(outputFbo);
		outputFbo.bindFrameBuffer();
		outputFbo.resolveToScreen();
		outputFbo.unbindFrameBuffer();
	}

	public void cleanUp() {
		pointRenderer.cleanUp();
		planetRenderer.cleanUp();
		multiSampleFbo.cleanUp();
		outputFbo.cleanUp();
		planet.cleanUp();
	}

}
