package chess;

import static chess.Piece.*;
import static chess.Board.*;
import static utils.Util.toSq;
import static utils.Util.toXY;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import utils.Util;

/**
 * Keep record of the chess pieces on board
 */
public class BoardManager
{
	// Piece 3D mesh models
	private Piece boardModels[] = new Piece[SQ_N];
	// What pieces are on the board?
	private int boardPieces[] = new int[SQ_N];
	// What piece colors (sides) are on the board?
	private int boardSides[] = new int[SQ_N];

	private int turn; // whose turn? White or Black?
	// Castling rights
	// Castling encoding: 2 bits, msb = O-O-O, lsb = O-O
	// &1 get kingside; &2 get queenside; &=1 delete queenside; &=2 delete kingside
	private int castleRights[] = new int[SIDE_N];
	
	private Node rootNode;
	
	/**
	 * Ctor with the default FEN string
	 */
	public BoardManager(Node rootNode)
	{
		this.rootNode = rootNode;
		this.parseFEN(FEN_START);
	}
	
	
	/**
	 * Get the piece at a specific square
	 */
	public int getPiece(int sq) {	return this.boardPieces[sq];	}
	
	/**
	 * Get the mesh model at a specific square
	 */
	public Piece getModel(int sq) {	return this.boardModels[sq];	}
	
	public void setModel(int sq, Piece model)
	{
		this.boardModels[sq] = model;
	}
	
	/**
	 * Get the color (side) of the piece at a specific square
	 */
	public int getSide(int sq) {	return this.boardSides[sq];	}
	
	/**
	 * Is the color of the piece at sq white?
	 */
	public boolean isWhite(int sq) {	return this.boardSides[sq] == W;	}
	
	/**
	 * Removes the piece, its side and its model
	 * @return removed piece model
	 */
	public Piece remove(int sq)
	{
		this.boardPieces[sq] = NON;
		this.boardSides[sq] = -1;
		Piece removed = this.boardModels[sq];
		this.boardModels[sq] = null;
		return removed;
	}
	
	/**
	 * Remove the rendering of a model, detach from RootNode.
	 */
	public void detach(Piece model)
	{
		if (model != null)
    		this.rootNode.detachChild(model);
	}
	
	/**
	 * Moves a piece from sq1 to sq2 and removes anything on sq2
	 * Also move the piece model
	 * Return the captured model, if any
	 */
	public Piece move(int sq1, int sq2)
	{
		this.boardPieces[sq2] = boardPieces[sq1];
		this.boardSides[sq2] = boardSides[sq1];
		Piece captured = this.boardModels[sq2];
		this.boardModels[sq2] = boardModels[sq1];
		remove(sq1);
		return captured;
	}

	/**
	 * Move the piece to the named quad
	 * Update board keeping
	 * Quad name looks like "@23"
	 */
	public PieceMoveControl moveControl(String quadName)
	{
		int sq = Integer.parseInt(quadName.substring(1));
		// PieceMoveControl updates the board keeping
		return new PieceMoveControl(sq, this);
	}
	
	/**
	 * Default start position FEN string
	 */
	public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	/**
	 * Parse an FEN string and update the board record.
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
				
				this.boardPieces[sq] = piece;
				this.boardSides[sq] = side;
				
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
}
