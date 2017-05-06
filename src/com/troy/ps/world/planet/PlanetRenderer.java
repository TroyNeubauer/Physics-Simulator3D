package com.troy.ps.world.planet;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import com.troy.ps.main.*;
import com.troy.ps.world.*;
import com.troyberry.math.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.opengl.util.*;

public class PlanetRenderer {

	private PlanetShader shader;

	public PlanetRenderer() {
		shader = new PlanetShader();
	}

	public void render(Planet planet, ICamera camera, Light light) {
		prepare(planet, camera, light);
		Vao mesh = planet.getMesh();
		mesh.bind();
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
		mesh.unbind();
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		finish();
	}

	private boolean cull = false;

	private void prepare(Planet planet, ICamera camera, Light light) {
		if (Controls.BACK_FACE_CULL.hasBeenPressed()) cull = !cull;
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		Vector3d rot = planet.getRotation();
		shader.modelMatrix
				.loadMatrix(Matrix4d.multiply(GLMaths.createTransformationMatrix(planet.getPosition()), GLMaths.createRotationMatrix(rot.x, rot.y, rot.z)));
		shader.lightPos.loadVec3(light.getPosition());
		shader.lightColor.loadVec3(light.getColor());
		shader.ambientLighting.loadVec3(0.1f);
		shader.enableLighting.loadBoolean(false);
		GLUtil.disableBlending();
		GLUtil.enableDepthTesting();
		GLUtil.cullBackFaces(cull);
	}

	private void finish() {
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

}
