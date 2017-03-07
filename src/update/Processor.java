package update;

import java.util.List;

import com.troyberry.logging.Timer;
import com.troyberry.math.Maths;
import com.troyberry.math.Vector3f;
import com.troyberry.util.thread.Task;

import main.PSGameSettings;
import sphere.Sphere;
import world.World;

class Processor extends Task {

	private int startIndex, endIndex;
	private List<Sphere> allPlanets;
	private World world;
	private static int count = 0;

	public Processor(int startIndex, int endIndex, List<Sphere> allPlanets, World world) {
		super(count++);
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.allPlanets = allPlanets;
		this.world = world;
	}

	@Override
	public void onRun() {
		Timer t = new Timer();
		if ((endIndex - startIndex) == 0) return;
		for (int i = startIndex; i < endIndex; i++) {
			Sphere sphere = null;
			try {
				sphere = allPlanets.get(i);
			} catch (Exception e) {
				continue;
			}
			List<Sphere> objects = world.getActualObjectsArray();
			for (Sphere sphere2 : objects) {
				if (sphere.equals(sphere2)) continue;
				double distance = Maths.getDistanceBetweenPoints(sphere.position.x, sphere.position.y, sphere.position.z, sphere2.position.x,
						sphere2.position.y, sphere2.position.z);
				Vector3f vecBetween = Vector3f.subtract(sphere2.position, sphere.position);
				vecBetween.setLength((float) (sphere2.mass / (distance * distance)) * PSGameSettings.GRAVITY_CONSTANT);
				sphere.velocity.add(vecBetween);

			}
		}
		t.stop();
	}
}