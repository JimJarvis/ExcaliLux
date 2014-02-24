package chess;

import utils.PP;

import com.jme3.renderer.*;
import com.jme3.scene.control.AbstractControl;

public class PieceMoveControl extends AbstractControl
{
	int newX;
	int newY;
	
	public PieceMoveControl(int[] newXY)
	{
		this.newX = newXY[0];
		this.newY = newXY[1];
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		((Piece) spatial).locate(newX, newY);
		spatial.removeControl(PieceSelectedControl.class);
		spatial.removeControl(this);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}

	
}
