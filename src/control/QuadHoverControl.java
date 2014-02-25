package control;

import utils.Util;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Highlights the quad when the mouse hovers over it
 */
public class QuadHoverControl extends AbstractControl
{
	private static final ColorRGBA HIGH_COLOR = ColorRGBA.Yellow;

	// This variable is used to implement the "one-off" behavior
	// The highlighted quad should stay high only as long as the mouse hovers over it. 
	private boolean highlighted = false;
	private ColorRGBA original;
	
	@Override
	protected void controlUpdate(float tpf)
	{
		// SelectedControl and HoverControl are mutually exclusive. 
		if (spatial == null) return; // means it's already detached
		
		if (!highlighted)
		{
    		this.original = Util.getColor(spatial);
			setColor(HIGH_COLOR);
			highlighted = true;
		}
		else
		{
			setColor(this.original);
			spatial.removeControl(this);
		}
	}
	
	private void setColor(ColorRGBA c)
	{
		Util.setColor(spatial, c);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
	
}
