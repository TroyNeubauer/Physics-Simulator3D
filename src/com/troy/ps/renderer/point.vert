#version 150

in vec4 position;
in vec4 color;

out vec4 passColor;

uniform mat4 projection_matrix;
uniform float pointSize;

void main(void){
	gl_PointSize = pointSize;
	gl_Position = projection_matrix * vec4(position.xyz, 1.0);
	passColor = color;
}
