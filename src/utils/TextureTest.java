/**
 * @author Jim Fan  (c) 2014
 * Imports a 3D model. Also shows how to save the entire scene graph to disk
 */
package utils;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;
import com.jme3.system.AppSettings;

import static utils.PP.*;

/**
 * test
 * @author normenhansen
 */
public class TextureTest extends SimpleApplication
{

	private static TextureTest app;
	
	private final static Trigger TRIGGER_COLOR= 
			new KeyTrigger(KeyInput.KEY_SPACE);
	// multiple mapping to one action
	private final static Trigger TRIGGER_COLOR2= 
			new KeyTrigger(KeyInput.KEY_V);
	
	private final static Trigger TRIGGER_ROT = 
			new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
	
	private final static String MAPPING_COLOR = "Toggle Color";
	private final static String MAPPING_ROT = "Rotate";
	
	private Geometry geom;
	private Material mat;
	private Spatial chessSpatial;
	
	   /**
     * Common/MatDefs/Light/Lighting.j3md
     * UseMaterialColors - boolean
     * Diffuse, Ambient, Specular - ColorRGBA
     * Shininess - Float
     * DiffuseMap - Texture
     * NormalMap, ParallaxMap, SpecularMap, AlphaMap, GlowMap - Texture
     * 
     * Shininess: 1 - 128f
     * Glass, water, silver: > 16f
     * Metal, plastic, polished: < 16f && > 0
     */
    private void lightingMat(String fileName)
    {
    	mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    	mat.setBoolean("UseMaterialColors", true);
    	mat.setColor("Diffuse", ColorRGBA.LightGray);
    	// mat.setColor("Diffuse", new ColorRGBA(95f/255, 0f/255, 95f/255, 0.5f));
    	mat.setColor("Ambient", ColorRGBA.Gray);
    	mat.setColor("Specular", ColorRGBA.White);
    	mat.setFloat("Shininess", 100f);  // [1, 128]
    	mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/" + fileName));
    }
	
	
    public static void main(String[] args)
    {
    	AppSettings settings = new AppSettings(true);
    	settings.setTitle("HelloJME");
    	settings.setBitsPerPixel(24);
    	settings.setFrameRate(60);
    	settings.setVSync(true);
    	settings.setResolution(1000, 840);
    	settings.setSamples(16);
    	settings.setUseInput(true);
    	
        app = new TextureTest();
        app.setSettings(settings);
        app.setShowSettings(false);
    	// disable debugging printout to screen
//        app.setDisplayFps(false);
//        app.setDisplayStatView(false);
        app.start();
        // to pause the game when it loses focus:
        // app.setPauseOnLostFocus(true);
    }
    
   
    @Override
    public void simpleInitApp()
    {
    	inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR, TRIGGER_COLOR2);
    	inputManager.addMapping(MAPPING_ROT, TRIGGER_ROT);
    	
    	inputManager.addListener(actionListener, MAPPING_COLOR);
    	// inputManager.addListener(analogCrosshairListener, MAPPING_ROT);
    	inputManager.addListener(analogFreemouseListener, MAPPING_ROT);
    	
    	// Make the mouse visible
    	flyCam.setDragToRotate(true);
    	inputManager.setCursorVisible(true);
    	
    	cam.setLocation(new Vector3f(0, 2, 10));
    	
    	boolean transparent = false;

    	Geometry cube = genBox("Cube", new Vector3f(0, 0, -5), ColorRGBA.Yellow);
		if (transparent) rootNode.attachChild(cube);

    	chessSpatial = assetManager.loadModel("Models/King1.j3o");
    	
    	lightingMat("Marble/black_marble.jpg");
    	// unshadedMat();
    	if (transparent) renderTransparent();
    	
    	chessSpatial.setMaterial(mat);
    	
    	DirectionalLight sun = new DirectionalLight();
    	sun.setDirection(new Vector3f(1, 0, -2));
    	sun.setColor(ColorRGBA.White);
    	
    	AmbientLight ambient = new AmbientLight();
    	ambient.setColor(ColorRGBA.White);
    	
    	PointLight lamp = new PointLight();
    	lamp.setPosition(new Vector3f(1, 0, 1));
    	lamp.setColor(ColorRGBA.Yellow);
    	
    	    /** A cone-shaped spotlight with location, direction, range */
        SpotLight spot = new SpotLight(); 
        spot = new SpotLight(); 
        spot.setSpotRange(100); 
        spot.setSpotOuterAngle(toRadian(20)); 
        spot.setSpotInnerAngle(toRadian(15)); 
        spot.setDirection(cam.getDirection()); 
        spot.setPosition(cam.getLocation()); 
        spot.setColor(ColorRGBA.Yellow);
        
    	rootNode.addLight(sun);
    	rootNode.addLight(ambient);
    	//rootNode.addLight(lamp);
        //rootNode.addLight(spot); 

    	rootNode.attachChild(chessSpatial);
    }
    
    /**
     * Common/MatDefs/Misc/Unshaded.j3md properties: (parameter string name - data type)
     * Color - ColorRBGA
     * ColorMap, LightMap - Texture
     */
    private void unshadedMat()
    {
    	mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	mat.setColor("Color", ColorRGBA.Cyan);
    	mat.setTexture("ColorMap", 
    			assetManager.loadTexture("Textures/Ivory/ivory_2.jpg"));
    }
    
    /**
     * After doing the lighting
     * Opaque geometries must be drawn first, and they must occlude each other depending on their depth. 
     * Transparent geometries must be rendered on top of opaque geometries, and they must blend with
	what is behind them. Finally, the GUI must be rendered on top of everything.

	jMonkeyEngine uses a RenderQueue that categorizes all geometries into "buckets",
	sorts each bucket by depth, and renders everything in the right order.
     */
    private void renderTransparent()
    {
    	mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	mat.getAdditionalRenderState().setAlphaTest(true);
    	// above alpha will be rendered
    	mat.getAdditionalRenderState().setAlphaFallOff(0f);
    	chessSpatial.setQueueBucket(Bucket.Transparent); // default: Bucket.Opaque
    }
   
    /**
     * ActionListener: for discrete either/or input events
     */
    private ActionListener actionListener = new ActionListener() {
    	public void onAction(String name, boolean isPressed, float tpf)
    	{
    		// TEST IF A KEY IS RELEASED
    		if (name.equals(MAPPING_COLOR) && !isPressed)
    			geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
    	}
	};

	private float alpha = 0f;
	/**
	 * Free mouse selector
	 */
	private AnalogListener analogFreemouseListener = new AnalogListener() {
    	public void onAnalog(String name, float intensity, float tpf)
    	{
    		if (name.equals(MAPPING_ROT))
    		{
    			CollisionResults results = new CollisionResults();
    			Vector2f click2d = inputManager.getCursorPosition();
    			Vector3f click3d = cam.getWorldCoordinates(click2d, 0f); // depth 0
    			// 1 WU deep into the screen
    			Vector3f dir = cam.getWorldCoordinates(click2d, 1f).subtractLocal(click3d);
    			Ray ray  = new Ray(click3d, dir);
    			/* Colliding with everthing is too expensive
    			 * you can add mutually exclusive selectable objects to another group of Node
    			 */
    			rootNode.collideWith(ray, results);
    			for (int i = 0; i < results.size(); i++)
				{
    				CollisionResult res = results.getCollision(i);
    				Geometry target = res.getGeometry();
    				target.rotate(0, 0, intensity);
				}
    			
    			// Can also use AlphaMap to decide how transparent each part of Geometry should be
    			// Black areas in the AlphaMap outline the areas that will disappear from the geometry
    			// white areas remain solid.
            	mat.getAdditionalRenderState().setAlphaFallOff(alpha += 0.005f);
    		}
    	}
	};
	
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private float toRadian(double deg) { return FastMath.DEG_TO_RAD * (float) deg; }
    
    public Geometry genBox(String name, Vector3f loc, ColorRGBA color)
    {
        Geometry geom = new Geometry(name, new Box(1, 1, 1));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    	mat.setBoolean("UseMaterialColors", true);
    	mat.setColor("Diffuse", ColorRGBA.DarkGray);
    	mat.setColor("Ambient", ColorRGBA.White);
    	mat.setColor("Specular", ColorRGBA.White);
    	mat.setFloat("Shininess", 30f);  // [1, 128]
    	mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Wood/oak.jpg"));

        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.rotate(1, 1, 1);
        
        return geom;
    }
}
