package renderEngine;

import com.troyberry.graphics.ResolutionUtil;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

import main.Controls;
import update.UpdateMaster;
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
	
	/**
	 * Updates the render engine so it is ready for the next frame.
	 * This method updates the display as well as updating the keyboard and mouse
	 * The name update may be misleading, this method must only be called from the Open GL thread!
	 * @param world
	 */
	public void update(World world) {
		window.update();
		if(!Controls.SLOW.isPressedUpdateThread())world.updateOnRender();
		camera.moveOther();
		Mouse.setGrabbed(true);
		KeyHandler.update();
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
		window.hide();
		UpdateMaster.shutdown();
		window.destroy();
		renderer.cleanUp();
	}

	public static RenderEngine init(ICamera camera) {
		if(!GlUtil.init()){
			System.exit(1);
		}
		Window window = new Window(ResolutionUtil.getscaledResolution(0.75));
		window.setClearColor(0, 0, 0);
		camera.update();
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
