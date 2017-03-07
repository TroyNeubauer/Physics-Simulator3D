package star;

import com.troyberry.math.*;
import com.troyberry.opengl.util.GLColorUtil;
import com.troyberry.opengl.util.GLMaths;

import main.Main;
import sphere.Sphere;

public class Star extends Sphere {
	public float intensity;

	public Star(Vector3f position, Vector3f velocity, Vector3f color, float scale, float mass) {
		super(position, velocity, color, scale, mass);
	}

	public Star(Vector3f pos, float scale) {
		super(pos, GLColorUtil.randomStarColor(15), scale);
	}


}
