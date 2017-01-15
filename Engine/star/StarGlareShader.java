package star;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.MyFile;

public class StarGlareShader extends ShaderProgram {
	private static final MyFile VERTEX_SHADER = new MyFile("star", "starglare.vert");
	private static final MyFile FRAGMENT_SHADER = new MyFile("star", "starglare.frag");


	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	protected UniformVec3 color = new UniformVec3("color");
	protected UniformFloat alpha = new UniformFloat("alpha");

	public StarGlareShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "position");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, transformationMatrix, color, alpha);
	}
}
