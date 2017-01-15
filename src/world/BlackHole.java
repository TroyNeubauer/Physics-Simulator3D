package world;

import com.troyberry.math.Vector3f;

import sphere.Sphere;

public class BlackHole extends Sphere {

	public BlackHole(Vector3f position, Vector3f velocity, Vector3f color, float scale, float mass) {
		super(position, velocity, color, scale, mass);
	}

	public BlackHole(Vector3f pos, float scale) {
		super(pos, Vector3f.randomVector(0f, 100f, 0f, 100f, 0f, 100f), scale);
	}

}
