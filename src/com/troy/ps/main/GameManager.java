package com.troy.ps.main;

import java.util.*;

import com.troy.ps.gamestate.*;
import com.troy.ps.renderer.*;
import com.troy.ps.world.planet.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class GameManager implements GameState {

	private MasterRenderer renderer;
	private ICamera camera;

	private Planet planet;

	public void init(Window window) {
		//OpenCLManager.create();
		Mouse.setGrabbed(true);
		renderer = new MasterRenderer(window);
		camera = new FreeCamera(window, 70);
		camera.setPosition(new Vector3f(0, 0, -15));
		//OpenCLManager.forceUpdate();
		planet = new Planet(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Random().nextLong(), 0);
		planet.reGenerate(new Vector3d(1, 0, 0), 0.75);
		
		Vector3f[] vecs = planet.findSutableSpawnLocation();
		//camera.setPosition(vecs[0]);
		//camera.setUpDirection(vecs[1]);
	}

	public void update(float delta, Window window) {
		Mouse.resetScroll();
	}

	public void render(Window window) {
		KeyHandler.update();
		MouseHandler.update();
		camera.move(Window.getFrameTimeSeconds());
		camera.onMouseMove();
		planet.update(Window.getFrameTimeSeconds());
		//OpenCLManager.update();
		renderer.render(camera, window, planet);
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
		planet.cleanUp();
	}

}
