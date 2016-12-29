package sphere;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a level of detail
 * @author Troy Neubauer
 *
 */
public class LodManager {
	private List<Lod> distances;
	
	public LodManager() {
		distances = new ArrayList<Lod>();
	}
	
	public void add(int level, float distance){
		Lod lod = new Lod(level, distance);
		if(!distances.contains(lod)){
			distances.add(lod);
		}
	}
	
	public int get(float distance){
		int level = 0;
		for(int i = 0; i < distances.size(); i++){
			if(distance > distances.get(i).getDistance()){
				return i;
			}
		}
		
		return level;
	}
}
