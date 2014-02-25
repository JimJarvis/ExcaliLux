package control;

import utils.PP;
import utils.Util;
import chess.Board;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
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
