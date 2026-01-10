#version 330

uniform sampler2D Sampler0;

// Using the built in RenderPipelines, it only supports buffer objects
layout(std140) uniform PartData
{
    vec3 Tint0;
    vec3 Tint1;
    vec3 Tint2;
};

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord2;

out vec4 fragColor;

void main()
{
  vec4 texCol = texture(Sampler0, texCoord0);

  float tone = texCol.r;
  float w1 = 1.0 - (texCol.g + texCol.b);
  float w2 = texCol.g * (1.0 - texCol.b);
  float w3 = texCol.b;
  float red = Tint0.r * w1 + Tint1.r * w2 + Tint2.r * w3;
  float green = Tint0.g * w1 + Tint1.g * w2 + Tint2.g * w3;
  float blue = Tint0.b * w1 + Tint1.b * w2 + Tint2.b * w3;

  vec4 color = vec4(tone * red, tone * green, tone * blue, texCol.a);
  if (color.a < vertexColor.a)
  {
    discard;
  }
  fragColor = color;
}
