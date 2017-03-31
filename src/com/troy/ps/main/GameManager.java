package com.troy.ps.main;

import com.troy.ps.gamestate.*;
import com.troy.ps.renderer.*;
import com.troy.ps.world.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class GameManager implements GameState {

	private MasterRenderer renderer;
	private World world;
	private ICamera camera;
	
	public void init(Window window) {
		OpenCLManager.create();
		Mouse.setGrabbed(true);
		renderer = new MasterRenderer(window);
		world = new World();
		camera = new FreeCamera(70);
		OpenCLManager.forceUpdate();
		Mouse.addMouseMotionCallback(() -> {
			camera.onMouseMove();
		});
	}
	
	public void update(double delta, Window window) {
		camera.move((float)delta);
	}

	public void render(Window window) {
		OpenCLManager.update();
		window.clear();
		renderer.render(camera, window, world);
		window.update();
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
	}

}
