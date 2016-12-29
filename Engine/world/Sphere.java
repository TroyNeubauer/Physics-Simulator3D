package world;

import com.troy.troyberry.math.*;
import com.troy.troyberry.opengl.util.GLColorUtil;
import com.troy.troyberry.opengl.util.GLMaths;

public class Sphere {
	
	public Vector3f position, color, velocity;
	public float scale, mass;
	private World world;
	private long id;
	public boolean glow = false;
	
	private static long count = 0L;
	
	public Sphere(Vector3f position, Vector3f velocity, Vector3f color, float scale, float mass) {
		this.position = position;
		this.velocity = velocity;
		this.scale = scale * 1.0f;
		this.mass = mass;
		this.color = GLColorUtil.toGLSLColor(color);
		this.id = count++;
	}
	
	public Sphere(Vector3f pos, float scale){
		this(pos, new Vector3f(), GLColorUtil.toGLSLColor(GLColorUtil.randomPlanetColor(15)), scale, scale * scale * scale);
	}

	
	public Matrix4f getTransformationMatrix() {
		float scaleAdd = (float) Math.pow(1.00002, Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), position));
		if(scale > 50.0){
			scaleAdd = (float) Math.pow(1.000005, Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), position));
		}
		glow = scaleAdd >= 1.1f;
		return GLMaths.createTransformationMatrix(position, 0, 0, 0, Math.min(scale * scaleAdd, scale * 6.0f));
	}

	public void setWorld(World world) {
		this.world = world;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Sphere){
			return ((Sphere)obj).id == this.id;
		}
		return false;
	}

}
