// Jim Fan (lf2422)

// Global variables taken care by the engine
uniform mat4 g_WorldViewProjectionMatrix;

// Material parameters defined by Checker.j3md
uniform vec4 m_Color1, m_Color2;
uniform int m_Density;

attribute vec3 inPosition;
attribute vec2 inTexCoord;

// passed to .frag
varying	vec4 color;

void main()
{
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
	
	// determine the texture row and columns
	int row, col;
	float s = inTexCoord.s;
	float t = inTexCoord.t;
	
	if (s < 0.0)		col = 0;
//	else if (s >= 4.0)	col = 3;
	else col = int(floor(s * m_Density));
	
	if (t < 0.0)		row = 0;
//	else if (t >= 4.0) 	row = 3;
	else row = int(floor(t * m_Density));
	
	if (mod(row + col, 2) == 0)
		color = m_Color1;
	else
		color = m_Color2;
}