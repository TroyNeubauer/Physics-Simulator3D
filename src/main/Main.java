package main;

import com.troyberry.math.Vector3f;
import com.troyberry.opengl.util.GlUtil;
import com.troyberry.opengl.util.ICamera;
import com.troyberry.util.VersionManager;

import renderEngine.RenderEngine;
import update.UpdateMaster;
import world.Galaxy;
import world.World;

public class Main {
	
	private static World world;
	private static long startTime = System.currentTimeMillis();

	public static void main(String[] args) {
		VersionManager.setVersion(new Version());
		ICamera camera = new FreeCamera();
		camera.getPosition().set(0, 10000, 0);
		RenderEngine engine = RenderEngine.init(camera);
		world = new World(camera, false);
		new Galaxy("Milky Way", 5, 20, 15000, 15000, 75, world, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		
		//new Galaxy("Small Way", 2, 20, 800, 50, 30, world, new Vector3f(0, 0, -12000), new Vector3f(0, 0, 0f), new Vector3f(0, 0, 1000));
		
		float d = 40000;
		float v = 400;
		//world.addSphere(new BlackHole(new Vector3f(-d, 0,  0), new Vector3f( v, 0,  0), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		//world.addSphere(new BlackHole(new Vector3f( d, 0,  0), new Vector3f(-v, 0,  0), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		//world.addSphere(new BlackHole(new Vector3f( 0, 0,  d), new Vector3f( 0, 0, -v), new Vector3f(50, 50, 50), 500, 4999 * 4999 * 4999));
		//world.addSphere(new BlackHole(new Vector3f( 0, 0, -d), new Vector3f( 0, 0,  v), new Vector3f(50, 50, 50), 525, 4999 * 4999 * 4999 + 1));

		UpdateMaster.init(world);
		world.update();
		while(!engine.getWindow().isCloseRequested()) {
			engine.renderScene(world);
			engine.update(world);
			GlUtil.checkForErrors("Render");
		}
		engine.close();
	}

}
