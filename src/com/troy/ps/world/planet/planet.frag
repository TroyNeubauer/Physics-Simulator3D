#version 330

in vec3 passColor;
in vec3 passNormal;
in vec3 toLightVector;

out vec4 outColour;

uniform vec3 lightColor;

void main(void) {
	float lighting = dot(passNormal, toLightVector);
	lighting = max(lighting, 0.0f);
	vec3 diffuse = lightColor * lighting;

	outColour = vec4(passColor * diffuse, 1.0f);
}
