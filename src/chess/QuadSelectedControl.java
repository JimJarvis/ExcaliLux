package chess;

import utils.PP;
import utils.Util;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * When the quad is under the selected piece
 */
public class QuadSelectedControl extends AbstractControl
{
	private Board board = Board.getInstance();
	
	private static final ColorRGBA HIGH_COLOR = ColorRGBA.Green;
	private ColorRGBA original;
	private boolean detached = false;
	private boolean colorSet = false;
	
	@Override
	protected void controlUpdate(float tpf)
	{
		// We don't want hovering to be activated on the selected quad
		spatial.removeControl(QuadHoverControl.class);
		if (original == null)
			original = Util.getColor(spatial);

		if (!detached)
		{
			if (!colorSet)
			{
				Util.setColor(spatial, HIGH_COLOR);
				colorSet = true;
			}
		}
		else // detach and restore the old color
		{
			Util.setColor(spatial, original);
			spatial.removeControl(this);
		}
		
	}
	
	// Destroy this control object
	public void detach() {	detached = true;	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
	
}
