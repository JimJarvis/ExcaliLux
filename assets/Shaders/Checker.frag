// Jim Fan (lf2422)

// We don't calculate the color here because GLSL can interpolate 
// the checkerboard colors. The interpolation makes the square boundaries smoother. 
varying vec4 color;

void main()
{
	gl_FragColor = color;
}