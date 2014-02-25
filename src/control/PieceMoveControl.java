package control;

import utils.Util;
import chess.Board;
import chess.BoardManager;
import chess.Piece;

/**
 * @author Jim Fan  (c) 2014
 * Moves the piece and updates the BoardManager
 */
public class PieceMoveControl extends StagedControl
{
	private int newSq;
	private int newX;
	private int newY;
	private BoardManager board = Board.getInstance().getBoardManager() ;
	
	private Piece me;
	private int oldSq;
	
	public PieceMoveControl(int sq)
	{
		this.newSq = sq;
		int[] newXY = Util.toXY(sq);
		this.newX = newXY[0];
		this.newY = newXY[1];
	}


	@Override
	protected void controlInit(float tpf)
	{
		me = (Piece) this.spatial;
		oldSq = me.getSq();
	}

	@Override
	protected void controlProcess(float tpf)
	{
		// We don't do anything if we're trying to capture a friendly piece
		if (newSq != oldSq &&
				board.getSide(oldSq) != board.getSide(newSq))
		{
    		Piece captured = board.move(oldSq, newSq);
    		
    		// Takes care of the dissolving away effect
    		if (captured != null)
    			captured.addControl(new PieceCapturedControl());
    		
    		me.locate(newX, newY);
		}
		
		detach(); // unconditional exit after one update.
	}

	@Override
	protected void controlDetach()
	{
		me.removeControl(PieceSelectedControl.class);
	}
	
}
