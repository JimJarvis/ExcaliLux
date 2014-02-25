package control;

import utils.PP;
import utils.Util;
import chess.Board;
import chess.BoardManager;
import chess.Piece;

import com.jme3.renderer.*;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Moves the piece and updates the BoardManager
 */
public class PieceMoveControl extends AbstractControl
{
	private int newSq;
	private int newX;
	private int newY;
	private BoardManager board = Board.getInstance().getBoardManager() ;
	
	public PieceMoveControl(int sq)
	{
		this.newSq = sq;
		int[] newXY = Util.toXY(sq);
		this.newX = newXY[0];
		this.newY = newXY[1];
	}


	@Override
	protected void controlUpdate(float tpf)
	{
		Piece me = (Piece) spatial;
		int oldSq = me.getSq();
		
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
		
		me.removeControl(PieceSelectedControl.class);
		me.removeControl(this);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
	
}
