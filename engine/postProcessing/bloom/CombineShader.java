package postProcessing.bloom;

import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.*;

public class CombineShader extends ShaderProgram {

	private static final MyFile VERTEX_FILE = new MyFile("/postProcessing/bloom/simple.vert");
	private static final MyFile FRAGMENT_FILE = new MyFile("/postProcessing/bloom/combine.frag");
	
	protected UniformSampler colorTexture = new UniformSampler("colorTexture");
	protected UniformSampler highlightTexture = new UniformSampler("highlightTexture");
	
	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position");
		super.storeAllUniformLocations(colorTexture, highlightTexture);
	}
	
	protected void connectTextureUnits(){
		colorTexture.loadTexUnit(0);
		highlightTexture.loadTexUnit(1);
	}
	
}
