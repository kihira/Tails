#version 130

in vec3 pos;
in vec3 normal;
in vec2 uv;

out vec2 TexCoord;

void main() {
  TexCoord = vec2(uv.x, uv.y);

  gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 0);
}