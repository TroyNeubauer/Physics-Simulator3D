package world;

import java.util.*;

import com.troyberry.math.Maths;
import com.troyberry.math.Vector3f;
import com.troyberry.opengl.util.ICamera;
import com.troyberry.opengl.util.Window;

import main.GravityCamera;
import sphere.Sphere;
import star.Star;
import update.UpdateMaster;

public class World {
	
	private final ICamera camera;
	private volatile List<Sphere> objects;
	private volatile List<Sphere> toRemove;
	private volatile  List<Sphere> toAdd;
	private List<Galaxy> galaxyies;
	
	private Vector3f lightDirection = new Vector3f(0.25f, -1f, 0.25f).normalised();
	
	public World(ICamera cam, boolean backgroundStars) {
		if(cam instanceof GravityCamera) ((GravityCamera)cam).setWorld(this);
		this.camera = cam;
		this.objects = new ArrayList<Sphere>(10000);
		this.galaxyies = new ArrayList<Galaxy>();
		this.toRemove = new ArrayList<Sphere>();
		this.toAdd = new ArrayList<Sphere>();
		if(backgroundStars){
			for(int i = 0; i < 3000; i++){
				addSphereUnsafe(new Star(Vector3f.randomVector(750000f), Maths.randRange(25, 200)));
			}
		}
	}
	
	/**
	 * Called on the Open GL thread to interpolate the planets to their next position
	 */
	public void updateOnRender() {
		Vector3f cache = new Vector3f();
		for(Sphere sphere : getObjects()) {
			if(sphere == null || !(sphere instanceof Sphere))continue;
			cache.set(sphere.velocity);
			sphere.position.add(cache.scale(Window.getFrameTimeSeconds()));
		}
	}
	
	/**
	 * Calculates the new radiuses for the galaxies and handles new spheres being added and removed
	 * This can be only be called on the updater thread or when no loops are looping through the contents in the list of planets.
	 */
	public void update(){
		for(Galaxy g : galaxyies) {
			float radius = 0;
			Sphere center = g.getCenterSphere();
			for(Sphere s : g.getSpheres()) {
				float distance = (float) Maths.approximateDistanceBetweenPoints(s.position, center.position);
				radius = Math.max(distance, radius);
			}
			g.currentRadius = radius;
		}
		synchronized (toAdd) {
			for(Sphere m : toAdd) {
				objects.add(m);
			}
			toAdd.clear();
		}
		synchronized (toRemove) {
			for(Sphere m : toRemove){
				objects.remove(m);
			}
			toRemove.clear();
		}
	}
	
	/**
	 * Gets the average velocity for the area
	 * @return The velocity
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
	
	/**
	 * Adds a sphere to be removed as soon as possible
	 * @param sphere The sphere to remove
	 */
	public void remove(Sphere sphere){
		synchronized (toRemove) {
			toRemove.add(sphere);
		}
	}
	
	/**
	 * Adds a new sphere to the list of spheres that exist in the world. If this is called on another thread, 
	 * the sphere will wait patiently until a time to add it to the list arises IE calling update
	 * {@link World#update()}
	 * @param s
	 */
	public void addSphere(Sphere s) {
		
		if(s == null)return;
		s.setWorld(this);
		synchronized (toAdd) {
			toAdd.add(s);
		}
	}
	
	/**
	 * Directly adds a sphere to the list of spheres that exist in the world. This must only be called when no loops on other
	 * threads are looping through the objects list otherwise a {@link ConcurrentModificationException} will be thrown
	 * @param s The sphere to add
	 */
	public void addSphereUnsafe(Sphere s) {
		
		if(s == null)return;
		s.setWorld(this);
		objects.add(s);
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

	/**
	 * Returns a copy of the list of objects in the world. This is to be used when rendering to avoid concurrent modification exceptions
	 * @return The new list
	 */
	public List<Sphere> getObjects() {
		return new ArrayList<Sphere>(objects);
	}
	
	public int getSphereCount(){
		return objects.size();
	}
	
	public void addGalaxy(Galaxy g){
		if(g == null)return;
		galaxyies.add(g);
	}
	
	/**
	 * Returns a reference to the original list of objects. This list should never be added to directly as this will most
	 * likely result in a concurrent modification exception. Therefore spawning and deleting methods exist that will change
	 * the original list when the time is right.
	 * @return The original list (be careful not a copy)<br>
	 * {@link World#addSphere(Sphere)}<br>
	 * {@link World#remove(Sphere)}
	 */
	public List<Sphere> getActualObjectsArray() {
		return objects;
	}

}
