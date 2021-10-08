package net.jfabricationgames.gdx.cutscene.action;

public class CutsceneMoveAction extends AbstractCutsceneMoveAction {
	
	private CutsceneMoveableUnit controlledUnit;
	
	public CutsceneMoveAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
		controlledUnit = getControlledUnitAs(CutsceneMoveableUnit.class);
		findTarget();
	}
	
	@Override
	public void execute(float delta) {
		if (actionConfig.updatePositionRelativeToTarget) {
			findTarget();
		}
		
		moveControlledUnitToTarget();
	}
	
	private void moveControlledUnitToTarget() {
		controlledUnit.changeToMovingState();
		controlledUnit.moveTo(target.cpy(), actionConfig.speedFactor);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return getDistanceToTarget() < MAX_DISTANCE_TO_TARGET_POINT;
	}
	
	@Override
	public boolean isMoveAction() {
		return true;
	}
	
	private float getDistanceToTarget() {
		return target.cpy().sub(controlledUnit.getPosition()).len();
	}
}
