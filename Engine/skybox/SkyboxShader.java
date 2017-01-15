package skybox;

import com.troyberry.opengl.shader.ShaderProgram;
import com.troyberry.opengl.shader.UniformMatrix;
import com.troyberry.util.data.MyFile;

public class SkyboxShader extends ShaderProgram {

	private static final MyFile VERTEX_SHADER = new MyFile("skybox", "skybox.vert");
	private static final MyFile FRAGMENT_SHADER = new MyFile("skybox", "skybox.frag");

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");

	public SkyboxShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "position");
		super.storeAllUniformLocations(projectionViewMatrix);
	}
}
