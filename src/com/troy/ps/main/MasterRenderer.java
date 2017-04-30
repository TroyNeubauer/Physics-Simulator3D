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
	

	public MasterRenderer(Window window) {
		pointRenderer = new PointRenderer();
		planetRenderer = new PlanetRenderer();
		multiSampleFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), new Integer(4));
		outputFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Fbo.DEPTH_RENDER_BUFFER);
	}

	public void render(ICamera camera, Window window, Planet planet) {
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
	}

}
