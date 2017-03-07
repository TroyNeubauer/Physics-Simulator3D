package test;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class TestShader extends ShaderProgram {
	
	private static final MyFile VERTEX_FILE = new MyFile("test/test.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("test/test.frag");
	
	protected UniformVec4 transform = new UniformVec4("transform");
	protected UniformVec3 color = new UniformVec3("color");
	
	public TestShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
		super.storeAllUniformLocations(transform, color);
	}

}
