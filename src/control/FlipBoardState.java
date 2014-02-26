package control;

import chess.Board;

import com.jme3.app.*;
import com.jme3.app.state.*;

/**
 * @author Jim Fan  (c) 2014
 * Flips the board view
 */
public class FlipBoardState extends AbstractAppState
{
	
	// Chessboard with pieces
	private Board board = Board.getInstance();
	private float angle = 0;
	private final float ANGLE_MAX = (float) Math.PI;
	private AppStateManager stateManager;
	// Rotation direction
	private int dir = board.isShiftyPressed() ? -1 : 1;

	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.stateManager = app.getStateManager();
	}
	
	/**
	 * Animation: rotate the board camera to the other side
	 */
	@Override
	public void update(float tpf)
	{
		if (angle + 3 * tpf >= ANGLE_MAX)
		{
			board.rotateBoardCam(ANGLE_MAX - angle);
			this.stateManager.detach(this);
		}
		
		board.rotateBoardCam((float) dir * 3 * tpf);
		
		angle += 3 * tpf;
	}
	
	@Override
	public void cleanup() {}
}
