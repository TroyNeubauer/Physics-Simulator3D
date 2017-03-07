#version 150

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colorTexture;
uniform sampler2D highlightTexture;

void main(void){
	vec4 sceneColor = texture(colorTexture, textureCoords);
	vec4 highlightColor = texture(highlightTexture, textureCoords);

	out_Colour = sceneColor + highlightColor * 10.0;
}
