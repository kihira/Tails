#version 120

varying vec3 Normal;
varying vec2 TexCoord;
varying vec4 Color;

uniform sampler2D tex;
uniform vec3[3] tints; // Normalised set of tints

void main() {

  vec4 texCol = texture2D(tex, TexCoord);
/*
  Experimental version using alpha as the tone instead of red channel
  float tone = texCol.a;
  float red = tints[0].r * texCol.r + tints[1].r * texCol.g + tints[2].r * texCol.b;
  float green = tints[0].g * texCol.r + tints[1].g * texCol.g + tints[2].g * texCol.b;
  float blue = tints[0].b * texCol.r + tints[1].b * texCol.g + tints[2].b * texCol.b;
  */

  float tone = texCol.r;
  float w1 = 1.0 - (texCol.g + texCol.b);
  float w2 = texCol.g * (1.0 - texCol.b);
  float w3 = texCol.b;
  float red = tints[0].r * w1 + tints[1].r * w2 + tints[2].r * w3;
  float green = tints[0].g * w1 + tints[1].g * w2 + tints[2].g * w3;
  float blue = tints[0].b * w1 + tints[1].b * w2 + tints[2].b * w3;

  //gl_FragColor = vec4(tone * red, tone * green, tone * blue, texCol.a);

  gl_FragColor = vec4(tone * red, tone * green, tone * blue, texCol.a) * Color;
}