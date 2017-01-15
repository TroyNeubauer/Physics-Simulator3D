#version 150

in vec3 position;

uniform mat4 projectionViewMatrix;

void main(void){
	
	gl_Position = projectionViewMatrix * vec4(position, 1.0);
	
}