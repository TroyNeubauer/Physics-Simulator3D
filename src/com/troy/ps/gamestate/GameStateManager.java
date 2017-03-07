package com.troy.ps.gamestate;

import com.troy.ps.main.*;

public class GameStateManager {

	private static GameState currentGameState = null;
	private static GameState nextGameState = new GameManager();
	private static GameState suggestedGameState;

	/**
	 * Sets the current game state to a new one.
	 * @param state The new game state.
	 */
	public static void setState(GameState state) {
		nextGameState = state;
	}

	/**
	 * Ends the desired game state. When {@link GameManager#update() } is called, if a new game state has been suggested, 
	 * that game state will become the current one
	 * @param state
	 */
	public static void endState(GameState state) {
		if (state == nextGameState) {
			state.cleanUp();
			nextGameState = null;
		}
	}
	
	/**
	 * Gets the current game state
	 * @return The current game state
	 */
	public static GameState getState(){
		return currentGameState;
	}

	/**
	 * Suggests a new game state. The suggested game state will become the new one if {@link GameStateManager#endState(GameState) }
	 * is called before {@link GameStateManager#update() } is called.
	 * @param state The state to suggest
	 */
	public static void suggestState(GameState state) {
		suggestedGameState = state;
	}
	
	/**
	 * Renders the current game state
	 */
	public static void render() {
		if(currentGameState != null) currentGameState.render();
	}

	/**
	 * Updates the current game state if a new one has been suggested or set. Then calls the update method for the current game state
	 */
	public static void update() {
		if (nextGameState == null) {
			nextGameState = suggestedGameState;
		}
		suggestedGameState = null;
		if (nextGameState != currentGameState) {
			changeState(nextGameState);
		}
		if(currentGameState != null) currentGameState.update();
	}

	private static void changeState(GameState state) {
		if(currentGameState != null)currentGameState.cleanUp();
		currentGameState = state;
		nextGameState = state;
		if (currentGameState != null) {
			currentGameState.init();
		}
	}
	
	private GameStateManager() {
	}

	/**
	 * Cleans up the current game state
	 */
	public static void cleanUp() {
		if(currentGameState != null)currentGameState.cleanUp();
	}

}
