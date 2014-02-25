package control;

import utils.Util;
import chess.Board;

import com.jme3.math.ColorRGBA;

/**
 * @author Jim Fan  (c) 2014
 * When the quad is under the selected piece
 */
public class QuadSelectedControl extends StagedControl
{
	private Board board = Board.getInstance();
	
	private static final ColorRGBA HIGH_COLOR = ColorRGBA.Green;
	private ColorRGBA original;
	
	@Override
	protected void controlInit(float tpf)
	{
		if (original == null)
			original = Util.getColor(spatial);
		Util.setColor(spatial, HIGH_COLOR);
	}

	@Override
	protected void controlProcess(float tpf)
	{
		// We don't want hovering to be activated on the selected quad
		spatial.removeControl(QuadHoverControl.class);
	}

	@Override
	protected void controlDetach()
	{
		// Restore the old color
		Util.setColor(spatial, original);
	}
	
}
