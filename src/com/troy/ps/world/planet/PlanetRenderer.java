package com.troy.ps.world.planet;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;

public class PlanetRenderer {

	private PlanetShader shader;

	public PlanetRenderer() {
		shader = new PlanetShader();
	}

	public void render(Planet planet, ICamera camera) {
		prepare(planet, camera);
		Vao mesh = planet.getMesh();
		mesh.bind();
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
		mesh.unbind();
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		finish();
	}

	private void prepare(Planet planet, ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		Vector3f rot = planet.getRotation();
		shader.modelMatrix.loadMatrix(Matrix4f.multiply(GLMaths.createTransformationMatrix(planet.getPosition()), GLMaths.createRotationMatrix(rot.x, rot.y, rot.z)));
		GLUtil.disableBlending();
		GLUtil.enableDepthTesting();
		GLUtil.cullBackFaces(true);
	}

	private void finish() {
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

}
