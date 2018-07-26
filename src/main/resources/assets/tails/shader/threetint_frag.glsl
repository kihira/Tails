#version 120

in vec2 TexCoord;

uniform sampler2D tex;

void main() {
  gl_FragColor = texture(tex, TexCoord);
}