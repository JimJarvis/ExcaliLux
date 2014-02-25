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

public class PieceSelectedControl extends StagedControl
{
	private Board board = Board.getInstance();
	
	private Geometry quad;
	private Piece piece;
	private QuadSelectedControl selectControl;

	@Override
	protected void controlInit(float tpf)
	{
		piece = (Piece) this.spatial;
		quad = board.getQuadUnderSelectedPiece();
		selectControl = new QuadSelectedControl();
		quad.addControl(selectControl);
	}

	@Override
	protected void controlProcess(float tpf)
	{
		// this Piece should be the same as board.getSelectedPiece()
		// Rotate the piece w.r.t. vertical axis
		piece.rotate(0, 0, tpf);
		
		detach(board.getSelectedPiece() != piece);
	}

	@Override
	protected void controlDetach()
	{
		// Restore the old quad color
		selectControl.detach();
	}
	
}
