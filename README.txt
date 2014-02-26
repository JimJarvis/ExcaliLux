W4160 Computer Graphics
Linxi Fan
lf2422

ExcaliLux
The Luxurious 3D Chess Interface

================ Part 1  Intro and Compilation ================

This is a 3D chessboard written with JMonkeyEngine. 

My submission is self-contained, i.e. doesn't require any extra library installation on CLIC. 
The project compiles and runs succesfully on CLIC, though the speed is extremely slow, because  there're around 400,000 triangular meshes in the scene. 
So please run it on your local machine. ExcaliLux runs smoothly on my local Ubuntu laptop. 

To facilitate compilation from source, I include a shell script "run.sh" in my submission. 
Please don't change the directory structure. 

To make it even more convenient, I have packed all the texture/mesh data and required libraries into a far jar that's directy runnable by double clicking. In case you're using windows, I've also made a self-contained .exe binary. 
Both can be downloaded from my temporary website: 

www.columbia.edu/~lf2422

I include a folder of screenshots in my submission. The video, however, is too large to upload to courseworks. So I also put it on my website. 

I spent almost two weeks watching/reading tutorials, trying out random fun stuff and tweaking parameters/models all over the place. Graphics gradually becomes addictive as I dive deeper into it.
The exploration was a pain, but definitely worth the effort. 

Btw, though I'm a chess lover, I'm probably the world's worst chess player. ;)

I decide to provide long term support for this project, just for fun. Stay tuned! 


================ Part 2  User Manual ================
A brief description of all the supported commands.

=== Navigation ===
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

=== Material selection ===
white piece material VS black piece material
Number key
1: Marble VS purple marble
2: Rosewood VS brown wood
3: Ivory VS florence marble
Luxurious!

=== Piece model selection ===
You can change the model set on the fly, by combo Shift + Numberkey

1: Classical piece models (from turbosquid)
2 or 3: "Cute" pieces (from 3Dwarehouse)


=== Gameplay ===
Click on a piece to select it. 
The selected piece will rotate itself. The square right beneath it will be highlighted in green, while any square your mouse hovers over will turn yellow. 
Click a square to place the piece. 
Right click the mouse to deselect. 

Note that you can't capture a friendly piece.

When you capture an enemy piece, that piece will drift up, rotate itself randomly, and finally dissolve into thin air. 
The dissolution effect is achieved by setting the alpha channel threshold. 


================ Part 3  Techniques ================
Here's a brief list of the techniques I used.
- Perspectives

- Texture mapping
  The textures are all carefully mined from Google image.

- 3D Models
  I spent a lot of time on this part. Because most of the online free models are either very poor in quality or stored in a specialized format. I actually installed Autodesk 3ds Max and AutoCAD last week. Learned some basics of these state-of-the-art 3D editors.
  I manually edited a few properties of the mesh models. 

- Phong lighting model
  I have implemented my own GLSL shaders in the folder assets/Shader/*
  Honestly, I don't really understand every line, because part of it is based on and learned from www.codinguniverse.com (LWJGL tutorial) and JMonkeyEngine tutorial, cited in the reference part later.
  I provide 2 sets of fragment/vertex shaders, one simple (plain lighting) and one phong lighting.
  The phong shader uses the classical exponential function to compute the specular intensity. 

- Lighting
  There are three light sources in the chess scene.
  (1) Ambient lighting (white)
  (2) Directional lighting (light source flies with the camera)
  (3) Two fixed spot lights, one yellow and one cyan, around the back ranks of the black army. 

- Transparent texture
  I manually edited a few texture images in photoshop to add an alpha channel. 
  Then by setting an alpha cutoff threshold with respect to time, it's possible to simulate a dissolving "ghost" effect.

- Ray casting
  This is how the mouse is able to select a chess piece or a board square.

- Shadow effect
  Mainly directional light's shadow effect.
  I also thought about doing ambient occlusion, but I run short of time.

- Control System
  A pretty complex control system is used to organize the input and logical operations. The system employs an object-oriented model to organize different phases and hierarchies of game actions.

- SkyBox
  The seamless background cube of a sunrise. 

- To-do in the future
  I've got so much ideas but so little time to lay out all of them. For example, the chess interface doesn't yet enforce game rules. The only restriction is that you can only move to a vacant square or capture an enemy piece.


Thanks for reading! Happy chess. ;)

================ Part 4  References ================
Book "JMonkeyEngine 3.0 Beginner's Guide"
www.codinguniverse.com
youtube LWJGL tutorial page
LWJGL official documentation
JMonkeyEngine official documentation
lighthouse3d.com
wikipedia.org
image.google.com
http://www.blender-models.com/
http://www.redsorceress.com/skybox.html
http://www.swiftless.com/opengl4tuts.html
http://www.lighthouse3d.com/tutorials/glsl-tutorial/
http://relativity.net.au/gaming/java/Introduction.html
