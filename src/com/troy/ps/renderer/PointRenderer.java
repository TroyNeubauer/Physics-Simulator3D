package com.troy.ps.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

import com.troy.ps.main.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;

public class PointRenderer {
	private PointShader shader;

	public PointRenderer() {
		shader = new PointShader();
	}

	public void render(Vao vao, Matrix4f projectionMatrix) {
		prepare(projectionMatrix);
		glEnable(GL_PROGRAM_POINT_SIZE);
		vao.bind(0,1);
		glDrawArrays(GL_POINTS, 0, vao.getIndexCount());
		vao.unbind(0,1);
		finish();
	}

	public void cleanUp() {
		this.shader.cleanUp();
	}

	private void prepare(Matrix4f projectionMatrix) {
		this.shader.start();
		shader.pointSize.loadFloat(Options.pointSize);
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		GLUtil.enableAdditiveBlending();
		GLUtil.disableDepthWriting();
		GLUtil.disableDepthTesting();
		GLUtil.cullBackFaces(false);
	}
	
	private void prepareInstance(Vector3f position) {
		
	}

	private void finish() {
		this.shader.stop();
	}
}
