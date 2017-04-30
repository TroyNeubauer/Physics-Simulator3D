package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera implements ICamera {

	private SmoothFloat speed = new SmoothFloat(1000.0f, 10.0f);
	private float near, far;
	private Vector3f position;
	private Vector3f forward, right, up;
	
	private Matrix4f projectionMatrix, viewMatrix;
	private float fov;

	public FreeCamera(Window window, float fov) {
		this.right = 	new Vector3f(1, 0, 0);
		this.up = 		new Vector3f(0, 1, 0);
		this.forward = 	new Vector3f(0, 0, 1);
		this.fov = fov;
		this.position = new Vector3f(0, 0, 0);

		this.near = 0.01f;
		this.far = 100000f;
		updateProjectionMatrix(window);
		updateViewMatrix();
	}
	
	private boolean state = false, wireFrame = false;
	Vector3f total = new Vector3f();
	private Vector3f checkInputs(float delta) {
		total.set(0.0f, 0.0f, 0.0f);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) total.add(Vector3f.negate(forward));
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) total.add(forward);

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) total.add(Vector3f.negate(right));
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) total.add(right);

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) total.add(up);
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT)) total.add(Vector3f.negate(up));
		if (Mouse.hasScrolled()) {
			speed.setTarget(speed.getTarget() + speed.getTarget() * (Mouse.getDWheel() * 30) * Window.getFrameTimeSeconds());
			speed.clamp(0.0000005f, 2500000f);
			Mouse.resetScroll();
		}
		System.out.println(speed.get());
		speed.update(delta);
		speed.clamp(0.000000001f, 100000f);
		
		total.setLength(delta * speed.get());

		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			if (!state) {
				state = true;
				wireFrame = !wireFrame;
				GLUtil.goWireframe(wireFrame);
			}
		} else state = false;

		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) rotateAround(-Window.getFrameTimeSeconds() * 45.0f);
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) rotateAround(Window.getFrameTimeSeconds() * 45.0f);
		updateViewMatrix();
		return total;
	}

	@Override
	public void onMouseMove() {
		if (Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
			rotateVertical(Mouse.getDY() * Options.getMouseYScale());
			rotateHorizontal(Mouse.getDX() * Options.getMouseXScale());
		} else {
			Mouse.setGrabbed(false);
			Mouse.resetDeltas();
		}
		
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
	public void updateProjectionMatrix(Window window) {
		this.projectionMatrix = GLMaths.createPerspectiveProjectionMatrix(window.getWidth(), window.getHeight(), near, far, fov);
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
		if(viewMatrix == null) updateViewMatrix();
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
		this.position.add(checkInputs(delta));
	}

	@Override
	public void setUpDirection(Vector3f newUp) {
		this.up = Vector3f.normalise(newUp);
		this.right = Vector3f.arbitraryOrthogonal(up);
		this.forward = Vector3f.cross(right, up);
	}

}
