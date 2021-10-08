package net.jfabricationgames.gdx.cutscene.action;

public class CutsceneChangeStateAction extends AbstractCutsceneAction {
	
	public CutsceneChangeStateAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
	}
	
	@Override
	public void execute(float delta) {
		CutsceneControlledStatefullUnit unit = getControlledUnitAs(CutsceneControlledStatefullUnit.class);
		CutsceneControlledState state = unit.getState(actionConfig.controlledUnitState);
		state.setAttackDirection(actionConfig.controlledUnitAttackTargetDirection);
		unit.setState(state);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
