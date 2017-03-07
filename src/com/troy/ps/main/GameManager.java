package com.troy.ps.main;

import com.troy.ps.gamestate.*;
import com.troy.ps.world.*;
import com.troyberry.opengl.util.*;

public class GameManager implements GameState {

	private MasterRenderer renderer;
	private EngineMaster engine;
	private World world;
	
	public void init() {
		renderer = new MasterRenderer(Window.getInstance());
		engine = new EngineMaster();
		world = new World();
	}
	
	public void update() {
		engine.update(world);
	}

	public void render() {
		renderer.render(world);
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
		engine.cleanUp();
	}

}
