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
	private Geometry quadBoardModels[] = new Geometry[SQ_N];
	
	// Piece meshes
	private Piece pieceModels[] = new Piece[SQ_N];
	
	/* Material texture configuration */
	private Material lightQuadMat; // for board color
	private Material darkQuadMat;
	private Material lightPieceMat; // for pieces
	private Material darkPieceMat;
	private int modelSet = 1; // which set of 3D models to be used
	
	/* Chess stats */
	// What pieces are on the board?
	private int boardPiece[] = new int[SQ_N];
	// What piece colors (sides) are on the board?
	private int boardSide[] = new int[SQ_N];
	private int turn; // whose turn? White or Black?
	// Castling rights
	// Castling encoding: 2 bits, msb = O-O-O, lsb = O-O
	// &1 get kingside; &2 get queenside; &=1 delete queenside; &=2 delete kingside
	private int castleRights[] = new int[SIDE_N];
	
	
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
		
		// Lay out the board made of quads
		setQuadMaterial(factory.loadPlain(ColorRGBA.Brown), 
                    		factory.loadPlain(ColorRGBA.LightGray));
		renderQuadBoard();
		
		// Put the default pieces
		parseFEN(FEN_START);

		// Re-render the scene
		setPieceMaterial(factory.loadMarble(ColorRGBA.White),
							factory.loadMarble(Purple));
		renderPieces();
	}
	
	/**
	 * Set the 3D mesh model set
	 */
	public void setModelSet(int modelSet) {	this.modelSet = modelSet;	}
	
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
				int p = boardPiece[sq];
				
				Piece piece = null;
				if (p != NON)
				{
					piece = new Piece(
							(Geometry) assetManager.loadModel("Models/" + PIECE_NAMES[p] + this.modelSet + ".j3o"),
							"*" + PIECE_NAMES[p],  // Piece names always start with asterisk
							boardSide[sq] == W ? this.lightPieceMat : this.darkPieceMat, x, y);
					rootNode.attachChild(piece);
				}
    			this.pieceModels[sq ++] = piece;
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
    			
    			this.quadBoardModels[sq ++] = quad;
    			rootNode.attachChild(quad);
    		}
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
	private QuadHighlightState quadHighlightState = new QuadHighlightState();
	
	// indicates which one is selected. null if none
	private Piece selectedPiece = null;
		
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
	    						selectedPiece.addControl(new PieceMoveControl(getQuadCoord(hitName)));
	    						stateManager.detach(quadHighlightState);
	    					}
    	    				break;
	    				}
	    				else if (hitName.charAt(0) == '*') // hits a piece
	    				{
	    					deselect();
	    					selectedPiece = (Piece) hit;
    						selectedPiece.addControl(new PieceSelectedControl());
    						// If a piece is selected, we allow quad highlighting
    						stateManager.attach(quadHighlightState);
    	    				break;
	    				}
					}
	    		}
				// Right click
				else if (name.equals(MAP_DESELECT))
				{
					deselect();
					stateManager.detach(quadHighlightState);
				}
					
			}
		};
	}
	
	// helper: remove selection
	private void deselect()
	{
		if (selectedPiece != null)
			selectedPiece.removeControl(PieceSelectedControl.class);
	}
	
	public int[] getQuadCoord(String name)
	{
		return toXY(Integer.parseInt(name.substring(1)));
	}
	
	/**
	 * Keyboard listener: changes models or piece/board material
	 */
	public ActionListener changeListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
			}
		};
	}
	
	
	/******************** Chess-related ********************/
	/**
	 * Default start position FEN string
	 */
	public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	/**
	 * Parse an FEN string and update the board record.
	 * No re-rendering
	 */
	private void parseFEN(String fen)
	{
		int rank = 7; // FEN starts from the top rank
		int file = 0; // left most file
		
		char ch; int i = 0;
		while ((ch = fen.charAt(i ++)) != ' ')
		{
			if (ch == '/') // move down a rank
			{
				-- rank;
				file = 0;
			}
			else if (Character.isDigit(ch)) // number means blank square, pass
				file += ch - '0';
			else
			{
				int side = Character.isUpperCase(ch) ? W : B;
				ch = Character.toLowerCase(ch);
				int piece = NON;
				switch (ch)
				{
				case 'p': piece = PAWN; break;
				case 'n': piece = KNIGHT; break;
				case 'b': piece = BISHOP; break;
				case 'r': piece = ROOK; break;
				case 'q': piece = QUEEN; break;
				case 'k': piece = KING; break;
				}
				// TODO add bit masks here
				int sq = toSq(file, rank);
				
				this.boardPiece[sq] = piece;
				this.boardSide[sq] = side;
				
				++ file;
			}
		}
		
		this.turn = fen.charAt(i ++) == 'w' ? W : B;
		i ++; // consume the white space
		
		// castling status, '-' if none available
		while ((ch = fen.charAt(i++)) != ' ')
		{
			int side = Character.isUpperCase(ch) ? W : B;
			ch = Character.toLowerCase(ch);
			switch (ch)
			{
			case 'k': castleRights[side] |= 1; break;
			case 'q': castleRights[side] |= 2; break;
			case '-': continue;
			}
		}
		
		// TODO enpassent square here
		// TODO fifty move and half move counter
		
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
