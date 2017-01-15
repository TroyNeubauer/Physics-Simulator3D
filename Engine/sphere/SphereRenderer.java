package sphere;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.troyberry.opengl.mesh.SphereGenerator;
import com.troyberry.opengl.mesh.Vao;
import com.troyberry.opengl.util.GlUtil;
import com.troyberry.opengl.util.ICamera;

public class SphereRenderer {

	private SphereShader shader;
	public static Vao[] vaos;
	private static int count = 0;
	
	public SphereRenderer(ICamera camera) {
		this.shader = new SphereShader();
		vaos = new Vao[5];
		for(int i = 0; i < vaos.length; i++){
			vaos[i] = SphereGenerator.generateCube(i);
		}
	}
	
	public void render(Map<Integer, List<Sphere>> planets, ICamera camera) {
		prepare(camera);
		for(Integer lod : planets.keySet()) {
			vaos[lod].bind(0);
			if(vaos.length - 1 == lod) GlUtil.cullBackFaces(false);
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
		GlUtil.enableDepthTesting();
		GlUtil.cullBackFaces(true);
	}
	
	private void finish() {
		shader.stop();
	}	

}
