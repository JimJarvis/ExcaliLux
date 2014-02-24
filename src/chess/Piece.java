package chess;

import utils.PP;
import utils.Util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import static chess.Board.*;

/**
 * @author Jim Fan  (c) 2014
 */
public class Piece extends Geometry
{
	// sides: white/black
	public static final int W = 0, B = 1,
			SIDE_N = 2;
	
	// pieces
	public static final int NON = 0, 
			PAWN = 1,
			KNIGHT = 2, 
			BISHOP = 3, 
			ROOK = 4,
			QUEEN = 5,
			KING = 6,
			PIECE_N = 7;
	
	// piece names
	public static final String PIECE_NAMES[] = 
		{"", "Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
	
	private int x; // file
	private int y; // rank
	
	/**
	 * A new chess piece
	 * The name will simply be a 0-63 square number
	 * the Board will keep track of which Piece is on that square
	 * when we do the ray hitting selection. 
	 */
	public Piece(Geometry model, String name, Material mat, int x, int y)
	{
		// Copy ctor
		super(name, model.getMesh());
		this.setLocalRotation(model.getLocalRotation());
		this.setLocalScale(model.getLocalScale());
		this.setLocalTranslation(model.getLocalTranslation());
		
		this.setMaterial(mat);
		// Adding shadow: both cast and receive
		this.setShadowMode(ShadowMode.CastAndReceive);
		
		this.move(coordSq(this.x = x, this.y = y));
	}
	
	/**
	 * Move the piece to the new 3D positioin
	 * @param x rank
	 * @param y file
	 */
	public void locate(int x, int y)
	{
		this.move(coordSq(x, y).subtract(coordSq(this.x, this.y)));
		this.x = x;
		this.y = y;
	}
	
	public int getX() {	return this.x;	}
	public int getY() {	return this.y;	}
	public int getSq() {	return Util.toSq(x, y);	}
}
