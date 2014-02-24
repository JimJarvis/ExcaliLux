package chess;

import utils.PP;

import com.jme3.renderer.*;
import com.jme3.scene.control.AbstractControl;

public class PieceSelectedControl extends AbstractControl
{
	@Override
	protected void controlUpdate(float tpf)
	{
		spatial.rotate(0, 0, tpf);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}

	
}
