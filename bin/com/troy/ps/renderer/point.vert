#version 150

in vec3 position;
in vec4 offset;
in vec4 color;
in float radius;

out vec4 passColor;

uniform mat4 projection_matrix;
uniform mat4 view_matrix;

void main(void){
	gl_Position = projection_matrix * view_matrix * vec4((position * radius) + offset.xyz, 1.0);
	passColor = color;
}
