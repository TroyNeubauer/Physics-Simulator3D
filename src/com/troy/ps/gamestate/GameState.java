package com.troy.ps.gamestate;

import com.troyberry.opengl.util.*;

public interface GameState {
	
	public void init(Window window);
	
	public void render(Window window);
	
	public void update(Window window);
	
	public void cleanUp();
}
