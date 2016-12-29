package world;

import java.util.ArrayList;
import java.util.List;

import com.troy.troyberry.math.*;
import com.troy.troyberry.opengl.util.GLColorUtil;

public class Galaxy {
	
	private long diskHeight;
	private World world;
	String name;
	private float size, starScale;
	private Sphere center;
	private List<Sphere> spheres;
	protected float currentRadius;
	
	public Galaxy(String name, int bands, int groupsPerBand, long bandLength, long diskHeight, int spheresPerGroup, 
			World worldin, Vector3f galaxyPosition, Vector3f galaxyRotation, Vector3f galaxyVelocity) {
		
		this.name = name;
		this.diskHeight = diskHeight;
		this.world = worldin;
		this.spheres = new ArrayList<Sphere>();
		int slices = groupsPerBand;
		float distance = 0;
		double angleChange = 0;
		float distanceFromLast = bandLength;
		float LastGroupX = -1;
		float LastGroupZ = -1;
		this.size = bands * bandLength * groupsPerBand;
		double slice = Math.PI * 2 / slices;
		this.starScale = (size / 150000.0f);
		center = new Sphere(new Vector3f(galaxyPosition), new Vector3f(galaxyVelocity), new Vector3f(50, 50, 50), 300 * starScale, (float)Math.pow(500 * starScale, 3.0));
		world.addSphere(center);
		Matrix4f matrix = new Matrix4f();
		matrix.rotate(galaxyRotation.x, new Vector3f(1, 0, 0));
		matrix.rotate(galaxyRotation.y, new Vector3f(0, 1, 0));
		matrix.rotate(galaxyRotation.z, new Vector3f(0, 0, 1));
		for(int inter = 0; inter < bands; inter++) {
			for (int i = 0; i < slices; i++) {
				//Generate group
				double angle = i * slice + angleChange;
				distance += bandLength;
				float groupX  = Maths.sin(angle) * distance;
				float groupZ = Maths.cos(angle) * distance;
				if(LastGroupX != -1 && LastGroupZ != -1) {
					distanceFromLast = (float) Maths.getDistanceBetweenPoints(groupX, groupZ, LastGroupX, LastGroupZ) / 1.8f;
				}
				for(int sphere = 0; sphere < spheresPerGroup; sphere++) {
					double thisAngle = Maths.randRange(-Math.PI * 2, Math.PI * 2);
					float thisDistance = Maths.randRange(0, distanceFromLast);
					float x = Maths.sin(thisAngle) * thisDistance;
					float z = Maths.cos(thisAngle) * thisDistance;
					Sphere s = new Sphere(new Vector3f(groupX + x, Maths.randRange(-diskHeight, diskHeight), groupZ + z), new Vector3f(),
							GLColorUtil.randomStarColor(25), Maths.randRange(1.0f, 30.0f), 
							(float) Math.pow(Maths.randRange(5.0f, 30.0f) * starScale, 3.0));
					
					Vector2f vel = new Vector2f(s.position.x, s.position.z).rotate(85);
					float distFromCenter = Maths.getDistanceBetweenPoints(galaxyPosition, s.position);
					vel.setLength(distFromCenter / 4000 * starScale * starScale);
					s.velocity.set(vel.x, 0, vel.y);
					s.velocity = s.velocity.mul(matrix);
					s.position = s.position.mul(matrix);
					s.position.add(galaxyPosition);
					s.velocity.add(galaxyVelocity);
					world.addSphere(s);
					spheres.add(s);
				}
				LastGroupX = groupX;
				LastGroupZ = groupZ;
				
			
			}
			distance = 0;
			angleChange += Math.PI / (bands / 2.0);
		}
		worldin.addGalaxy(this);
	}

	public List<Sphere> getSpheres() {
		return spheres;
	}

	public Sphere getCenterSphere() {
		return center;
	}	
}
