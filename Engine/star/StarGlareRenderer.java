package star;

import java.util.*;

import org.lwjgl.opengl.GL11;

import com.troyberry.math.Maths;
import com.troyberry.opengl.util.*;

import main.PSGameSettings;
import sphere.Sphere;
import sphere.SphereRenderer;
import world.World;

public class StarGlareRenderer {
	private StarGlareShader shader;
	private LodManager lod;
	
	public StarGlareRenderer(ICamera camera) {
		this.shader = new StarGlareShader();
		lod = new LodManager();
		lod.add(0, 600);
		lod.add(1, 300);
		lod.add(3, 100);
		lod.add(4, 0);
	}
	
	public void render(World world) {
		Map<Integer, List<Star>> list = new HashMap<Integer, List<Star>>();
		for(Sphere sphere : world.getObjects()) {
			if(sphere == null)continue;
			if(!(sphere instanceof Star)) continue;
			double distance = Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), sphere.position) / sphere.scale;
			if(distance > PSGameSettings.ATMOSPHERE_RENDER_DISTANCE)continue;
			int currentLOD = this.lod.get((float) distance);
			List<Star> batch = list.get(currentLOD);
			if (batch != null) {
				batch.add((Star)sphere);
			} else {
				List<Star> newBatch = new ArrayList<Star>();
				newBatch.add((Star)sphere);
				list.put(currentLOD, newBatch);
			}
		}
		
		ICamera camera = world.getCamera();
		prepare(camera);
		for(Integer lod : list.keySet()) {
			SphereRenderer.vaos[lod].bind(0);
			for(Star star : list.get(lod)) {
				float atmosphereThickness = star.scale * 1.5f;
				/** Distance from camera to the center of the star */
				double distanceFromCenter = Maths.getDistanceBetweenPoints(star.position, camera.getPosition());
				/** Distance from camera to star surface */
				double distanceFromSurface = distanceFromCenter - star.scale; // Negative if camera is inside star
				/** Distance from camera to star surface */
				double distanceFromAtmosphere = distanceFromSurface - atmosphereThickness; // Negative if camera is inside atmosphere
				float bottomLayerStart = star.scale;
				/** How far from the center of the sphere does the atmosphere end*/
				float topLayerEnd = bottomLayerStart + atmosphereThickness;
				/**How thick is the atmosphere */
				float maxLayers = (atmosphereThickness / 5.0f) * PSGameSettings.ATMOSPHERE_QUALITY * 2.5f;
				int layerCount = (int) Maths.clamp(1.0, maxLayers, (atmosphereThickness * 25.0f) / distanceFromAtmosphere);
				if(distanceFromAtmosphere < 0.0){
					layerCount = (int)maxLayers;
				}
				float totalAlpha = 75.0f / 100.0f;
				
				float eachLayerThickness = atmosphereThickness / layerCount;
				float eachLayerAlpha = totalAlpha / maxLayers;
				float cameraLayer = Maths.lerpSafe(0, maxLayers, ((float)distanceFromSurface / maxLayers));
				shader.alpha.loadFloat(eachLayerAlpha);
				shader.color.loadVec3(star.color);
				float distFactor = 0f;
				// Make things inside the atmosphere look blue
				for(int i = 0; i <= (int)cameraLayer; i++) {
					distFactor = i / cameraLayer;
					shader.transformationMatrix.loadMatrix(star.getTransformationMatrix(Maths.lerpSafe(bottomLayerStart, topLayerEnd, distFactor)));
					GL11.glDrawElements(GL11.GL_TRIANGLES, SphereRenderer.vaos[lod].getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
				
			}
			SphereRenderer.vaos[lod].unbind(0);
		}
		finish();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(ICamera camera){
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		GlUtil.enableAdditiveBlending();
		GlUtil.enableDepthTesting();
		GlUtil.disableDepthWriting();
		GlUtil.cullBackFaces(true);
	}
	
	private void finish() {
		shader.stop();
		GlUtil.enableDepthWriting();
	}	
}
