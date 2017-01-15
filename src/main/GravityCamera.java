package main;

import com.troyberry.math.Maths;
import com.troyberry.math.Vector3f;
import com.troyberry.opengl.input.Mouse;
import com.troyberry.opengl.util.ICamera;
import com.troyberry.opengl.util.Window;

import renderEngine.MasterRenderer;
import sphere.Sphere;
import world.World;

public class GravityCamera extends ICamera {

	private World world;
	
	public GravityCamera() {
		super(MasterRenderer.NEAR_PLANE);
	}

	@Override
	public void moveRotation() {
		pitch += Mouse.getDY() / 10.0f;
		yaw   += Mouse.getDX() / 10.0f;

		roll %= 360.0f;
		this.yaw %= 360;
		updateViewMatrix();
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
