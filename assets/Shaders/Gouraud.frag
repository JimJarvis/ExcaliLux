// Jim Fan (lf2422)
varying vec4 diffuse;
varying vec4 specular;

uniform vec4 m_Ambient;
uniform vec4 g_AmbientLightColor;

void main()
{
	gl_FragColor = m_Ambient * g_AmbientLightColor + diffuse + specular;
}