package com.troy.ps.main;

import com.troy.ps.gamestate.*;
import com.troy.ps.glRequestProcessing.*;
import com.troyberry.opengl.util.*;

public class Main {
	
	public static void main(String[] args) {

		PhysicsSimulator ps = new PhysicsSimulator(args);
		Thread GLThread = new Thread(ps, "Open GL Thread");
		GLThread.start();
	}

}
