package com.troy.ps.world;

import com.troyberry.math.*;

public class Body {

	protected Vector3d position;
	protected Vector3d velocity;
	protected Vector3d rotation, rotationVelocity;
	
	private double mass, furthestPoint;

	public Body(Vector3d position, Vector3d velocity, Vector3d rotation, Vector3d rotationVelocity) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.rotationVelocity = rotationVelocity;
	}

	public void update(float delta) {
		this.position.addScaled(velocity, delta);
		this.rotation.addScaled(rotationVelocity, delta);
	}
	
	public Vector3d getPosition() {
		return position;
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3d velocity) {
		this.velocity = velocity;
	}

	public Vector3d getRotation() {
		return rotation;
	}

	public void setRotation(Vector3d rotation) {
		this.rotation = rotation;
	}

	public Vector3d getRotationVelocity() {
		return rotationVelocity;
	}

	public void setRotationVelocity(Vector3d rotationVelocity) {
		this.rotationVelocity = rotationVelocity;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public double getFurthestPoint() {
		return furthestPoint;
	}

}
