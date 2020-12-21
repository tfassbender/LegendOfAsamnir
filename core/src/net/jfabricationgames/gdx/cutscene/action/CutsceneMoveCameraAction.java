package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.camera.CameraMovementHandler;
import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.cutscene.function.IsUnitMovingFunction;

public class CutsceneMoveCameraAction extends AbstractCutsceneMoveAction {
	
	private CameraMovementHandler cameraMovementHandler;
	private IsUnitMovingFunction isUnitMovingFunction;
	
	public CutsceneMoveCameraAction(CutsceneControlledActionConfig actionConfig, IsUnitMovingFunction isUnitMovingFunction) {
		super(actionConfig);
		this.isUnitMovingFunction = isUnitMovingFunction;
		cameraMovementHandler = CameraMovementHandler.getInstance();
		
		findTarget();
	}
	
	@Override
	public void execute(float delta) {
		if (actionConfig.updatePositionRelativeToTarget) {
			findTarget();
		}
		
		moveCameraToTarget(delta);
	}
	
	private void moveCameraToTarget(float delta) {
		Vector2 movement = target.cpy().sub(cameraMovementHandler.getCameraPosition());
		movement.nor().scl(actionConfig.speedFactor);
		movement.scl(delta);
		cameraMovementHandler.moveCamera(movement.x, movement.y);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return followTargetFinished() && getDistanceToTarget() < MAX_DISTANCE_TO_TARGET_POINT;
	}
	
	private boolean followTargetFinished() {
		return !actionConfig.cameraFollowsTarget || !isUnitMovingFunction.apply(actionConfig.targetPositionRelativeToUnitId);
	}
	
	private float getDistanceToTarget() {
		return target.cpy().sub(cameraMovementHandler.getCameraPosition()).len();
	}
}
