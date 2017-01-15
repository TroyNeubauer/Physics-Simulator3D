package skybox;

import org.lwjgl.opengl.GL11;

import com.troyberry.opengl.mesh.CubeGenerator;
import com.troyberry.opengl.mesh.Vao;
import com.troyberry.opengl.util.GlUtil;
import com.troyberry.opengl.util.ICamera;

public class SkyboxRenderer {
	
	private static final float SIZE = 1000000;
	
	private SkyboxShader shader;
	private Vao box;
	
	public SkyboxRenderer(){
		this.shader = new SkyboxShader();
		this.box = CubeGenerator.generateCube(SIZE);
	}
	
	/**
	 * Renders the skybox.
	 * @param camera - the scene's camera.
	 */
	public void render(ICamera camera){
		prepare(camera);
		box.bind(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, box.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		box.unbind(0);
		finish();
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(ICamera camera){
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		GlUtil.disableBlending();
		GlUtil.enableDepthTesting();
		GlUtil.cullBackFaces(true);
		GlUtil.antialias(false);
	}
	
	private void finish(){
		shader.stop();
	}	

}
