package main;

import com.troy.troyberry.math.Maths;
import com.troy.troyberry.math.Vector3f;
import com.troy.troyberry.opengl.input.Mouse;
import com.troy.troyberry.opengl.util.ICamera;
import com.troy.troyberry.opengl.util.Window;

import renderEngine.MasterRenderer;
import world.Sphere;
import world.World;

public class GravityCamera extends ICamera {

	private World world;
	
	public GravityCamera() {
		super(MasterRenderer.NEAR_PLANE, MasterRenderer.FAR_PLANE);
	}

	@Override
	public void moveRotation() {
		pitch += Mouse.getDY() / 10.0f;
		yaw   += Mouse.getDX() / 10.0f;
		if (pitch > 90)
			pitch = 90;
		if (pitch < -90)
			pitch = -90;
		roll %= 360.0f;
		this.yaw %= 360;
		updateViewMatrix();
	}
	
	@Override
	public void render() {
		
	}

	@Override
	public void moveOther() {
		for(Sphere sphere : world.getObjects()){
			if(sphere == null) continue;
			double distance = Maths.getDistanceBetweenPoints(this.position.x, this.position.y, this.position.z, sphere.position.x, sphere.position.y, sphere.position.z);
			Vector3f vecBetween = Vector3f.subtract(sphere.position, this.position);
			vecBetween.setLength((float)(sphere.mass / (distance * distance)));

			if(distance < sphere.scale){
				Vector3f distanceRelativeToSphere = Vector3f.subtract(this.position, sphere.position);
				distanceRelativeToSphere.setLength(sphere.scale + 1f);
				this.position.set(Vector3f.add(sphere.position, distanceRelativeToSphere));
				this.velocity.zero();
			} else {
				this.velocity.add(vecBetween.scale(0.01f));
			}
		}
		this.position.add(new Vector3f(velocity).scale(Window.getFrameTimeSeconds()));
		updateViewMatrix();
	}
	
	public void setWorld(World world){
		this.world = world;
	}

}
