package net.jfabricationgames.gdx.cutscene.action;

public class CutsceneWaitAction extends AbstractCutsceneAction {
	
	public CutsceneWaitAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
	}
	
	@Override
	public void execute(float delta) {
		//do nothing here; only wait
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
