package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera implements ICamera {

	private static float speed = 10;
	private float pitch, yaw, near, far;
	private Vector3f position;
	private Matrix4f projectionMatrix, viewMatrix;
	private float fov;

	public FreeCamera(float fov) {
		this.pitch = 0;
		this.yaw = 0;
		this.fov = fov;
		this.position = new Vector3f();

		this.near = 0.01f;
		this.far = 100000f;
		updateProjectionMatrix();
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
			updateViewMatrix();
		}
		return total;
	}

	@Override
	public void updateProjectionMatrix() {
		this.projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(Window.getInstance().getWidth(), Window.getInstance().getHeight(), near, far, fov);
	}

	@Override
	public void updateViewMatrix() {
		this.viewMatrix = GLMaths.createViewMatrix(position, pitch, yaw, 0);
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
		return GLMaths.createViewMatrix(pos, pitch, yaw, 0);
	}

	@Override
	public float getXRotation() {
		return pitch;
	}

	@Override
	public float getYRotation() {
		return yaw;
	}

	@Override
	public float getZRotation() {
		return 0;
	}

	@Override
	public Quaternion getRotation() {
		return new Quaternion().setEulerAngles(pitch, yaw, 0);
	}

	@Override
	public void setRotation(Quaternion newRotation) {
		this.pitch = newRotation.getPitch();
		this.yaw = newRotation.getYaw();
	}

	@Override
	public void setRotation(float x, float y, float z) {
		this.pitch = x;
		this.yaw = y;
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
		return Vector3f.UP;
	}

	@Override
	public Vector3f getForwardVector() {
		return Vector3f.NEG_Z;
	}

	@Override
	public Vector3f getRightVector() {
		return Vector3f.rotate(getForwardVector(), Vector3f.UP, Maths.PI / 2.0f);
	}

	@Override
	public void move(float delta) {
		this.position.add(checkInputs());
	}

	@Override
	public void onMouseMove() {
		float x = Mouse.getDX() * Options.getMouseXScale();
		float y = Mouse.getDY() * Options.getMouseYScale();
		this.pitch += y;
		this.yaw += x;
		updateViewMatrix();
	}

}
