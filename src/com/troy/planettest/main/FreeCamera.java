package com.troy.planettest.main;

import com.troy.troyberry.math.*;
import com.troy.troyberry.opengl.input.Mouse;
import com.troy.troyberry.opengl.util.*;

import renderEngine.MasterRenderer;
import utils.Controls;
import utils.Updater;

public class FreeCamera extends ICamera {
	
	private SmoothFloat speed = new SmoothFloat(0.1f, 10);
	
	public FreeCamera() {
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
		yaw %= 360.0f;
		roll %= 360.0f;
	}
	
	@Override
	public void moveOther() {
		if(Controls.BANK_LEFT.isPressedUpdateThread())roll += 3;
		if(Controls.BANK_RIGHT.isPressedUpdateThread())roll -= 3;
		float dx = (float) (Math.cos(Math.toRadians(yaw - 90)));
		float dz = (float) (Math.sin(Math.toRadians(yaw - 90)));
		Vector2f forward = checkInputs(new Vector2f(dx, dz));
		Vector3f temp = new Vector3f(forward.x, 0, forward.y);
		
		if(Controls.SPACE.isPressedUpdateThread()) {
			temp.y += speed.get() * Window.getFrameTimeSeconds();
		}
		if(Controls.SHIFT.isPressedUpdateThread()){
			temp.y -= speed.get() * Window.getFrameTimeSeconds();
		}
		temp.setLength(speed.get());
		this.position.add(temp);
		speed.update(Window.getFrameTimeSeconds());
		speed.setTarget(speed.getTarget() + speed.getTarget() * (Mouse.getDWheel() * 3) * Window.getFrameTimeSeconds());
		speed.clamp(0.000005f, 999999f);
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

	@Override
	public void render() {
		
	}
}
