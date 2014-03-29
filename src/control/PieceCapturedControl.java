package control;

import java.util.Random;

import utils.MaterialFactory;
import chess.Board;
import chess.Piece;

import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

/**
 * @author Jim Fan  (c) 2014
 * Add animation when a piece is captured
 */
public class PieceCapturedControl extends StagedControl
{
	private static Board board = Board.getInstance();
	private static MaterialFactory factory = MaterialFactory.getInstance();
	
	Piece captured;
	float alphaThresh = 0f; // alphaFallOff threshold
	Random rand = new Random();
	Vector3f randRotate;

	@Override
	protected void controlInit(float tpf)
	{
		captured = (Piece) spatial;

		// Set this piece's material to be transparent
		factory.setTransparent(captured);
		
		captured.setQueueBucket(Bucket.Transparent);
		captured.setShadowMode(ShadowMode.Off);
		// Dissolve after random rotation
		randRotate = new Vector3f(
				rand.nextInt(5) * tpf * (rand.nextFloat() - 0.5f),
				rand.nextInt(5) * tpf * (rand.nextFloat() - 0.5f),
				rand.nextInt(5) * tpf * (rand.nextFloat() - 0.5f));
	}

	@Override
	protected void controlProcess(float tpf)
	{
		captured.move(new Vector3f(0, 2 * tpf, 0));
		captured.rotate(randRotate.x, randRotate.y, randRotate.z);
		// Dissolving effect
		factory.setAlphaFallOff(captured, alphaThresh += 0.5f * tpf);
		detach(alphaThresh >= 1f);
	}

	@Override
	protected void controlDetach()
	{
		board.detach(captured);
	}
}
