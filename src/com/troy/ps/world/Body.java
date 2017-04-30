package com.troy.ps.world;

import com.troyberry.math.*;

public class Body {

	protected Vector3f position;
	protected Vector3f velocity;
	protected Vector3f rotation, rotationVelocity;
	
	private float mass, furthestPoint;

	public Body(Vector3f position, Vector3f velocity, Vector3f rotation, Vector3f rotationVelocity) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.rotationVelocity = rotationVelocity;
	}

	public void update(float delta) {
		this.position.addScaled(velocity, delta);
		this.rotation.addScaled(rotationVelocity, delta);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getRotationVelocity() {
		return rotationVelocity;
	}

	public void setRotationVelocity(Vector3f rotationVelocity) {
		this.rotationVelocity = rotationVelocity;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public float getFurthestPoint() {
		return furthestPoint;
	}

}
