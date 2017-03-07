#version 150

out vec4 out_colour;

in vec2 blurTextureCoords[11];

uniform sampler2D originalTexture;

void main(void) {
	
	out_colour = vec4(0.0);
	out_colour += texture(originalTexture, blurTextureCoords[0]) * 0.000003;
    out_colour += texture(originalTexture, blurTextureCoords[1]) * 0.000229;
    out_colour += texture(originalTexture, blurTextureCoords[2]) * 0.005977;
    out_colour += texture(originalTexture, blurTextureCoords[3]) * 0.060598;
    out_colour += texture(originalTexture, blurTextureCoords[4]) * 0.24173;
    out_colour += texture(originalTexture, blurTextureCoords[5]) * 0.382925;
    out_colour += texture(originalTexture, blurTextureCoords[6]) * 0.24173;
    out_colour += texture(originalTexture, blurTextureCoords[7]) * 0.060598;
    out_colour += texture(originalTexture, blurTextureCoords[8]) * 0.005977;
    out_colour += texture(originalTexture, blurTextureCoords[9]) * 0.000229;
    out_colour += texture(originalTexture, blurTextureCoords[10]) * 0.000003;

}
