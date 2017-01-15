package sphere;

import com.troyberry.math.Matrix4f;
import com.troyberry.math.Vector3f;
import com.troyberry.opengl.util.GLColorUtil;
import com.troyberry.opengl.util.GLMaths;

import world.World;

public abstract class Sphere {
	
	public Vector3f position, color, velocity;
	public float scale, mass;
	protected World world;
	private final long id;
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
	
	public Sphere(Vector3f pos, Vector3f color, float scale){
		this(pos, new Vector3f(), color, scale, scale * scale * scale);
	}

	
	public Matrix4f getTransformationMatrix() {
		return GLMaths.createTransformationMatrix(position, scale);
	}
	
	public Matrix4f getTransformationMatrix(float scale) {
		return GLMaths.createTransformationMatrix(position, scale);
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

	public long getId() {
		return id;
	}

}
