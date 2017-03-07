package postProcessing.gaussianBlur;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class HorizontalBlurShader extends ShaderProgram {

	private static final MyFile VERTEX_FILE = new MyFile("postProcessing/gaussianBlur/horizontalBlur.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("postProcessing/gaussianBlur/blur.frag");
	
	protected UniformFloat targetWidth = new UniformFloat("targetWidth");
	
	protected HorizontalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
		super.storeAllUniformLocations(targetWidth);
	}
	
}
