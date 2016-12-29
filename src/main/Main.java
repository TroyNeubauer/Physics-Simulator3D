package main;

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
		ICamera camera = new GravityCamera();
		camera.getPosition().set(0, 0, 3500);
		camera.getVelocity().y = -180;
		RenderEngine engine = RenderEngine.init(camera);
		Main.world = new World(camera, false);
		new Galaxy("Milky Way", 5, 25, 1200, 1500, 80, world, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		
		//new Galaxy("Small Way", 2, 17, 600, 500, 60, world, new Vector3f(-5000, -750, 2000), new Vector3f(-0.2f, 0, 0), new Vector3f(1000, 0, 0));
		float d = 40000;
		float v = 400;
		world.addSphere(new Sphere(new Vector3f(-d, 0,  0), new Vector3f( v, 0,  0), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		world.addSphere(new Sphere(new Vector3f( d, 0,  0), new Vector3f(-v, 0,  0), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		world.addSphere(new Sphere(new Vector3f( 0, 0,  d), new Vector3f( 0, 0, -v), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		world.addSphere(new Sphere(new Vector3f( 0, 0, -d), new Vector3f( 0, 0,  v), new Vector3f(50, 50, 50), 525, 4999 * 4999 * 4999 + 1));
		if(camera instanceof GravityCamera) ((GravityCamera)camera).setWorld(world);
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
