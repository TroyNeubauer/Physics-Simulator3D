package utils;

import com.troy.troyberry.opengl.input.KeyBinding;
import com.troy.troyberry.opengl.input.Keyboard;

public class Controls {
	
	/** A keybinding **/
	public static final KeyBinding FORWARD = new KeyBinding(Keyboard.KEY_W), BACKWARD = new KeyBinding(Keyboard.KEY_S),
		LEFT = new KeyBinding(Keyboard.KEY_A), RIGHT = new KeyBinding(Keyboard.KEY_D),
		TOGGLE_FULLSCREEN = new KeyBinding(Keyboard.KEY_BACKSLASH), SPACE = new KeyBinding(Keyboard.KEY_SPACE),
		SHIFT = new KeyBinding(Keyboard.KEY_LEFT_SHIFT), MORE = new KeyBinding(Keyboard.KEY_RIGHT),
		LESS = new KeyBinding(Keyboard.KEY_LEFT), GO_WIREFRAME = new KeyBinding(Keyboard.KEY_G),
		BANK_LEFT = new KeyBinding(Keyboard.KEY_Q), BANK_RIGHT = new KeyBinding(Keyboard.KEY_E),
		RESET_POS = new KeyBinding(Keyboard.KEY_F7);

	private Controls() {
	}

}
