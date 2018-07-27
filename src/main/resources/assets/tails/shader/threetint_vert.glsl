#version 130

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;

out vec2 TexCoord;

void main() {
  TexCoord = vec2(uv.x, uv.y);

  gl_Position = gl_ModelViewProjectionMatrix * pos;
}