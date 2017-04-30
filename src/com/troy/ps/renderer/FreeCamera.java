package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera implements ICamera {

	private SmoothFloat speed = new SmoothFloat(10.0f, 5.0f);
	private float near, far;
	private Vector3f position;
	private Vector3f forward, right, up;

	private Matrix4f projectionMatrix, viewMatrix;
	private float fov;

	public FreeCamera(Window window, float fov) {
		this.right = new Vector3f(1, 0, 0);
		this.up = new Vector3f(0, 1, 0);
		this.forward = new Vector3f(0, 0, 1);
		this.fov = fov;
		this.position = new Vector3f(0, 0, 0);

		this.near = Constants.ONE_METER * 5;
		this.far = Constants.ONE_HUNDRED_MILLION_KILOMETERS;
		updateProjectionMatrix(window);
		updateViewMatrix();
	}

	private boolean wireFrame = false, mouseControl;
	Vector3f total = new Vector3f();

	private Vector3f checkInputs(float delta) {
		total.set(0.0f, 0.0f, 0.0f);

		if (Controls.FORWARD.isPressed()) total.add(Vector3f.negate(forward));
		if (Controls.BACKWARD.isPressed()) total.add(forward);

		if (Controls.LEFT.isPressed()) total.add(Vector3f.negate(right));
		if (Controls.RIGHT.isPressed()) total.add(right);

		if (Controls.UP.isPressed()) total.add(up);
		if (Controls.DOWN.isPressed()) total.add(Vector3f.negate(up));
		if (Mouse.hasScrolled()) {
			speed.setTarget(speed.getTarget() + speed.getTarget() * (Mouse.getDWheel() * 30) * Window.getFrameTimeSeconds());
			speed.clamp(0.0000005f, 2500000f);
			Mouse.resetScroll();
		}
		speed.update(delta);
		speed.clamp(0.000000001f, 100000f);

		total.setLength(delta * speed.get());

		if (Controls.POLYGON.hasBeenPressed()) {
			wireFrame = !wireFrame;
			GLUtil.goWireframe(wireFrame);
		}

		if (Controls.ROTATE_LEFT.isPressed()) rotateAround(-Window.getFrameTimeSeconds() * 45.0f);
		if (Controls.ROTATE_RIGHT.isPressed()) rotateAround(Window.getFrameTimeSeconds() * 45.0f);
		updateViewMatrix();
		return total;
	}

	@Override
	public void onMouseMove() {
		if(Controls.LOOK.hasBeenPressed()) mouseControl = !mouseControl;
		
		if (mouseControl) {
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
		if (viewMatrix == null) updateViewMatrix();
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
		return false;
	}

	@Override
	public float getFarPlane() {
		return -1;
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
