package world;

import java.util.ArrayList;
import java.util.List;

import com.troy.troyberry.math.*;
import com.troy.troyberry.opengl.util.ICamera;
import com.troy.troyberry.opengl.util.Window;

import main.GravityCamera;
import utils.Updater;

public class World {
	
	private final ICamera camera;
	private volatile List<Sphere> objects;
	private List<Sphere> toRemove;
	private List<Galaxy> galaxyies;
	
	private Vector3f lightDirection = new Vector3f(0.25f, -1f, 0.25f).normalised();
	
	/**
	 * Called on the Open GL thread to interpolate the planets to their next position
	 */
	public void updateOnRender() {
		Vector3f cache = new Vector3f();
		for(Sphere sphere : getObjects()) {
			if(sphere == null || !(sphere instanceof Sphere))continue;
			cache.set(sphere.velocity);
			sphere.position.add(cache.scale(Window.getFrameTimeSeconds() / (float)Updater.UPS));
		}
	}
	
	/**
	 * Called on the update thread to calculate the new velocities for the planets
	 */
	public void update(){
		for(Sphere sphere1 : objects) {
			for(Sphere sphere2 : objects) {
				if(sphere1.equals(sphere2))continue;
				double distance = Maths.getDistanceBetweenPoints(sphere1.position.x, sphere1.position.y, sphere1.position.z, sphere2.position.x, sphere2.position.y, sphere2.position.z);
				if(distance < sphere1.scale + sphere2.scale){
					if(sphere1.mass > sphere2.mass){
						toRemove.add(sphere2);
					} else {
						toRemove.add(sphere1);
					}
				}
				Vector3f vecBetween = Vector3f.subtract(sphere2.position, sphere1.position);
				vecBetween.setLength((float)(sphere2.mass / (distance * distance)));
				sphere1.velocity.add(vecBetween);
				
			}
		}
		for(Galaxy g : galaxyies) {
			float radius = 0;
			Sphere center = g.getCenterSphere();
			for(Sphere s : g.getSpheres()) {
				float distance = (float) Maths.approximateDistanceBetweenPoints(s.position, center.position);
				radius = Math.max(distance, radius);
			}
			g.currentRadius = radius;
		}
		for(Sphere m : toRemove){
			objects.remove(m);
		}
		toRemove.clear();
	}
	
	/**
	 * Gets the average velocity for the area
	 * @return
	 */
	public Vector3f getVelocity(Vector3f position, float radius) {
		int samples = 0;
		Vector3f total =  new Vector3f();
		for(Sphere s : getObjects()){
			if(Maths.getDistanceBetweenPoints(position, s.position) < radius){
				if(s.velocity.isRational()){
					samples++;
					total.add(s.velocity);
				}
			}
		}
		return total.scale(1.0f / (float)samples);
	}
	
	public World(ICamera cam, boolean backgroundStars) {
		if(cam instanceof GravityCamera) ((GravityCamera)cam).setWorld(this);
		this.camera = cam;
		this.objects = new ArrayList<Sphere>(10000);
		this.galaxyies = new ArrayList<Galaxy>();
		this.toRemove = new ArrayList<Sphere>();
		if(backgroundStars){
			for(int i = 0; i < 3000; i++){
				addSphere(new Sphere(Vector3f.randomVector(5000000f), Maths.randRange(15, 40)));
			}
		}
	}

	public ICamera getCamera() {
		return camera;
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}

	public void setLightDirection(Vector3f lightDir) {
		this.lightDirection.set(lightDir);
	}

	public List<Sphere> getObjects() {
		return new ArrayList<Sphere>(objects);
	}

	public void addSphere(Sphere s) {
		if(s == null)return;
		s.setWorld(this);
		objects.add(s);
	}
	
	public int getSphereCount(){
		return objects.size();
	}
	
	public void addGalaxy(Galaxy g){
		if(g == null)return;
		galaxyies.add(g);
	}

}
