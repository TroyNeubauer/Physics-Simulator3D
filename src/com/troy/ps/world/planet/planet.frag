#version 330

in vec3 passColor;
in vec3 passNormal;
in vec3 toLightVector;
in vec2 passTextureCoords;

out vec4 outColour;

uniform vec3 lightColor;

uniform sampler2D planetTexture;

uniform float disableLighting;

void main(void) {
	vec3 diffuseColor = texture(planetTexture, passTextureCoords).rgb;

	float lighting = dot(passNormal, toLightVector);
	lighting = max(lighting, 0.0f);
	vec3 diffuse = lightColor * lighting;
	if(disableLighting > 0.5f) diffuse = vec3(1.0f);

	outColour = vec4(diffuseColor * diffuse, 1.0f);
}
