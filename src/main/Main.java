package main;

import com.troyberry.math.*;
import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

import renderEngine.*;
import star.*;
import update.*;
import world.*;

public class Main {
	
	public static World world;

	public static void main(String[] args) {
		VersionManager.setVersion(new Version());
		ICamera camera = new GravityCamera();
		RenderEngine engine = RenderEngine.init(camera);
		world = new World(camera, false);
		camera.setPosition(new Vector3f(0, 0, -10));
		
		Galaxy.createStandardGalaxy("Milky Way", 4, 15, 500, 25000, 200, world, new Vector3f(0, -1000000, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		UpdateMaster.init(world);
		world.update();
		while(!engine.getWindow().isCloseRequested()) {
			engine.renderScene(world);
			engine.update(world);
			GLUtil.checkForErrors("Render");
		}
		engine.close();
	}

}
