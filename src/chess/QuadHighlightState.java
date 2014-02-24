package chess;

import static utils.PP.p;
import static utils.Util.*;
import static chess.Board.*;

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

public class QuadHighlightState extends AbstractAppState
{
	private SimpleApplication app;
	private InputManager inputManager;
	private Camera cam;
	private Node rootNode;
	
	// Chessboard with pieces
	private Board board = Board.getInstance();
	

	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
		this.cam = this.app.getCamera();
		this.rootNode = this.app.getRootNode();
		this.inputManager = this.app.getInputManager();
	}
	

	@Override
	public void update(float tpf)
	{
		// Highlight a board square when the mouse hovers over it
		CollisionResults results = new CollisionResults();
		Vector2f hover2d = inputManager.getCursorPosition();
		Vector3f hover3d = cam.getWorldCoordinates(hover2d, 0f); // depth 0
		// 1 WU deep into the screen
		Vector3f dir = cam.getWorldCoordinates(hover2d, 0.5f).subtractLocal(hover3d);
		 Ray ray  = new Ray(hover3d, dir);
		rootNode.collideWith(ray, results);
		
		for (CollisionResult res : results)
		{
			// Identify a quad by e.g. @23
			Geometry hit = res.getGeometry();
			String hitName = hit.getName();
			if (hitName.charAt(0) == '@')
			{
				hit.addControl(new QuadHighlightControl(ColorRGBA.Yellow));
				break;
			}
		}
	}
	
	@Override
	public void cleanup() {}
}
