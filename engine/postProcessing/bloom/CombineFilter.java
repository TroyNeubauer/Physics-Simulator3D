package postProcessing.bloom;

import org.lwjgl.opengl.*;

import com.troyberry.opengl.util.*;

public class CombineFilter {
	
	private ImageRenderer renderer;
	private CombineShader shader;
	
	public CombineFilter(int width, int height){
		shader = new CombineShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
		renderer = new ImageRenderer(width, height);
	}
	
	public void render(Fbo colorTexture, Fbo highlightTexture){
		shader.start();
		colorTexture.bind(0);
		highlightTexture.bind(1);
		renderer.renderQuad();
		shader.stop();
	}
	
	public Fbo getOutputTexture() {
		return renderer.getOutputTexture();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}

}
