package sphere;

import java.util.*;
import java.util.concurrent.CompletionException;

import org.lwjgl.opengl.GL11;

import com.codedisaster.steamworks.SteamFriends.FriendRelationship;
import com.troy.troyberry.math.Maths;
import com.troy.troyberry.opengl.util.*;

import openglObjects.Vao;
import utils.*;
import world.Sphere;

public class SphereRenderer {

	private SphereShader shader;
	private Vao[] vaos;
	private static int count = 0;
	private LodManager lod;
	private FrustumCuller culler;
	
	public SphereRenderer(ICamera camera) {
		this.shader = new SphereShader();
		this.culler = new FrustumCuller(camera);
		vaos = new Vao[5];
		for(int i = 0; i < vaos.length; i++){
			vaos[i] = SphereGenerator.generateCube(i);
		}
		lod = new LodManager();
		lod.add(0, 100);
		lod.add(1, 50);
		lod.add(2, 30);
		lod.add(3, 10);
		lod.add(4, 0);
	}
	
	public void render(List<Sphere> origionalPlanets, ICamera camera) {
		culler.update(camera);
		Map<Integer, List<Sphere>> planets = new HashMap<Integer, List<Sphere>>();

		for(Sphere sphere : origionalPlanets) {
			if(sphere == null)continue;
			double distance = Maths.getDistanceBetweenPoints(camera.getPosition(), sphere.position) / sphere.scale;
			int currentLOD = this.lod.get((float) distance);
			List<Sphere> batch = planets.get(currentLOD);
			if (batch != null) {
				batch.add(sphere);
			} else {
				List<Sphere> newBatch = new ArrayList<Sphere>();
				newBatch.add(sphere);
				planets.put(currentLOD, newBatch);
			}
		}
		prepare(camera);
		for(Integer lod : planets.keySet()) {
			vaos[lod].bind(0);
			for(Sphere planet : planets.get(lod)) {
				prepareInstance(planet);
				GL11.glDrawElements(GL11.GL_TRIANGLES, vaos[lod].getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			vaos[lod].unbind(0);
		}
		finish();
	}
	
	public void cleanUp(){
		shader.cleanUp();
		for(int i = 0; i < vaos.length; i++){
			vaos[i].delete();
		}
	}
	
	private void prepareInstance(Sphere planet){
		shader.transformationMatrix.loadMatrix(planet.getTransformationMatrix());
		shader.color.loadVec3(planet.color);
	}
	
	private void prepare(ICamera camera){
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		GlUtil.enableAlphaBlending();
		GlUtil.enableDepthTesting(true);
		GlUtil.cullBackFaces(true);
	}
	
	private void finish() {
		shader.stop();
	}	

}
