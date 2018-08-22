#version 120

attribute vec3 pos;
attribute vec3 normal;
attribute vec2 uv;

varying vec3 Normal;
varying vec2 TexCoord;
varying vec4 Color;

vec4 ambient;
vec4 diffuse;

void directional_light(in int light, in vec3 normal) {
  ambient += gl_LightSource[light].ambient;
  diffuse += gl_LightSource[light].diffuse * max(0, dot(normal, normalize(vec3(gl_LightSource[light].position))));
}

void main() {
  Normal = vec3(gl_ModelViewMatrix * vec4(normal, 1));
  TexCoord = vec2(uv.x, uv.y);

  ambient = vec4(0);
  diffuse = vec4(0);
  directional_light(0, Normal);
  directional_light(1, Normal);
  Color = gl_LightModel.ambient + ambient * gl_FrontMaterial.ambient + diffuse * gl_FrontMaterial.diffuse + gl_FrontMaterial.specular;

  gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1);
}