#version 330

in vec3 passColor;

out vec4 outColour;

void main(void) {
	outColour = vec4(passColor, 1.0f);
}
