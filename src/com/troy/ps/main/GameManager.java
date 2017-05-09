package com.troy.ps.main;

import java.io.*;
import java.util.Random;

import com.troy.ps.gamestate.GameState;
import com.troy.ps.renderer.FreeCamera;
import com.troy.ps.world.Light;
import com.troy.ps.world.planet.Planet;
import com.troyberry.math.*;
import com.troyberry.opengl.engine.text.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.loading.texture.*;
import com.troyberry.opengl.resources.Resource;
import com.troyberry.opengl.util.*;

public class GameManager implements GameState, Resource {

	private MasterRenderer renderer;
	private ICamera camera;

	private Planet planet;

	private Light light = new Light(new Vector3f(0, 200000000f, -500000000f), new Vector3f(1.0f, 1.0f, 1.0f));
	
	public void init(Window window) {

		//OpenCLManager.create();
		Mouse.setGrabbed(true);
		renderer = new MasterRenderer(window);
		camera = new FreeCamera(window, 70);
		//OpenCLManager.forceUpdate();
		planet = new Planet(new Vector3d(0, 0, 0), new Random().nextLong(), 4);
		planet.setRotation(new Vector3d(0, 180, 0));
		planet.setRotationVelocity(new Vector3d(0, 0, 0));
		Vector3d[] vecs = planet.findSutableSpawnLocation();
		camera.setPosition(vecs[0]);
		camera.setForwardDirection(vecs[1]);

		planet.compress();
		
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
		renderer.render(camera, window, planet, light);
	}

	@Override
	public void delete() {
		renderer.delete();
		planet.delete();
	}

}
