package renderEngine;

import java.util.*;

import com.troyberry.math.*;
import com.troyberry.opengl.util.*;

import main.*;
import planet.*;
import postProcessing.*;
import sphere.*;
import star.*;
import test.*;
import world.*;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 *
 */
public class MasterRenderer {

	private SphereRenderer shpereRenderer;
	private AtmosphereRenderer atmosphereRenderer;
	private TestRenderer test;
	public static final float NEAR_PLANE = 0.00001f, FAR_PLANE = 1000000000;
	private LodManager lod;
	private Fbo multiSampleFbo;
	private static Fbo outputFbo;

	protected MasterRenderer(ICamera camera) {
		shpereRenderer = new SphereRenderer(camera);
		atmosphereRenderer = new AtmosphereRenderer(camera);
		test = new TestRenderer();
		lod = new LodManager();
		lod.add(0, PSGameSettings.MIN_MESH_DISTANCE);
		lod.add(1, 500);
		lod.add(2, 300);
		lod.add(3, 100);
		lod.add(4, 0);
		multiSampleFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), new Integer(4));
		outputFbo = new Fbo(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init();
		GLUtil.enableMultiSampling();
	}

	/**
	 * Renders the scene to the screen.
	 * 
	 * @param world
	 */
	protected void renderScene(World world) {

		multiSampleFbo.clear();
		Map<Integer, List<Sphere>> objects = new HashMap<Integer, List<Sphere>>();

		for (Sphere sphere : world.getObjects()) {
			if (sphere == null)
				continue;
			double distance = Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), sphere.position)
					/ sphere.scale;
			if (distance / sphere.scale > PSGameSettings.MAX_STAR_RENDER_DISTANCE && (sphere instanceof Star))
				continue;
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
		// starGlareRenderer.render(world);

		multiSampleFbo.unbindFrameBuffer();
		multiSampleFbo.resolveTo(outputFbo);
		outputFbo.bindFrameBuffer();
		
		GLUtil.checkForErrors("Render");

		outputFbo.unbindFrameBuffer();
		PostProcessing.doPostProcessing(outputFbo);

	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		shpereRenderer.cleanUp();
		atmosphereRenderer.cleanUp();
		PostProcessing.cleanUp();
		multiSampleFbo.cleanUp();
	}

}
