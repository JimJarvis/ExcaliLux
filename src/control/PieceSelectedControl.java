package control;

import utils.PP;
import utils.Util;
import chess.Board;
import chess.Piece;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class PieceSelectedControl extends AbstractControl
{
	private Board board = Board.getInstance();
	
	private Geometry quad = null;
	private QuadSelectedControl selectControl;
	
	@Override
	protected void controlUpdate(float tpf)
	{
		// this Piece should be the same as board.getSelectedPiece()
		Piece piece = (Piece) spatial; 
		// Rotate the piece w.r.t. vertical axis
		piece.rotate(0, 0, tpf);

		if (quad == null)
		{
			quad = board.getQuadUnderSelectedPiece();
			selectControl = new QuadSelectedControl();
			quad.addControl(selectControl);
		}
		
		if (board.getSelectedPiece() != piece)
		{
			// Restore the old quad state
			selectControl.detach();
			// Detach myself
			piece.removeControl(this);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
	
}
