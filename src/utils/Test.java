package utils;

import java.awt.*;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

import static utils.PP.*;

public class Test extends SimpleApplication
{

	private static Test app;
	
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
	private static Box mesh = new Box(1, 1, 1);
	
	
    public static void main(String[] args)
    {
    	AppSettings settings = new AppSettings(true);
    	settings.setTitle("HelloJME");
    	settings.setBitsPerPixel(24);
    	settings.setFrameRate(60);
    	settings.setVSync(true);
    	settings.setResolution(1000, 840);
    	settings.setSettingsDialogImage("Interface/saber.png");
    	settings.setSamples(16);
    	settings.setUseInput(true);
    	
        app = new Test();
        app.setSettings(settings);
        app.setShowSettings(false);
    	// disable debugging printout to screen
//        app.setDisplayFps(false);
//        app.setDisplayStatView(false);
        app.start();
        // to pause the game when it loses focus:
        // app.setPauseOnLostFocus(true);
    }
    
    public void toggleToFullscreen()
    {
    	  GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	  DisplayMode[] modes = device.getDisplayModes();
    	  int i=0; // note: there are usually several, let's pick the first
    	  settings.setResolution(modes[i].getWidth(),modes[i].getHeight());
    	  settings.setFrequency(modes[i].getRefreshRate());
    	  settings.setBitsPerPixel(modes[i].getBitDepth());
    	  settings.setFullscreen(device.isFullScreenSupported());
    	  app.setSettings(settings);
    	  app.restart(); // restart the context to apply changes
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
    	
/*    	// Delete builtin mapping
    	inputManager.deleteMapping(INPUT_MAPPING_CAMERA_POS);
    	inputManager.clearMappings();*/
    	
    	Quad qd = new Quad(2, 2);
    	Geometry quad = new Geometry("Quad", qd);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        quad.setMaterial(mat);
        quad.rotate(toRadian(-90), 0, 0);
        
        rootNode.attachChild(quad);
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

    /**
     * AnalogListener: for continuous events with an intensity
     * crosshair (2D center at the screen) selector
     */
	private AnalogListener analogCrosshairListener = new AnalogListener() {
    	public void onAnalog(String name, float intensity, float tpf)
    	{
    		if (name.equals(MAPPING_ROT))
    		{
    			CollisionResults results = new CollisionResults();
    			Ray ray = new Ray(cam.getLocation(), cam.getDirection());
    			rootNode.collideWith(ray, results);
    			int i = 0;
    			for (CollisionResult res : results)
    			{
    				p("Selection: #", i++, ":", res.getGeometry(), "at", res.getContactPoint()
    						, ",", res.getDistance(), "WU away");
    			// Rotate the cubes
    				Geometry target = res.getGeometry();
    				if (target.getName().equals("Blue cube"))
    					target.rotate(0, -intensity, 0);
    				if (target.getName().equals("Yellow cube"))
    					target.rotate(intensity, 0, 0);
				}
    			
    		}
    	}
	};
	
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
    				p("Selection: #", i+1, ":", res.getGeometry(), "at", res.getContactPoint()
    						, ",", res.getDistance(), "WU away");
    			// Rotate the cubes
    				Geometry target = res.getGeometry();
    				if (target.getName().equals("Blue cube"))
    					target.rotate(0, -intensity, 0);
    				if (target.getName().equals("Yellow cube"))
    					target.rotate(intensity, 0, 0);
				}
    			
    		}
    	}
	};
	
	/**
	 * Attach a center mark to the 2D UI
	 */
	private void attachCrosshairCenter()
	{
		Geometry c = genBox("Center mark", Vector3f.ZERO, ColorRGBA.White);
		c.scale(4);
		c.setLocalTranslation(settings.getWidth()/2, settings.getHeight()/2, 0);
		guiNode.attachChild(c);
	}

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private float toRadian(float deg) { return FastMath.DEG_TO_RAD * deg; }
    
    public Geometry genBox(String name, Vector3f loc, ColorRGBA color)
    {
        Geometry geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
}
