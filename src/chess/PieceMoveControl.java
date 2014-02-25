package chess;

import utils.PP;
import utils.Util;

import com.jme3.renderer.*;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Moves the piece and updates the BoardManager
 */
public class PieceMoveControl extends AbstractControl
{
	int newSq;
	int newX;
	int newY;
	BoardManager board;
	
	public PieceMoveControl(int sq, BoardManager board)
	{
		this.newSq = sq;
		int[] newXY = Util.toXY(sq);
		this.newX = newXY[0];
		this.newY = newXY[1];

		this.board = board;
	}


	@Override
	protected void controlUpdate(float tpf)
	{
		Piece me = (Piece) spatial;
		int oldSq = me.getSq();
		
		if (newSq != oldSq)
		{
    		Piece captured = board.move(oldSq, newSq);
    		// Remove the captured model from the scene
    		board.detach(captured);
    		me.locate(newX, newY);
		}
		
		me.removeControl(PieceSelectedControl.class);
		me.removeControl(this);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}

	
}
