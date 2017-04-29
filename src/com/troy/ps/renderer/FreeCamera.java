package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera implements ICamera {

	private static float speed = 10;
	private float near, far;
	private Vector3f position;
	private Vector3f forward, right, up;
	
	private Matrix4f projectionMatrix, viewMatrix;
	private float fov;

	public FreeCamera(Window window, float fov) {
		this.fov = fov;
		this.position = new Vector3f(0, 100, -100);

		this.near = 0.01f;
		this.far = 100000f;
		updateProjectionMatrix(window);
		updateViewMatrix();
	}

	private Vector3f checkInputs() {
		speed += Mouse.getDWheel() / 10.0f;
		speed = Maths.clamp(0.001f, 1000f, speed);
		Vector3f forward = getForwardVector();
		Vector3f up = getUpVector();
		Vector3f right = getRightVector();
		Vector3f total = new Vector3f(0, 0, 0);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) total.add(forward);

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) total.add(Vector3f.negate(forward));

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) total.add(Vector3f.negate(right));

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) total.add(right);

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) total.add(up);
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT)) total.add(Vector3f.negate(up));

		if (!total.equals(Vector3f.ZERO)) {
			total.setLength(speed);
		}
		updateViewMatrix();
		return total;
	}

	@Override
	public void updateProjectionMatrix(Window window) {
		this.projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(window.getWidth(), window.getHeight(), near, far, fov);
	}

	@Override
	public void onMouseMove() {
		float x = Mouse.getDX() * Options.getMouseXScale();
		float y = Mouse.getDY() * Options.getMouseYScale();
		rotateHorizontal(x);
		rotateVertical(y);
		updateViewMatrix();
	}
	
	public void rotateHorizontal(float degrees) {
		float radins = Maths.degreesToRadians(degrees);
		forward.rotate(up, radins);
		right.rotate(up, radins);
	}

	public void rotateVertical(float degrees) {
		float radins = Maths.degreesToRadians(degrees);
		forward.rotate(right, radins);
		up.rotate(right, radins);
	}

	public void rotateAround(float degrees) {
		float radins = Maths.degreesToRadians(degrees);
		right.rotate(forward, radins);
		up.rotate(forward, radins);
	}

	@Override
	public void updateViewMatrix() {
		this.viewMatrix = GLMaths.createViewMatrix(position, forward, right, up);
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.multiply(projectionMatrix, viewMatrix);
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Matrix4f getViewMatrix(Vector3f pos) {
		return GLMaths.createViewMatrix(pos, forward, right, up);
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector3f newPosition) {
		this.position.set(newPosition);
	}

	@Override
	public void setPotition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	@Override
	public boolean hasNearPlane() {
		return true;
	}

	@Override
	public float getNearPlane() {
		return near;
	}

	@Override
	public boolean hasFarPlane() {
		return true;
	}

	@Override
	public float getFarPlane() {
		return far;
	}

	@Override
	public Vector3f getUpVector() {
		return up;
	}

	@Override
	public Vector3f getForwardVector() {
		return forward;
	}

	@Override
	public Vector3f getRightVector() {
		return right;
	}

	@Override
	public void move(float delta) {
		this.position.add(checkInputs());
	}

}
