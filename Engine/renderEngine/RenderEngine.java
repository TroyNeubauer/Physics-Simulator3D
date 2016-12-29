package renderEngine;

import com.troy.troyberry.opengl.input.*;
import com.troy.troyberry.opengl.util.*;
import com.troy.troyberry.utils.graphics.ResolutionUtil;

import utils.Controls;
import utils.Updater;
import world.World;

/**
 * This class represents the entire render engine.It's the only class of the render engine that needs
 * to be accessed outside of the "Engine" code.
 *
 */
public class RenderEngine {

	private MasterRenderer renderer;
	private Window window;
	private ICamera camera;
	private float time = 0f;

	private RenderEngine(Window window, MasterRenderer renderer, ICamera camera) {
		this.renderer = renderer;
		this.window = window;
		this.camera = camera;
	}
	
	public void update(World world) {
		if(!Controls.GO_WIREFRAME.isPressedUpdateThread())world.updateOnRender();
		camera.moveOther();
		Mouse.setGrabbed(true);
		KeyHandler.update();
		window.update();
		renderer.update();
		time += Window.getFrameTimeSeconds();
		if(time >= 5.0f){
			time = 0.0f;
			System.gc();
		}
	}

	public void renderScene(World world) {
		renderer.renderScene(world);
	}

	public void close() {
		Updater.stop();
		window.hide();
		renderer.cleanUp();
		window.destroy();
		System.exit(0);
	}

	public static RenderEngine init(ICamera camera) {
		if(!GlUtil.init()){
			System.exit(1);
		}
		Window window = new Window(ResolutionUtil.getscaledResolution(0.8));
		window.setClearColor(0, 0, 0);
		camera.createProjectionMatrix();
		Mouse.init(window);
		Mouse.setCamera(camera);
		Keyboard.init(window);
		MasterRenderer renderer = new MasterRenderer(camera);
		return new RenderEngine(window, renderer, camera);
	}
	
	public Window getWindow() {
		return window;
	}

}
