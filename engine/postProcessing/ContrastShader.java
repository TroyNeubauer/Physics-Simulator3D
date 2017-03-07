package postProcessing;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class ContrastShader extends ShaderProgram {

	private static final MyFile VERTEX_FILE = new MyFile("/postProcessing/contrast.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("/postProcessing/contrast.frag");
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
	}



}
