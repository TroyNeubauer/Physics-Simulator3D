package com.troy.ps.main;

import com.troyberry.color.*;
import com.troyberry.logging.*;
import com.troyberry.math.*;
import com.troyberry.util.Interpolation.*;
import com.troyberry.util.Interpolation.KeyFrameMaster.*;
import com.troyberry.util.data.*;

public class Main {
	
	public static void main(String[] args) {
		
		PhysicsSimulator ps = new PhysicsSimulator(args);
		Thread GLThread = new Thread(ps, "Open GL Thread");
		GLThread.start();
	}

}
