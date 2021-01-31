package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;

public abstract class CutsceneActionFactory {
	
	public static AbstractCutsceneAction createAction(CutsceneControlledActionConfig actionConfig, CutsceneHandler handler) {
		switch (actionConfig.type) {
			case CHANGE_STATE:
				return new CutsceneChangeStateAction(actionConfig);
			case EVENT:
				return new CutsceneEventAction(actionConfig);
			case MOVE:
				return new CutsceneMoveAction(actionConfig);
			case MOVE_CAMERA:
				return new CutsceneMoveCameraAction(actionConfig, handler.createIsUnitMovingFunction());
			case WAIT:
				return new CutsceneWaitAction(actionConfig);
			case SHOW_ON_SCREEN_TEXT:
				return new CutsceneShowOnScreenTextAction(actionConfig);
			case PLAYER_CHOICE:
				return new CutscenePlayerChoiceAction(actionConfig);
			default:
				throw new IllegalStateException("Unexpected CutsceneControlledActionType in parameter actionConfig: " + actionConfig.type);
		}
	}
}
