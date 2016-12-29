package renderEngine;

import com.troy.troyberry.opengl.util.ICamera;

import sphere.SphereRenderer;
import world.World;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 *
 */
public class MasterRenderer {

	private SphereRenderer shpereRenderer;
	public static final float NEAR_PLANE = 0.1f, FAR_PLANE = Float.MAX_VALUE;

	protected MasterRenderer(ICamera camera) {
		shpereRenderer = new SphereRenderer(camera);
	}

	/**
	 * Renders the scene to the screen.
	 * @param world
	 */
	protected void renderScene(World world) {
		shpereRenderer.render(world.getObjects(), world.getCamera());
	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		shpereRenderer.cleanUp();
	}

	public void update() {
		
	}

}
