#version 150

in vec2 in_position;

uniform vec4 transform;

void main(void){

	vec2 screenPosition = in_position * transform.zw + transform.xy;
	screenPosition.x *= +2.0 - 1.0;
	screenPosition.y *= -2.0 + 1.0;
	gl_Position = vec4(screenPosition, 0.0, 1.0);
	
}
