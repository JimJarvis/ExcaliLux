package control;

import utils.PP;
import utils.Util;
import chess.Board;
import chess.MaterialFactory;
import chess.Piece;

import com.jme3.math.Vector3f;
import com.jme3.renderer.*;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Add animation when a piece is captured
 */
public class PieceCapturedControl extends StagedControl
{
	private static Board board = Board.getInstance();
	private static MaterialFactory factory = MaterialFactory.getInstance();
	
	Piece captured;

	@Override
	protected void controlInit(float tpf)
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
	}

	@Override
	protected void controlProcess(float tpf)
	{
		captured.move(new Vector3f(0, tpf, 0));
		
		detach(captured.getLocalTranslation().y > 6);
	}

	@Override
	protected void controlDetach()
	{
		board.detach(captured);
	}
}
