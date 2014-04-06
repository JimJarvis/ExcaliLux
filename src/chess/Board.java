package chess;

import utils.MaterialFactory;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.scene.shape.Quad;

import control.FlipBoardState;
import control.PieceSelectedControl;
import control.QuadHoverState;
import static chess.Piece.*;
import static utils.MaterialFactory.*;
import static utils.Util.*;
import static chess.BoardState.*;

/**
 * @author Jim Fan  (c) 2014
 * Main board renderer
 */
public class Board
{
	// Singleton pattern
	private static Board instance = null;

	/* constants */
	// Specifies the width of one chess square
	public static final float SQ_WIDTH = 2f;
	public static final int SQ_N = 64;
	public static final int RANK_N = 8, FILE_N = 8;
	
	// Node scene graph
	private Node rootNode;
	
	private AssetManager assetManager;
	private InputManager inputManager;
	private AppStateManager stateManager;
	private Camera cam; 
	private MaterialFactory factory = MaterialFactory.getInstance();

	// Quad board
	private Geometry boardQuads[] = new Geometry[SQ_N];
	
	// Housekeeper
	private BoardManager boardManager;
	
	// Use different sets of models
	private int modelId = 1;
	private int pieceMatId = 1;
	
	/* Material texture configuration */
	private Material lightQuadMat; // for board color
	private Material darkQuadMat;
	private Material lightPieceMat; // for pieces
	private Material darkPieceMat;
	
	/**
	 * Should only be called once at main()
	 */
	public static void setup(SimpleApplication app)
	{
		if (instance == null)
			instance = new Board(app);
	}
	
	/**
	 * Singleton pattern
	 */
	public static Board getInstance() {	return instance;	}
	
	
	/**
	 * Private ctor, renders the board and the pieces
	 */
	private Board(SimpleApplication app)
	{
		this.rootNode = app.getRootNode();
		this.assetManager = app.getAssetManager();
		this.inputManager = app.getInputManager();
		this.stateManager = app.getStateManager();
		this.cam = app.getCamera();
		
    	boardManager = new BoardManager(rootNode);

		// Lay out the board made of quads
		setQuadMaterial(factory.loadPlain(ColorRGBA.Brown), 
                    		factory.loadPlain(ColorRGBA.LightGray));
		renderQuadBoard();

		// Render the scene, default material/model ID = 1
		setPieceMaterial();
		renderPieces();
	}
	
	
	public BoardManager getBoardManager() {	return this.boardManager;	}
	
	/**
	 * Set the material for the pieces
	 */
	public void setPieceMaterial()
	{
		switch(this.pieceMatId)
		{
		case 1 :
			lightPieceMat = factory.loadGold();
			darkPieceMat = factory.loadPurpleMarble();
			break;
		case 2 :
			lightPieceMat = factory.loadRosewood();
			darkPieceMat = factory.loadBrownwood();
			break;
		case 3 :
			lightPieceMat = factory.loadIvory();
			darkPieceMat = factory.loadFlorenceMarble();
			break;
		case 4 :
			lightPieceMat = factory.loadLab();
			darkPieceMat = factory.loadLab();
			break;
		}
	}
	
	public int getPieceMatId() {	return this.pieceMatId;	}
	
	public int getModelId()	{	return this.modelId;	}
	
	/**
	 * Set the material for the board
	 */
	private void setQuadMaterial(Material lightMat, Material darkMat)
	{
		this.lightQuadMat = lightMat;
		this.darkQuadMat = darkMat;
	}
	
	
	/******************** Rendering ********************/
	/**
	 * Render all the pieces. Piece names start with '*'
	 * Default model set: #1
	 */
	private void renderPieces()
	{
		int sq = 0;
		for (int y = 0; y < RANK_N; y++)
    		for (int x = 0; x < FILE_N; x++)
    		{
    			// First clear all old models
    			Piece piece = null;
    			if ((piece = boardManager.getModel(sq)) != null)
    				rootNode.detachChild(piece);
    					
				int p = boardManager.getPiece(sq);
				
				if (p != NON)
				{
					piece = new Piece(
							(Geometry) assetManager.loadModel("Models/" + Piece.name(p) + this.modelId + ".j3o"),
							p,  // specify the piece type
							boardManager.isWhite(sq) ? this.lightPieceMat : this.darkPieceMat,
							boardManager.getSide(sq), x, y);
					rootNode.attachChild(piece);
				}
				boardManager.setModel(sq++, piece);
    		}
	}
	
	/**
	 * Render the chess board
	 */
	private void renderQuadBoard()
	{
		int sq = 0;
		for (int y = 0; y < RANK_N; y++)
    		for (int x = 0; x < FILE_N; x++)
    		{
    			Geometry quad = new Geometry("@" + sq, new Quad(SQ_WIDTH, SQ_WIDTH));
    			quad.setMaterial((x + y) % 2 == 0 ? 
                    					this.darkQuadMat.clone() : this.lightQuadMat.clone());

    			// Rotate to X-Z plane
    			quad.rotate(toRad(-90), 0, 0);
    			quad.move(coordSqCorner(x, y));
    			// Floor only receives shadow
    			quad.setShadowMode(ShadowMode.Receive);
    			
    			this.boardQuads[sq ++] = quad;
    			rootNode.attachChild(quad);
    		}
	}
	
	public Geometry getQuad(int sq) {	return this.boardQuads[sq];	}
	
	/**
	 * Remove the rendering of a model, detach from RootNode.
	 */
	public void detach(Piece model)
	{
		if (model != null)
    		this.rootNode.detachChild(model);
	}


	/******************** Input Processing ********************/
	
	// State managing
	private QuadHoverState quadHighlightState = new QuadHoverState();
	
	// indicates which one is selected. null if none
	private Piece selectedPiece = null;
	
	public Piece getSelectedPiece() {	return selectedPiece;	}
	
	public Geometry getQuadUnderSelectedPiece()
	{
		return selectedPiece != null ? 
				boardQuads[selectedPiece.getSq()] : null;
	}
		
	/**
	 * Mouse listener: selects pieces
	 */
	public ActionListener mouseListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (isPressed)
				if (name.equals(MAP_SELECT))
	    		{
	    			CollisionResults results = new CollisionResults();
	    			Vector2f click2d = inputManager.getCursorPosition();
	    			Vector3f click3d = cam.getWorldCoordinates(click2d, 0f); // depth 0
	    			// 1 WU deep into the screen
	    			Vector3f dir = cam.getWorldCoordinates(click2d, 0.5f).subtractLocal(click3d);
	    			Ray ray  = new Ray(click3d, dir);
	    			
	    			rootNode.collideWith(ray, results);
	    			for (CollisionResult res : results)
					{
	    				Geometry hit = res.getGeometry();
	    				String hitName = hit.getName();
	    				// If it's the board
	    				if (hitName.charAt(0) == '@')
	    				{
	    					// If a piece is selected, move the piece to the designated location 
	    					// and detach the SelectionControl
	    					if (selectedPiece != null)
	    					{
	    						selectedPiece.addControl(
	    									boardManager.moveControl(hitName));
	    						stateManager.detach(quadHighlightState);
	    						selectedPiece = null;
	    					}
    	    				break;
	    				}
	    				else if (hitName.charAt(0) == '*') // hits a piece
	    				{
	    					Piece hitPiece = (Piece) hit;
	    					if (hitPiece != selectedPiece)
	    					{
    	    					selectedPiece = hitPiece;
        						selectedPiece.addControl(new PieceSelectedControl());
        						// If a piece is selected, we allow quad highlighting
        						stateManager.attach(quadHighlightState);
	    					}
    	    				break;
	    				}
					}
	    		}
				// Right click
				else if (name.equals(MAP_DESELECT))
				{
					selectedPiece = null;
					stateManager.detach(quadHighlightState);
				}
					
			}
		};
	}
	
	
	/**
	 * Keyboard listener: chooses a new set of models or piece/board material
	 */
	public ActionListener keyChoiceListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (isPressed)
				{
					// Get the ID of a set of models/piece/board material
					int id = Integer.parseInt(name);
					
					if (isShiftPressed)
					{
						Board.this.modelId = id > 2 ? 2 : id;
					}
					else
					{
    					Board.this.pieceMatId = id;
    					setPieceMaterial();
					}
					renderPieces();
				}
			}
		};
	}
	
	/**
	 * Keyboard listener: rotate view
	 * Default rotation direction: clockwise
	 * Press shift to rotate counterclockwise
	 * @param id "analog" for continous rotate (KEY_R), "action" for board flip view (KEY_F)
	 */
	public InputListener keyRotateListener(String id)
	{
		if (id.equals("analog"))
    		return new AnalogListener()
    		{
    			@Override
    			public void onAnalog(String name, float value, float tpf)
    			{
    				if (name.equals(MAP_ROTATE))
    					rotateBoardCam(value * (isShiftPressed ? -1 : 1));
    			}
    		};
    	else
    	// press F-key to flip the whole scene
    	// Shift-F to flip counterclockwise
    		return new ActionListener()
			{
				@Override
				public void onAction(String name, boolean isPressed, float tpf)
				{
					if (isPressed)
					if (name.equals(MAP_FLIP))
						stateManager.attach(new FlipBoardState());
				}
			};
	}
	
	/**
	 * Rotation helper: rotate the camera while fixing the view
	 * on the center of the board
	 */
	public void rotateBoardCam(float value)
	{
		final Vector3f center2D = coordSqCorner(4, 4);
		Vector3f center3D = coordSqCorner(4, 4);
		Vector3f ray = cam.getLocation().subtract(center3D);
		
		cam.setLocation(new Quaternion()
					.fromAngleAxis(value, Vector3f.UNIT_Y)
					.mult(ray).add(center3D));
		
		cam.lookAt(center2D, Vector3f.UNIT_Y);
	}
	
	/**
	 * Listens to combo keys like Shift+ and Ctrl+
	 */
	// The following booleans indicate whether these keys are active
	private volatile boolean isShiftPressed = false;
	public boolean isShiftyPressed() {	return this.isShiftPressed;	}
	private boolean isCtrlPressed = false;
	public boolean isCtrlPressed() {	return this.isCtrlPressed;		}
	
	public ActionListener keyComboListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (name.equals("Shift"))
					Board.this.isShiftPressed = isPressed;
				else if (name.equals("Ctrl"))
					Board.this.isCtrlPressed = isPressed;
			}
		};
	}
	
	
	
	/* Helper Methods */
	/**
	 * Get coordinate of the center of a square
	 * elevation floats above the scene, default to 0
	 * @param x file
	 * @param y rank
	 */
	public static Vector3f coordSq(double x, double y, float elevation)
	{
		return new Vector3f((float) x * SQ_WIDTH + SQ_WIDTH/2, 
									elevation, 
									- (float) (y * SQ_WIDTH + SQ_WIDTH/2));
	}
	
	public static Vector3f coordSq(double x, double y)
	{
		return coordSq(x, y, 0);
	}
	
	/**
	 * Get coordinate of the corner of a square
	 * @param x file
	 * @param y rank
	 */
	public static Vector3f coordSqCorner(int x, int y)
	{
		return new Vector3f(x * SQ_WIDTH, 0f, - y * SQ_WIDTH);
	}
	
	public static Vector3f coordSqCorner(int x, int y, float elevation)
	{
		return new Vector3f(x * SQ_WIDTH, elevation, - y * SQ_WIDTH);
	}
}
