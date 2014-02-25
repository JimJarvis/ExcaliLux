package control;

import static utils.PP.p;
import static utils.Util.*;
import static chess.Board.*;
import chess.Board;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowRenderer;

public class FlipBoardState extends AbstractAppState
{
	
	// Chessboard with pieces
	private Board board = Board.getInstance();
	private float angle = 0;
	private final float ANGLE_MAX = (float) Math.PI;
	private AppStateManager stateManager;
	// Rotation direction
	private int dir = board.isShiftyPressed() ? 1 : -1;

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
