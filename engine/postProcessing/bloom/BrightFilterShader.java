package postProcessing.bloom;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class BrightFilterShader extends ShaderProgram{
	
	private static final MyFile VERTEX_FILE = new MyFile("/postProcessing/bloom/simple.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("/postProcessing/bloom/brightFilter.frag");
	
	public BrightFilterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
	}

}
