#version 150

in vec3 position;
in vec3 color;
in vec2 textureCoords;
in vec3 normal;

out vec3 passColor;
out vec3 passNormal;
out vec3 toLightVector;
out vec2 passTextureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 lightPos;

void main(void) {
	vec4 worldPos = modelMatrix * vec4(position, 1.0f);
	gl_Position = projectionMatrix * viewMatrix * worldPos;

	passNormal = normalize((modelMatrix * vec4(normal, 1.0f)).xyz);
	passColor = color;

	toLightVector = normalize(lightPos - worldPos.xyz);
	passTextureCoords = textureCoords;
}
