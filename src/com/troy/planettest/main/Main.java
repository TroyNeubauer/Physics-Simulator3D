package com.troy.planettest.main;

import com.troy.troyberry.math.Maths;
import com.troy.troyberry.math.Vector3f;
import com.troy.troyberry.opengl.util.*;

import renderEngine.RenderEngine;
import sphere.SphereShader;
import utils.Controls;
import utils.Updater;
import world.*;

public class Main {
	
	private static World world;
	private static long startTime = System.currentTimeMillis();

	public static void main(String[] args) {
		System.out.println("Starting planet test 0.1.0");
		ICamera camera = new FreeCamera();
		RenderEngine engine = RenderEngine.init(camera);
		Main.world = new World(camera, false);
		new Galaxy("Milky Way", 5, 25, 1200, 750, 100, world, new Vector3f(25000, -75000, 2000), new Vector3f(0.3f, 0, 0), new Vector3f(0, 100, 0));
		
		new Galaxy("Small Way", 2, 17, 600, 200, 100, world, new Vector3f(-25000, -75000, 2000), new Vector3f(0.4f, 0, 0), new Vector3f(0, 0, 0));
		//world.addSphere(new Sphere(new Vector3f(0, -501.5f, 0), new Vector3f(0, 3, 0), GLColorUtil.LIGHT_GRAY, 500, 500 * 500 * 5000000));
		Updater.init();
		while(!engine.getWindow().isCloseRequested()) {
			engine.renderScene(world);
			engine.update(world);
			GlUtil.checkForErrors("Render");
		}
		engine.close();
	}
	
	public static void update(){
		if(!Controls.GO_WIREFRAME.isPressedUpdateThread() && (System.currentTimeMillis() - startTime > 1000))world.update();
	}

}
