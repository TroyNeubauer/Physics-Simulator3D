package com.troy.ps.renderer;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.input.*;
import com.troyberry.opengl.util.*;

public class FreeCamera implements ICamera {

	private SmoothDouble speed = new SmoothDouble(100, 5.0);
	private double near, far;
	private Vector3d position;
	private Vector3d forward, right, up;

	private Matrix4d projectionMatrix, viewMatrix;
	private double fov;

	public FreeCamera(Window window, float fov) {
		this.right = new Vector3d(1, 0, 0);
		this.up = new Vector3d(0, 1, 0);
		this.forward = new Vector3d(0, 0, 1);
		this.fov = fov;
		this.position = new Vector3d(0, 0, 0);

		this.near = Constants.ONE_METER * 10;
		this.far = Constants.ONE_HUNDRED_MILLION_KILOMETERS;
		updateProjectionMatrix(window);
		updateViewMatrix();
	}

	private boolean wireFrame = false, mouseControl;
	Vector3d total = new Vector3d();

	private Vector3d checkInputs(double delta) {
		total.set(0.0f, 0.0f, 0.0f);

		if (Controls.FORWARD.isPressed()) total.add(Vector3d.negate(forward));
		if (Controls.BACKWARD.isPressed()) total.add(forward);

		if (Controls.LEFT.isPressed()) total.add(Vector3d.negate(right));
		if (Controls.RIGHT.isPressed()) total.add(right);

		if (Controls.UP.isPressed()) total.add(up);
		if (Controls.DOWN.isPressed()) total.add(Vector3d.negate(up));
		if (Mouse.hasScrolled()) {
			speed.setTarget(speed.getTarget() + speed.getTarget() * (Mouse.getDWheel() * 30) * Window.getFrameTimeSeconds());
			speed.clamp(0.0000005f, 2500000f);
			Mouse.resetScroll();
		}
		speed.update(delta);

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

	public void rotateHorizontal(double degrees) {
		double radins = Math.toRadians(degrees);
		forward.rotate(up, radins);
		right.rotate(up, radins);
	}

	public void rotateVertical(double degrees) {
		double radins = Math.toRadians(degrees);
		forward.rotate(right, radins);
		up.rotate(right, radins);
	}

	public void rotateAround(double degrees) {
		double radins = Math.toRadians(degrees);
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
	public boolean hasNearPlane() {
		return true;
	}

	@Override
	public boolean hasFarPlane() {
		return true;
	}


	@Override
	public void move(double delta) {
		this.position.add(checkInputs(delta));
	}

	@Override
	public void setUpDirectionDouble(Vector3d newUp) {
		this.up = Vector3d.normalise(newUp);
		this.right = Vector3d.arbitraryOrthogonal(up);
		this.forward = Vector3d.cross(right, up);
	}
	@Override
	public void setUpDirectionFloat(Vector3f newUp) {
		setUpDirectionDouble(newUp.toDouble());
	}

	@Override
	public Matrix getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix getProjectionViewMatrix() {
		return Matrix4d.multiply(projectionMatrix, viewMatrix);
	}

	@Override
	public Matrix getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Vector3f getUpVectorFloat() {
		return up.toFloat();
	}

	@Override
	public Vector3f getForwardVectorFloat() {
		return forward.toFloat();
	}

	@Override
	public Vector3f getRightVectorFloat() {
		return right.toFloat();
	}

	@Override
	public Vector3d getUpVectorDouble() {
		return up;
	}

	@Override
	public Vector3d getForwardVectorDouble() {
		return forward;
	}

	@Override
	public Vector3d getRightVectorDouble() {
		return right;
	}

	@Override
	public Vector3f getPositionFloat() {
		return position.toFloat();
	}

	@Override
	public Vector3d getPositionDouble() {
		return position;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		this.position.set(x, y, z);
	}

	@Override
	public double getNearPlane() {
		return near;
	}

	@Override
	public double getFarPlane() {
		return far;
	}

	@Override
	public void setPosition(Vector3d vec) {
		this.position.set(vec);
	}

	@Override
	public void setPosition(Vector3f vec) {
		this.position.set(vec);
	}

}
