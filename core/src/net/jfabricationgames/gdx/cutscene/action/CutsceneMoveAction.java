package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;

public class CutsceneMoveAction extends AbstractCutsceneAction {
	
	public static final float MAX_DISTANCE_TO_TARGET_POINT = 0.1f;
	
	private CutsceneMoveableUnit controlledUnit;
	private Vector2 target;
	
	public CutsceneMoveAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
		controlledUnit = getControlledUnitAs(CutsceneMoveableUnit.class);
		target = findTarget();
	}
	
	private Vector2 findTarget() {
		if (actionConfig.controlledUnitId.equals(actionConfig.targetPositionRelativeToUnitId) && actionConfig.updatePositionRelativeToTarget) {
			throw new IllegalStateException(
					"The target position can't be relative to the controlled unit AND be updated. The unit will never reach the target.");
		}
		
		CutsceneMoveableUnit unit;
		if (actionConfig.targetPositionRelativeToUnitId != null) {
			unit = getUnitAs(actionConfig.targetPositionRelativeToUnitId, CutsceneMoveableUnit.class);
		}
		else {
			unit = getControlledUnitAs(CutsceneMoveableUnit.class);
		}
		
		return unit.getPosition().cpy().add(actionConfig.controlledUnitTarget);
	}
	
	@Override
	public void execute() {
		if (actionConfig.updatePositionRelativeToTarget) {
			findTarget();
		}
		
		moveControlledUnitToTarget();
	}
	
	private void moveControlledUnitToTarget() {
		controlledUnit.changeToMovingState();
		controlledUnit.moveTo(target);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return getDistanceToTarget() < MAX_DISTANCE_TO_TARGET_POINT;
	}
	
	private float getDistanceToTarget() {
		return target.cpy().sub(controlledUnit.getPosition()).len();
	}
}
