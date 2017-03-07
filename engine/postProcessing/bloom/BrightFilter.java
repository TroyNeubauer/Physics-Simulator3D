package postProcessing.bloom;

import org.lwjgl.opengl.*;

import com.troyberry.opengl.util.*;

public class BrightFilter {

	private ImageRenderer renderer;
	private BrightFilterShader shader;
	
	public BrightFilter(int width, int height) {
		shader = new BrightFilterShader();
		renderer = new ImageRenderer(width, height);
	}
	
	public void render(Fbo texture){
		shader.start();
		texture.bind(0);
		renderer.renderQuad();
		shader.stop();
	}
	
	public Fbo getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}
	
}
