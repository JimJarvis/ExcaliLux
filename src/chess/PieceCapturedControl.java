package chess;

import utils.PP;
import utils.Util;

import com.jme3.math.Vector3f;
import com.jme3.renderer.*;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Add animation when a piece is captured
 */
public class PieceCapturedControl extends AbstractControl
{
	private static Board board = Board.getInstance();
	private static MaterialFactory factory = MaterialFactory.getInstance();
	
	private enum Stage { Init, Update, Detach };
	private Stage stage = Stage.Init;
	Piece captured;
	
	@Override
	protected void controlUpdate(float tpf)
	{
		if (stage == Stage.Init)
		{
			captured = (Piece) spatial;
			switch (board.getModelIndex())
			{
			case 1: 
				captured.setMaterial(
						factory.loadMarbleTransparent(
										Util.getColor(captured)));
				break;
			}
			captured.setQueueBucket(Bucket.Transparent);
			captured.setShadowMode(ShadowMode.Off);
			stage = Stage.Update;
		}

		// Update
		captured.move(new Vector3f(0, tpf, 0));
		
		if (captured.getLocalTranslation().y > 6)
			detach();
		
		// Remove the captured model from the scene
		if (stage == Stage.Detach)
		{
    		board.detach(captured);
    		captured.removeControl(this);
		}
	}
	
	// Destroy this controller
	private void detach()
	{
		stage = Stage.Detach;
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
	
}
