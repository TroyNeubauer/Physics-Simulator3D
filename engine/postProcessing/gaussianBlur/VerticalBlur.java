package postProcessing.gaussianBlur;

import org.lwjgl.opengl.*;

import com.troyberry.opengl.util.*;

public class VerticalBlur {
	
	private ImageRenderer renderer;
	private VerticalBlurShader shader;
	
	public VerticalBlur(int targetFboWidth, int targetFboHeight){
		shader = new VerticalBlurShader();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
		shader.start();
		shader.targetHeight.loadFloat(targetFboHeight);
		shader.stop();
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
