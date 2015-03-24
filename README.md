# ExcaliLux
####Jim Fan, Eona Studio &copy;2014

##Intro and Compilation

This is a 3D chessboard with customized shaders. 
ExcaliLux = *Excalibur Luxurious*, where *Excalibur* is the name of my AI chess engine.

The repo is self-contained. The JMonkeyEngine library is included in "/lib". 
The project compiles and runs succesfully on Ubuntu. 

To facilitate compilation, I include a shell script "run.sh" in my submission. 
Please don't change the directory structure. 

A video highlight has been included. 
Screenshots of each different shader effects are collected in "/screenshots" folder.

## Techniques
All the shaders can be accessed in "/assets/Shaders" folder.
Note that JME requires an extra material definition file for any pair of frag/vert shaders. The material configurations can be found in "/assets/MatDefs". 

- Gouraud Shader
  Customizable diffuse/ambient/specular color.
  Adjustable shininess parameter in code. 

- Blinn-Phong Shading

- Checkerboard procedural texture
  Adjustable density and light/dark colors. 
  Smooth transition between neighboring squares. 

- Wireframe
  "0" key to toggle wireframe on/off.

- Phong-shaded texture mapping
  Standard phong shading. 
  The texture images are carefully mined from Google image.
  Supports multiple sources of lights. 
  Alpha falloff effect (dissolve pieces).

- Lighting
  There are three light sources in the chess scene.
  (1) Ambient lighting (white)
  (2) Directional lighting (light source flies with the camera)
  (3) Two fixed spot lights, one yellow and one cyan, around the back ranks of the black army. 

- Transparent texture
  I manually edited a few texture images in photoshop to add an alpha channel. 
  Then by setting an alpha cutoff threshold with respect to time, it's possible to simulate a dissolving "ghost" effect.

- Shadow effect

- SkyBox
  The seamless background cube of a sunrise. 

##User Manual
A brief description of all the supported commands.

###Navigation
Mouse drag: change camera's perspective
Mouse wheel: zoom 
R: rotate the chessboard with respect to its center (clockwise)
Shift + R: rotate (counter-clockwise)
F: flipping the chessboard to the opponent's side (animation)
Shift + F: flip counter-clockwise
A: camera shift to left
D: to right
W: forward
S: backward
Q: fly up
Z: dive down
Esc: quit

###Material selection
White piece material VS black piece material
Number key
1: Marble VS purple marble
2: Rosewood VS brown wood
3: Ivory VS florence marble
[1 - 3 are Phong-shaded texture mapping]
4: Gouraud Shader
5: Blinn-Phong Shader
6: Checkerboard procedural texture

0: Toggle Wireframe on/off

###Piece model selection
You can change the model set on the fly, by combo Shift + Numberkey

1: Classical piece models (from turbosquid)
2 or 3: "Cute" pieces (from 3Dwarehouse)

###Gameplay
Click on a piece to select it. 
The selected piece will rotate itself. The square right beneath it will be highlighted in green, while any square your mouse hovers over will turn yellow. 
Click a square to place the piece. 
Right click the mouse to deselect. 

Note that you can't capture a friendly piece.

When you capture an enemy piece, that piece will drift up, rotate itself randomly, and finally dissolve into thin air. 
The dissolution effect is achieved by setting the alpha channel threshold. 


##References

 - Book "JMonkeyEngine 3.0 Beginner's Guide" 
 - www.codinguniverse.com
 - JMonkeyEngine official documentation
 - http://lighthouse3d.com
 - http://www.mathematik.uni-marburg.de/ 
 - http://www.blog.nathanhaze.com
 - http://en.wikipedia.org/wiki/Blinn-Phong_shading_model
 - http://en.wikipedia.org/wiki/Gouraud_shading
 - http://www.blender-models.com/
 - http://www.redsorceress.com/skybox.html
 - http://www.swiftless.com/opengl4tuts.html
 - http://www.lighthouse3d.com/tutorials/glsl-tutorial/
 - http://relativity.net.au/gaming/java/Introduction.html
