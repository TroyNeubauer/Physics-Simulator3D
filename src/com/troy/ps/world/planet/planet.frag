#version 330

in vec3 passColor;
in vec3 passNormal;
in vec3 toLightVector;

out vec4 outColour;

uniform vec3 lightColor;
uniform vec3 ambientLighting;
uniform float enableLighting;

void main(void) {
	if (enableLighting > 0.5f) {
		float lighting = dot(passNormal, toLightVector);
		lighting = max(lighting, 0.0f);
		vec3 diffuse = lightColor * lighting;

		outColour = vec4(passColor * diffuse, 1.0f);
		outColour += vec4(ambientLighting, 0.0f);
	} else {
		outColour = vec4(passColor, 1.0f);
	}
}
