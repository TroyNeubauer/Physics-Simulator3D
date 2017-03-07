package postProcessing;

import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;

import postProcessing.bloom.*;
import postProcessing.gaussianBlur.*;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static Vao quad;
	private static ContrastChanger contrastChanger;
	private static boolean contrast = false;
	
	public static void init() {
		quad = Vao.create();
		quad.createAttribute(0, POSITIONS, 2);
		contrastChanger = new ContrastChanger();
	}
	
	public static void doPostProcessing(Fbo scene) {

		Fbo lastStage = scene;
		
		start();
		if(contrast) {
			contrastChanger.render(lastStage);
			lastStage = contrastChanger.getOutputTexture();
		}
		lastStage.resolveToScreen();
		
		end();
	}
	
	public static void cleanUp() {
		quad.delete();
		contrastChanger.cleanUp();
	}
	
	private static void start() {
		quad.bind(0);
		GLUtil.disableDepthTesting();
	}
	
	private static void end() {
		GLUtil.enableDepthTesting();
		quad.unbind(0);
	}
}

