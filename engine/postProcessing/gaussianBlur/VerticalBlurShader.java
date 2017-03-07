package postProcessing.gaussianBlur;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class VerticalBlurShader extends ShaderProgram {

	private static final MyFile VERTEX_FILE = new MyFile("postProcessing/gaussianBlur/verticalBlur.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("postProcessing/gaussianBlur/blur.frag");
	
	protected UniformFloat targetHeight = new UniformFloat("targetHeight");
	
	protected VerticalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
		super.storeAllUniformLocations(targetHeight);
	}
}
