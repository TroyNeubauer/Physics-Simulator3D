package com.troy.ps.main;

import org.apache.commons.cli.Options;

public class CommandArgsOptions extends Options {
	
	protected static final String WIDTH = "width", HEIGHT = "height", FULLSCREEN = "fullscreen";

	public CommandArgsOptions() {
		super();
		super.addOption("w", WIDTH, true, "The width of the game window");
		super.addOption("h", HEIGHT, true, "The height of the game window");
		super.addOption("f", FULLSCREEN, false, "A flag that determenes the width of the game window (this will override width and height settings)");
		
	}

}
