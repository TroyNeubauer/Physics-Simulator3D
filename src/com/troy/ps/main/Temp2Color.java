package com.troy.ps.main;

import com.troyberry.math.*;
import com.troyberry.util.Interpolation.*;

public class Temp2Color {
	private static CustomKeyFrameManager<Vector3f> manager = new CustomKeyFrameManager<Vector3f>();

	static {

		manager.addFrame(1000, new Vector3f(1.000, 0.007, 0.000));
		manager.addFrame(1500, new Vector3f(1.000, 0.126, 0.000));
		manager.addFrame(2000, new Vector3f(1.000, 0.234, 0.010));
		manager.addFrame(2500, new Vector3f(1.000, 0.349, 0.067));
		manager.addFrame(3000, new Vector3f(1.000, 0.454, 0.151));
		manager.addFrame(3500, new Vector3f(1.000, 0.549, 0.254));
		manager.addFrame(4000, new Vector3f(1.000, 0.635, 0.370));
		manager.addFrame(4500, new Vector3f(1.000, 0.710, 0.493));
		manager.addFrame(5000, new Vector3f(1.000, 0.778, 0.620));
		manager.addFrame(5500, new Vector3f(1.000, 0.837, 0.746));
		manager.addFrame(6000, new Vector3f(1.000, 0.890, 0.869));
		manager.addFrame(6500, new Vector3f(1.000, 0.937, 0.988));
		manager.addFrame(7000, new Vector3f(0.907, 0.888, 1.000));
		manager.addFrame(7500, new Vector3f(0.827, 0.839, 1.000));
		manager.addFrame(8000, new Vector3f(0.762, 0.800, 1.000));
		manager.addFrame(8500, new Vector3f(0.711, 0.766, 1.000));
		manager.addFrame(9000, new Vector3f(0.668, 0.738, 1.000));
		manager.addFrame(9500, new Vector3f(0.632, 0.714, 1.000));
		manager.addFrame(10000, new Vector3f(0.602, 0.693, 1.000));

	}
	
	public static Vector3f getColor(double kelvin) {
		return manager.getValue(kelvin);
	}
}
