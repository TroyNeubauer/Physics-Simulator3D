package com.troy.ps.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.*;

import com.troy.ps.main.*;
import com.troy.ps.world.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;

public class PointRenderer {
	
	private PointShader shader;

	public PointRenderer() {
		shader = new PointShader();
	}

	public void render(World world, ICamera camera) {
		prepare(camera);
		Vao vao = world.getVao();
		vao.bind(0, 1, 2, 3);
		glDrawElementsInstanced(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0, OpenCLManager.MAX_PARTICLES);
		vao.unbind(0, 1, 2, 3);
		finish();
	}

	private void prepare(ICamera camera) {
		this.shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		GLUtil.enableAlphaBlending();
		GLUtil.disableDepthWriting();
		GLUtil.disableDepthTesting();
		GLUtil.cullBackFaces(false);
	}

	private void finish() {
		this.shader.stop();
	}

	public void cleanUp() {
		this.shader.cleanUp();
	}
}
