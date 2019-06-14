#version 330

in vec3 passColor;
in vec3 passNormal;
in vec3 toLightVector;

out vec4 outColour;

uniform vec3 lightColor;
uniform samplerCube planetTexture;
uniform float disableLighting;

#define PI 3.141592653589793238462643383279

void main(void) {
	vec3 diffuseColor = texture(planetTexture, passNormal).rgb;

	float lighting = dot(passNormal, toLightVector);
	lighting = max(lighting, 0.0f);
	vec3 diffuse = lightColor * lighting;
	if(disableLighting > 0.5f) diffuse = vec3(1.0f);

	outColour = vec4(diffuseColor * diffuse, 1.0f);

}
