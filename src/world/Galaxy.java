package world;

import java.util.ArrayList;
import java.util.List;

import com.troyberry.math.*;
import com.troyberry.opengl.util.GLColorUtil;

import main.PSGameSettings;
import planet.Planet;
import sphere.Sphere;
import star.Star;

public class Galaxy {
	
	private World world;
	String name;
	private float size, starScale;
	private Sphere center;
	private List<Sphere> spheres;
	protected float currentRadius;
	
	public Galaxy(String name, World worldin) {
		
		this.name = name;
		this.world = worldin;
		this.spheres = new ArrayList<Sphere>();
		
	}
	
	public static void createStandardGalaxy (String name, int bands, int groupsPerBand, long bandLength, long diskHeight, int spheresPerGroup, 
			World worldin, Vector3f galaxyPosition, Vector3f galaxyRotation, Vector3f galaxyVelocity) {
		
		Galaxy g = new Galaxy(name, worldin);
		int slices = groupsPerBand;
		float distance = 0;
		double angleChange = 0;
		float distanceFromLast = bandLength;
		float LastGroupX = -1;
		float LastGroupZ = -1;
		g.size = bands * bandLength * groupsPerBand;
		double slice = Math.PI * 2 / slices;
		g.starScale = (g.size / 150000.0f);
		g.center = new BlackHole(new Vector3f(galaxyPosition), new Vector3f(galaxyVelocity), new Vector3f(5, 5, 5), 20 * g.starScale, (float)Math.pow(1500 * g.starScale, 3.0));
		worldin.addSphere(g.center);
		Matrix4f matrix = new Matrix4f();
		matrix.rotate(galaxyRotation.x, new Vector3f(1, 0, 0));
		matrix.rotate(galaxyRotation.y, new Vector3f(0, 1, 0));
		matrix.rotate(galaxyRotation.z, new Vector3f(0, 0, 1));
		for(int inter = 0; inter < bands; inter++) {
			for (int i = 0; i < slices; i++) {
				//Generate group
				double angle = i * -slice + angleChange;
				distance += bandLength;
				float groupX  = Maths.sinFloat(angle) * distance;
				float groupZ = Maths.cosFloat(angle) * distance;
				if(LastGroupX != -1 && LastGroupZ != -1) {
					distanceFromLast = (float) Maths.getDistanceBetweenPoints(groupX, groupZ, LastGroupX, LastGroupZ) / 1.8f;
				}
				for(int sphere = 0; sphere < spheresPerGroup; sphere++) {
					double thisAngle = Maths.randRange(-Math.PI * 2, Math.PI * 2);
					float thisDistance = Maths.randRange(0, distanceFromLast);
					float x = Maths.sinFloat(thisAngle) * thisDistance;
					float z = Maths.cosFloat(thisAngle) * thisDistance;
					Sphere s = new Star(new Vector3f(groupX + x, Maths.randRange(-diskHeight, diskHeight), groupZ + z), new Vector3f(),
							GLColorUtil.randomStarColor(25), Maths.randRange(10.0f, 50.0f), 
							(float) Math.pow(Maths.randRange(5.0f, 30.0f) * g.starScale, 3.0));
					double starDistance = Maths.getDistanceBetweenPoints(g.center.position.x, g.center.position.y, g.center.position.z, s.position.x, s.position.y, s.position.z);
					Vector3f vecBetween = Vector3f.subtract(g.center.position, s.position);
					vecBetween.setLength((float)(Math.pow(PSGameSettings.GRAVITY_CONSTANT * g.center.mass / starDistance, 0.5) * 0.60));
					vecBetween.rotate(new Vector3f(0, 1, 0), (float)Math.toRadians(90));
					s.velocity.set(vecBetween);
					s.velocity = s.velocity.mul(matrix);
					s.position = s.position.mul(matrix);
					s.position.add(galaxyPosition);
					s.velocity.add(galaxyVelocity);
					worldin.addSphere(s);
					g.spheres.add(s);
				}
				LastGroupX = groupX;
				LastGroupZ = groupZ;
				
			
			}
			distance = 0;
			angleChange += Math.PI / (bands / 2.0);
		}
		worldin.addGalaxy(g);
	}

	public List<Sphere> getSpheres() {
		return spheres;
	}

	public Sphere getCenterSphere() {
		return center;
	}	
}
