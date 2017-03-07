package postProcessing.gaussianBlur;

import org.lwjgl.opengl.*;

import com.troyberry.opengl.util.*;

public class HorizontalBlur {
	
	private ImageRenderer renderer;
	private HorizontalBlurShader shader;
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight){
		shader = new HorizontalBlurShader();
		shader.start();
		shader.targetWidth.loadFloat(targetFboWidth);
		shader.stop();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
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
