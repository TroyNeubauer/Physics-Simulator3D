package com.troy.ps.main;

import java.util.*;

import com.troyberry.math.*;
import com.troyberry.noise.*;

public class Main {

	public static void main(String[] args) {

		PhysicsSimulator ps = new PhysicsSimulator(args);
		Thread GLThread = new Thread(ps, "Open GL Thread");
		GLThread.start();
	}

	public static double getAdd(SimplexNoise noise, double x, double y, double z, double maxDistance) {
		double value = noise.getNoise(x, y, z);// It will be in +- this range
		double range = noise.getLargestFeature() * noise.getPersistence();// Get a noise value
		value /= range;//Change from -range to range TO -1 to 1
		value *= maxDistance;//Change from -1 to 1 TO -maxDistance to maxDistance
		return value;//Return the finalized value
	}

}
