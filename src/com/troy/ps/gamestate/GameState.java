package com.troy.ps.gamestate;

public interface GameState {
	
	public void init();
	
	public void render();
	
	public void update();
	
	public void cleanUp();
}
