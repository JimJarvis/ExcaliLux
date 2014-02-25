package chess;

import static utils.PP.p;
import static utils.Util.*;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowRenderer;

public class BoardState extends AbstractAppState
{
	private AssetManager assetManager;
	private InputManager inputManager;
	private Camera cam;
	private Node rootNode;
	private ViewPort viewPort;
	private LightingFactory lighter = LightingFactory.getInstance();
	private DirectionalLight sun; // will go with the camera
	
	// Chessboard with pieces
	private Board board;
	
	/*
	 * Triggers of Listeners
	 */
	private final static Trigger
		// Click on a piece to move it. Right click to deselect
		TRIGGER_SELECT = new MouseButtonTrigger(MouseInput.BUTTON_LEFT),
		TRIGGER_DESELECT = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT), 
		
		// Choose different board material + a number trigger
		TRIGGER_SHIFT = new KeyTrigger(KeyInput.KEY_LSHIFT),
		
		// Choose different piece model set + a number trigger
		TRIGGER_CTRL = new KeyTrigger(KeyInput.KEY_LCONTROL),
		
		// Rotate the Camera view around the board
		TRIGGER_ROTATE = new KeyTrigger(KeyInput.KEY_R),
		TRIGGER_FLIP = new KeyTrigger(KeyInput.KEY_F),

		// Choose different piece material [also serve for combination]
		TRIGGER_1 = new KeyTrigger(KeyInput.KEY_1),
		TRIGGER_2 = new KeyTrigger(KeyInput.KEY_2),
		TRIGGER_3 = new KeyTrigger(KeyInput.KEY_3),
		TRIGGER_4 = new KeyTrigger(KeyInput.KEY_4);
	
     // Trigger maps
	public final static String
    	MAP_SELECT = "MouseSelect",
    	MAP_DESELECT = "MouseDeselect",
    	MAP_ROTATE = "Rotate",
    	MAP_FLIP = "Flip",
    	MAP_SHIFT = "Shift",
    	MAP_CTRL = "Ctrl",
    	MAP_1 = "1", 
    	MAP_2 = "2", 
    	MAP_3 = "3", 
    	MAP_4 = "4";

	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		SimpleApplication sapp = (SimpleApplication) app;
		this.cam = sapp.getCamera();
		this.rootNode = sapp.getRootNode();
		this.assetManager = sapp.getAssetManager();
		this.inputManager = sapp.getInputManager();
		this.viewPort = sapp.getViewPort();
		
		// Setting up the single board instance
		// Renders the default checkerboard and full set of pieces
		Board.setup(sapp);
		board = Board.getInstance();
		
		inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_CAMERA_POS);
    	inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
    	
		// Add input
		inputManager.addMapping(MAP_SELECT, TRIGGER_SELECT);
		inputManager.addMapping(MAP_DESELECT, TRIGGER_DESELECT);
		inputManager.addMapping(MAP_1, TRIGGER_1);
		inputManager.addMapping(MAP_2, TRIGGER_2);
		inputManager.addMapping(MAP_3, TRIGGER_3);
		inputManager.addMapping(MAP_4, TRIGGER_4);
		inputManager.addMapping(MAP_ROTATE, TRIGGER_ROTATE);
		inputManager.addMapping(MAP_FLIP, TRIGGER_FLIP);
		inputManager.addMapping(MAP_SHIFT, TRIGGER_SHIFT);
		inputManager.addMapping(MAP_CTRL, TRIGGER_CTRL);
		
		inputManager.addListener(board.mouseListener(), MAP_SELECT, MAP_DESELECT);
		inputManager.addListener(board.keyRotateListener("analog"), MAP_ROTATE);
		inputManager.addListener(board.keyRotateListener("flip"), MAP_FLIP);
		inputManager.addListener(board.keyComboListener(), MAP_SHIFT, MAP_CTRL);
		inputManager.addListener(board.keyChoiceListener(), MAP_1, MAP_2, MAP_3, MAP_4);
		
		
		// Add white ambience
		lighter.addAmbient();
		
		sun = lighter.addDirectional(cam.getDirection(), ColorRGBA.White);

		// lighter.addPoint(Board.coordSq(6, 3.5, 0), 30, ColorRGBA.White);
		// lighter.addPoint(1, 0, -4, 10, ColorRGBA.White);

		lighter.addSpot(Board.coordSq(6, 6, 5), 
					new Vector3f(-1, -1, -3), 100, 50, 30, ColorRGBA.Cyan);
		lighter.addSpot(Board.coordSq(4, 4, 5), 
					new Vector3f(-1, -1, -3), 100, 30, 20, ColorRGBA.Yellow);
		
		
		/* Shadow effect for the sun!!! */
		// DLSRenderer: slow but better
		DirectionalLightShadowRenderer dlsr = 
					new DirectionalLightShadowRenderer(assetManager, 2048, 1);
		dlsr.setLight(sun);
		dlsr.setShadowIntensity(0.6f);
		viewPort.addProcessor(dlsr);

		// DLSFilter: faster but the edge of quads look worse
/*		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		DirectionalLightShadowFilter dlsf = 
					new DirectionalLightShadowFilter(assetManager, 2048, 1);
		dlsf.setShadowIntensity(0.6f);
		dlsf.setLight(sun);
		fpp.addFilter(dlsf);
		viewPort.addProcessor(fpp);
*/
		// Ambient Occlusion
		/* parameters
		 * sampleRadius - The radius of the area where random samples will be picked. default 5.1f
		 *	intensity - intensity of the resulting AO. default 1.2f
		 *	scale - distance between occluders and occludee that are still considered casting shadows. default 0.2f
		 *	bias - the width of the occlusion cone considered by the occludee. default 0.1f
		 */
//		fpp.addFilter(new SSAOFilter(5f, 4f, .33f, .6f));

		// Turn off global shadowing, only activate on Spatials
		rootNode.setShadowMode(ShadowMode.Off);
	}
	

	@Override
	public void update(float tpf)
	{
		// Directional light goes with the camera
		sun.setDirection(cam.getDirection());
	}
	
	@Override
	public void cleanup() {}
}
