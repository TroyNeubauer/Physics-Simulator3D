#version 150

out vec4 out_colour;

uniform vec3 color;

void main(void) {

	out_colour = vec4(color, 1.0);
	
}
