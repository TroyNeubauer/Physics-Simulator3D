package postProcessing;

import com.troyberry.opengl.util.*;

public class ContrastChanger {

	private ImageRenderer renderer;
	private ContrastShader shader;
	
	public ContrastChanger() {
		shader = new ContrastShader();
		renderer = new ImageRenderer(Window.getInstance().getWidth(), Window.getInstance().getHeight());
	}
	
	public void render(Fbo texture) {
		shader.start();
		texture.bind(0);
		renderer.renderQuad();
		shader.stop();
	}
	
	public Fbo getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	public void cleanUp() {
		shader.cleanUp();
		renderer.cleanUp();
	}
	
}
