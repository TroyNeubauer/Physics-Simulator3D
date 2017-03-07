package main;

import com.troyberry.math.*;
import com.troyberry.opengl.input.Mouse;
import com.troyberry.opengl.util.ICamera;
import com.troyberry.opengl.util.Window;

import renderEngine.MasterRenderer;

public class FreeCamera extends ICamera {
	
	private SmoothFloat speed = new SmoothFloat(10000f, 10);
	
	public FreeCamera() {
		super(MasterRenderer.NEAR_PLANE);
	}

	@Override
	public void onMouseMove() {
		pitch += Mouse.getDY() / 10.0f;
		yaw   += Mouse.getDX() / 10.0f;
		
		pitch %= 360.0f;
		yaw %= 360.0f;
		roll %= 360.0f;
	}
	
	@Override
	public void move() {
		if(Controls.BANK_LEFT.isPressedUpdateThread())roll += 3;
		if(Controls.BANK_RIGHT.isPressedUpdateThread())roll -= 3;
		float dx = (float) (Math.cos(Math.toRadians(yaw - 90)));
		float dz = (float) (Math.sin(Math.toRadians(yaw - 90)));
		Vector2f forward = checkInputs(new Vector2f(dx, dz));
		Vector3f temp = new Vector3f(forward.x, 0, forward.y);
		
		if(Controls.SPACE.isPressedUpdateThread()) {
			temp.y += 1;
		}
		if(Controls.SHIFT.isPressedUpdateThread()){
			temp.y -= 1;
		}
		temp.setLength(speed.get());
		this.position.add(temp.scale(Window.getFrameTimeSeconds()));
		speed.update(Window.getFrameTimeSeconds());
		speed.setTarget(speed.getTarget() + speed.getTarget() * (Mouse.getDWheel() * 3) * Window.getFrameTimeSeconds());
		speed.clamp(0.0000005f, 2500000f);
		updateViewMatrix();
	}

	private Vector2f checkInputs(Vector2f forward) {
		Vector2f total = new Vector2f(0f, 0f);
			if (Controls.FORWARD.isPressedUpdateThread()) {
				total.add(forward);
			}
			if (Controls.BACKWARD.isPressedUpdateThread()) {
				total.add(Vector2f.negate(forward));
			}
			if (Controls.LEFT.isPressedUpdateThread()) {
				total.add(forward.rotate(-90));
			}
			if (Controls.RIGHT.isPressedUpdateThread()) {
				total.add(forward.rotate(90));
			}
		
		return total;
	}
}
