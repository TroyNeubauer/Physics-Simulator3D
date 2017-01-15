package line;

import java.util.Map;

import com.troyberry.color.TroyColor;
import com.troyberry.math.Vector3f;
import com.troyberry.opengl.mesh.Vao;
import com.troyberry.opengl.util.GlUtil;
import com.troyberry.opengl.util.ICamera;

import world.World;

public class LineRenderer {
	
	private LineShader shader;
	private Vao vao;
	
	public LineRenderer(ICamera camera) {
		this.shader = new LineShader();
		vao = Vao.create();
	}
	
	public void render(World world, Map<Vector3f, TroyColor> points) {	
		prepare(world.getCamera());
		float[] data = new float[points.keySet().size() * 3];
		int i = 0;
		for(Vector3f vec : points.keySet()) {
			data[i * 3 + 0] = vec.x;
			data[i * 3 + 1] = vec.y;
			data[i * 3 + 2] = vec.z;
			i++;
		}
		i = 0;
		for(TroyColor vec : points.values()) {
			data[i * 3 + 0] = vec.getRed();
			data[i * 3 + 1] = vec.getGreen();
			data[i * 3 + 2] = vec.getBlue();
			i++;
		}
		vao.getVbo(0).storeData(data);
		vao.getVbo(1).storeData(data);
		
		
		finish();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(ICamera camera){
		vao.bind(0, 1);
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		GlUtil.enableAlphaBlending();
		GlUtil.enableDepthTesting();
		GlUtil.cullBackFaces(true);
	}
	
	private void finish() {
		shader.stop();
	}	
}
