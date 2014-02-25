package chess;

import utils.Util;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

import control.PieceSelectedControl;
import control.QuadHoverState;
import static utils.PP.p;
import static chess.Piece.*;
import static utils.Util.*;
import static chess.MaterialFactory.*;

/**
 * @author Jim Fan  (c) 2014
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
	
	/* Material texture configuration */
	private Material lightQuadMat; // for board color
	private Material darkQuadMat;
	private Material lightPieceMat; // for pieces
	private Material darkPieceMat;
	private int modelIndex = 1; // which set of 3D models to be used
	
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

		// Re-render the scene
		setPieceMaterial(factory.loadMarble(ColorRGBA.White),
							factory.loadMarble(Purple));
		renderPieces();
	}
	
	/**
	 * Set the 3D mesh model set
	 */
	public void setModelIndex(int modelIndex) {	this.modelIndex = modelIndex;	}
	
	public int getModelIndex() {	return this.modelIndex;	}
	
	public BoardManager getBoardManager() {	return this.boardManager;	}
	
	/**
	 * Set the material for the pieces
	 */
	public void setPieceMaterial(Material lightMat, Material darkMat)
	{
		this.lightPieceMat = lightMat;
		this.darkPieceMat = darkMat;
	}
	
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
				int p = boardManager.getPiece(sq);
				
				Piece piece = null;
				if (p != NON)
				{
					piece = new Piece(
							(Geometry) assetManager.loadModel("Models/" + Piece.name(p) + this.modelIndex + ".j3o"),
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
	
	public final static String // Trigger maps
		MAP_SELECT = "MouseSelect",
		MAP_DESELECT = "MouseDeselect",
		MAP_BOARD = "Board",
		MAP_MODEL = "Model",
		MAP_1 = "1", 
		MAP_2 = "2", 
		MAP_3 = "3", 
		MAP_4 = "4";
	
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
	public ActionListener choiceListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
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
}
