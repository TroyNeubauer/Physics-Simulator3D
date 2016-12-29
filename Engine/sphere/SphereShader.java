package sphere;

import com.troy.troyberry.util.MyFile;
import com.troy.troyberry.utils.graphics.ColorUtil;

import shaders.*;

public class SphereShader extends ShaderProgram {
	
	private static final MyFile VERTEX_SHADER = new MyFile("sphere", "sphere.vert");
	private static final MyFile FRAGMENT_SHADER = new MyFile("sphere", "sphere.frag");


	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	protected UniformVec3 color = new UniformVec3("color");

	public SphereShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, transformationMatrix, color);
	}

}
