package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;

public class CutsceneWaitAction extends AbstractCutsceneAction {
	
	public CutsceneWaitAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
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
