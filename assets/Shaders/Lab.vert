// Jim Fan (lf2422)

// Global varible taken care by the engine
uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition; // "attribute" is deprec in newer version: "in"
attribute vec2 inTexCoord;

varying vec2 texCoord;  // "varying" is deprec in newer versions: 
									// Can be "out" or "in" depending on the context

void main()
{
// GLSL:  texCoord = gl_MultiTextCoord0.xy;
	texCoord = inTexCoord;
	
// GLSL:  gl_Position = gl_ModelViewProjectMatrix * gl_Vertex;
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}