package com.troy.ps.main;

import com.troyberry.opengl.input.*;

public class Controls {
	//format:off
	public static final KeyBinding 		FORWARD = new KeyBinding(Keyboard.KEY_W), 		BACKWARD = new KeyBinding(Keyboard.KEY_S),
										LEFT = new KeyBinding(Keyboard.KEY_A), 			RIGHT = new KeyBinding(Keyboard.KEY_D), 
										UP = new KeyBinding(Keyboard.KEY_SPACE), 		DOWN = new KeyBinding(Keyboard.KEY_LEFT_SHIFT), 
										ROTATE_LEFT = new KeyBinding(Keyboard.KEY_Q),	ROTATE_RIGHT = new KeyBinding(Keyboard.KEY_E), 	
										POLYGON = new KeyBinding(Keyboard.KEY_G);
	public static final MouseBinding 	LOOK = new MouseBinding(0);

}
