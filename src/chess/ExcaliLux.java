package chess;

import utils.LightingFactory;
import utils.MaterialFactory;
import utils.Util;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * @author Jim Fan  (c) 2014
 * Excalibur Luxury Edition
 * 3D Chess board.
 */
public class ExcaliLux extends SimpleApplication
{
	private static ExcaliLux app;

	/**
	 * Main entry point of ExcaliLux
	 */
    public static void main(String[] args)
    {
    	// Global game settings
        AppSettings settings = new AppSettings(true);
    	settings.setTitle("ExcaliLux");
    	settings.setFrameRate(60);
//    	settings.setVSync(true);
    	settings.setResolution(1000, 840);
    	// Setting the sampling rate might crash on some systems.
    	settings.setSamples(16);
    	
        ExcaliLux app = new ExcaliLux();
        app.setSettings(settings);
        app.setShowSettings(false);
    	// disable debugging printout to screen
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        // to pause the game when it loses focus:
        app.setPauseOnLostFocus(true);

    	// And here we go!!!
        app.start();
    }
    

    @Override
    public void simpleInitApp()
    {
    	// Setting up various factories
    	MaterialFactory.setup(assetManager);
    	LightingFactory.setup(rootNode, cam);

    	// Make the mouse visible
    	flyCam.setDragToRotate(true);
    	flyCam.setMoveSpeed(10);
    	inputManager.setCursorVisible(true);

    	// set move speed
    	BoardState boardState = new BoardState();
    	stateManager.attach(boardState);
    	
    	// Set initial camera orientation
    	cam.setLocation(Board.coordSq(8.5, -5.5, 10.0f));
    	cam.lookAt(Board.coordSq(3, 4), Vector3f.UNIT_Y);
    	
    	// Load SkyBox background texture
    	rootNode.attachChild(MaterialFactory
    			.getInstance().loadSkyBox("Sky/Sandsky", "bmp")
    			.rotate(Util.toRad(180), 0, 0));
    }

    public void simpleUpdate(float tpf)
    {
    }

    public void simpleRender(RenderManager rm)
    {
    }
}
