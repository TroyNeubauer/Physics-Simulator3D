#version 150

in vec3 position;
in vec3 color;

out vec3 passColor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0f);
	passColor = color;
}
