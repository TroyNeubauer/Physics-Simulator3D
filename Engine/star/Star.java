package star;

import com.troyberry.math.*;
import com.troyberry.opengl.util.GLColorUtil;
import com.troyberry.opengl.util.GLMaths;

import sphere.Sphere;

public class Star extends Sphere {
	public float intensity;

	public Star(Vector3f position, Vector3f velocity, Vector3f color, float scale, float mass) {
		super(position, velocity, color, scale, mass);
	}

	public Star(Vector3f pos, float scale) {
		super(pos, GLColorUtil.randomStarColor(15), scale);
	}
	
	@Override
	public Matrix4f getTransformationMatrix() {
		float scaleAdd = (float) Math.pow(1.00002, Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), position));
		if(scale > 50.0){
			scaleAdd = (float) Math.pow(1.000005, Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), position));
		}
		glow = scaleAdd >= 1.1f;
		return GLMaths.createTransformationMatrix(position, 0, 0, 0, Math.min(scale * scaleAdd, scale * 6.0f));
	}


}
