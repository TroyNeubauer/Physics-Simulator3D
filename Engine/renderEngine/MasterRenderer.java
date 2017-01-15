package renderEngine;

import java.util.*;

import com.troyberry.math.Maths;
import com.troyberry.opengl.util.ICamera;
import com.troyberry.opengl.util.LodManager;

import planet.AtmosphereRenderer;
import skybox.SkyboxRenderer;
import sphere.Sphere;
import sphere.SphereRenderer;
import star.StarGlareRenderer;
import world.World;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 *
 */
public class MasterRenderer {

	private SphereRenderer shpereRenderer;
	private AtmosphereRenderer atmosphereRenderer;
	private SkyboxRenderer skyboxRenderer;
	private StarGlareRenderer starGlareRenderer;
	public static final float NEAR_PLANE = 0.00001f, FAR_PLANE = 1000000000;
	private LodManager lod;

	protected MasterRenderer(ICamera camera) {
		shpereRenderer = new SphereRenderer(camera);
		atmosphereRenderer = new AtmosphereRenderer(camera);
		skyboxRenderer = new SkyboxRenderer();
		starGlareRenderer = new StarGlareRenderer(camera);
		lod = new LodManager();
		lod.add(0, 1200);
		lod.add(1, 500);
		lod.add(2, 300);
		lod.add(3, 100);
		lod.add(4, 0);
	}

	/**
	 * Renders the scene to the screen.
	 * @param world
	 */
	protected void renderScene(World world) {
		skyboxRenderer.render(world.getCamera());
		Map<Integer, List<Sphere>> objects = new HashMap<Integer, List<Sphere>>();

		for(Sphere sphere : world.getObjects()) {
			if(sphere == null)continue;
			double distance = Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), sphere.position) / sphere.scale;
			int currentLOD = this.lod.get((float) distance);
			List<Sphere> batch = objects.get(currentLOD);
			if (batch != null) {
				batch.add(sphere);
			} else {
				List<Sphere> newBatch = new ArrayList<Sphere>();
				newBatch.add(sphere);
				objects.put(currentLOD, newBatch);
			}
		}
		
		
		shpereRenderer.render(objects, world.getCamera());
		atmosphereRenderer.render(world);
		starGlareRenderer.render(world);
	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		shpereRenderer.cleanUp();
		atmosphereRenderer.cleanUp();
	}

}
