#version 150

out vec4 out_colour;

uniform vec3 color;
uniform float alpha;

void main(void) {

	out_colour = vec4(color, alpha);
	
}