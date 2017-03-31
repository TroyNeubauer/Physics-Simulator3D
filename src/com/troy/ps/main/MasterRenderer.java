package com.troy.ps.main;

import com.troy.ps.renderer.*;
import com.troy.ps.world.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class MasterRenderer {

	private PointRenderer pointRenderer;
	private Fbo multiSampleFbo;
	private Fbo outputFbo;

	public MasterRenderer(Window window) {
		
		pointRenderer = new PointRenderer();
		multiSampleFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), new Integer(8));
		outputFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Fbo.DEPTH_RENDER_BUFFER);
	}

	public void render(ICamera camera, Window window, World world) {
		multiSampleFbo.clear();
		pointRenderer.render(world.getVao(), camera);

		multiSampleFbo.unbindFrameBuffer();
		multiSampleFbo.resolveTo(outputFbo);
		outputFbo.bindFrameBuffer();
		outputFbo.resolveToScreen();
		outputFbo.unbindFrameBuffer();
	}

	public void cleanUp() {

	}

}
