package com.troy.ps.main;

import com.troyberry.opengl.util.*;
import com.troyberry.util.*;

public class Initializer {

	public static Window init() {
		VersionManager.setVersion(new Version());
		GLUtil.init();
		return new Window(1440, 810);
	}
	
	private Initializer() {
	}

}
