package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;

public abstract class CutsceneActionFactory {
	
	public static AbstractCutsceneAction createAction(CutsceneControlledActionConfig actionConfig) {
		switch (actionConfig.type) {
			case CHANGE_STATE:
				return new CutsceneChangeStateAction(actionConfig);
			case EVENT:
				return new CutsceneEventAction(actionConfig);
			case MOVE:
				return new CutsceneMoveAction(actionConfig);
			case WAIT:
				return new CutsceneWaitAction(actionConfig);
			default:
				throw new IllegalStateException("Unexpected CutsceneControlledActionType in parameter actionConfig: " + actionConfig.type);
		}
	}
}
