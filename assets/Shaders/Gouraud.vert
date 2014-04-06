// Jim Fan (lf2422)

// Global variables taken care by the engine
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

// Material parameters defined by Gouraud.j3md
uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

attribute vec3 inPosition;
attribute vec3 inNormal;

// passed to .frag
varying vec4 color;

void main()
{
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);

	vec3 pos = vec3(g_WorldViewMatrix * vec4(inPosition, 1.0));
	vec3 normal = normalize(g_NormalMatrix * inNormal);
	
	vec3 light = normalize(g_LightPosition.xyz - pos);
	vec3 viewer = normalize(-pos);
	vec3 reflection = normalize(reflect(-light, normal));
	
	vec4 diffuse = clamp(m_Diffuse * max(dot(normal, light), 0.0), 0.0, 1.0);
	vec4 specular = clamp(m_Specular * pow(max(dot(reflection, viewer), 0.0), 0.3 * m_Shininess), 0.0, 1.0);
	
	color = m_Ambient + diffuse + specular;
}