package com.troy.ps.world.planet;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import com.troy.ps.main.Controls;
import com.troy.ps.world.Light;
import com.troyberry.math.*;
import com.troyberry.opengl.loading.texture.Texture;
import com.troyberry.opengl.mesh.Vao;
import com.troyberry.opengl.util.*;

public class PlanetRenderer {

	private PlanetShader shader;

	public PlanetRenderer() {
		shader = new PlanetShader();
	}

	public void render(Planet planet, ICamera camera, Light light, Texture planetTexture) {
		prepare(planet, camera, light, planetTexture);
		Vao mesh = planet.getMesh();
		mesh.bind(0, 4);
		glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
		mesh.unbind(0, 4);
		finish();
	}

	private void prepare(Planet planet, ICamera camera, Light light, Texture planetTexture) {
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		Vector3d rot = planet.getRotation();
		shader.modelMatrix.loadMatrix(GLMaths.createTransformationMatrix(planet.getPosition()));
		shader.lightPos.loadVec3(light.getPosition());
		shader.lightColor.loadVec3(light.getColor());
		shader.disableLighting.loadBoolean(true);

		planetTexture.bind(0, shader.planetTexture);

		GLUtil.disableBlending();
		GLUtil.enableDepthTesting();
		GLUtil.cullBackFaces(true);
	}

	private void finish() {
		shader.stop();
	}

	public void delete() {
		shader.delete();
	}

}
