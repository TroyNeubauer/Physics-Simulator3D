package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera extends ICamera {

	private static float speed = 10;

	public FreeCamera(float near) {
		super(near);
	}

	public FreeCamera(ICamera camera) {
		super(camera);
	}

	@Override
	public void onMouseMove() {
		yaw += Mouse.getDX() * Options.getMouseXScale();
		pitch += Mouse.getDY() * Options.getMouseYScale();
	}

	@Override
	public void move() {
		pitch %= 180.0f;
		yaw %= 360.0f;
		roll %= 360.0f;
		position.add(checkInputs());
		updateViewMatrix();
	}

	private Vector3f checkInputs() {
		speed += Mouse.getDWheel() / 10.0f;
		speed = Maths.clamp(0.001f, 1000f, speed);
		Vector3f forward = new Vector3f(Maths.sinFloatDegrees(yaw), 0, -Maths.cosFloatDegrees(yaw));
		forward.normalise();
		Vector3f total = new Vector3f(0, 0, 0);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) total.add(forward);

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) total.add(Vector3f.negate(forward));

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) total.add(forward.rotate(Vector3f.UP, -90));

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) total.add(forward.rotate(Vector3f.UP, 90));

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) total.y += 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT)) total.y -= 1;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) roll += 0.75f;
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) roll -= 0.75f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) roll += 0.75f;
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) Options.pointSize -= 0.02f;
		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) Options.pointSize += 0.02f;

		total.setLength(speed);
		return total;
	}

}
