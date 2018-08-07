#version 120

attribute vec3 pos;
attribute vec3 normal;
attribute vec2 uv;

varying vec2 TexCoord;

void main() {
  TexCoord = vec2(uv.x, uv.y);

  gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1);
}