package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.cutscene.function.IsUnitMovingFunction;

public abstract class CutsceneActionFactory {
	
	public static AbstractCutsceneAction createAction(CutsceneControlledActionConfig actionConfig, IsUnitMovingFunction isUnitMovingFunction) {
		switch (actionConfig.type) {
			case CHANGE_STATE:
				return new CutsceneChangeStateAction(actionConfig);
			case EVENT:
				return new CutsceneEventAction(actionConfig);
			case MOVE:
				return new CutsceneMoveAction(actionConfig);
			case MOVE_CAMERA:
				return new CutsceneMoveCameraAction(actionConfig, isUnitMovingFunction);
			case WAIT:
				return new CutsceneWaitAction(actionConfig);
			case SHOW_ON_SCREEN_TEXT:
				return new CutsceneShowOnScreenTextAction(actionConfig);
			case PLAYER_CHOICE:
				return new CutscenePlayerChoiceAction(actionConfig);
			case CONDITION:
				return new CutsceneConditionAction(actionConfig);
			default:
				throw new IllegalStateException("Unexpected CutsceneControlledActionType in parameter actionConfig: " + actionConfig.type);
		}
	}
}
