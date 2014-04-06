// Jim Fan (lf2422)
uniform vec4 m_Ambient;
uniform vec4 g_AmbientLightColor;

varying vec4 diffuse;
varying vec4 specular;

void main()
{
	gl_FragColor = m_Ambient * g_AmbientLightColor + diffuse + specular;
}