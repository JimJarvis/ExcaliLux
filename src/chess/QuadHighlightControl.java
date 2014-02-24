package chess;

import utils.PP;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class QuadHighlightControl extends AbstractControl
{
	ColorRGBA c;
	// This variable is used to implement the "one-off" behavior
	// The highlighted quad should stay high only as long as the mouse hovers over it. 
	boolean highlighted = false;
	ColorRGBA original;
	
	public QuadHighlightControl(ColorRGBA c)
	{
		this.c = c;
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		if (!highlighted)
		{
    		this.original = 
    				(ColorRGBA) ((Geometry) this.spatial)
    										.getMaterial().getParam("Color").getValue();
			setColor(this.c);
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
		((Geometry) spatial).getMaterial().setColor("Color", c);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}

	
}
