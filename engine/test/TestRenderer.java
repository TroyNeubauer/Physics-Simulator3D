package test;

import org.lwjgl.opengl.*;

import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.GLUtil;

public class TestRenderer {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	
	private TestShader shader;
	private Vao vao;
	
	public TestRenderer() {
		shader = new TestShader();
		vao = Vao.create();
		vao.createAttribute(0, POSITIONS, 2);
	}

	public void renderRect(Vector2f position, Vector2f size, Vector3f color) {
		shader.start();
		GLUtil.disableBlending();
		vao.bind(0);
		shader.transform.loadVec4(position.x, position.y, size.x, size.y);
		shader.color.loadVec3(color);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		vao.unbind(0);
		shader.stop();
	}

}
