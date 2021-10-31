package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jfabricationgames.gdx.cutscene.function.IsUnitMovingFunction;

public class CutsceneActionFactory {
	
	private static OrthographicCamera hudCamera;
	
	private CutsceneActionFactory() {}
	
	public static void setHudCamera(OrthographicCamera hudCamera) {
		CutsceneActionFactory.hudCamera = hudCamera;
	}
	
	public static AbstractCutsceneAction createAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig,
			IsUnitMovingFunction isUnitMovingFunction) {
		switch (actionConfig.type) {
			case CHANGE_STATE:
				return new CutsceneChangeStateAction(unitProvider, actionConfig);
			case EVENT:
				return new CutsceneEventAction(unitProvider, actionConfig);
			case MOVE:
				return new CutsceneMoveAction(unitProvider, actionConfig);
			case MOVE_CAMERA:
				return new CutsceneMoveCameraAction(unitProvider, actionConfig, isUnitMovingFunction);
			case WAIT:
				return new CutsceneWaitAction(unitProvider, actionConfig);
			case SHOW_ON_SCREEN_TEXT:
				return new CutsceneShowOnScreenTextAction(unitProvider, actionConfig);
			case PLAYER_CHOICE:
				return new CutscenePlayerChoiceAction(unitProvider, actionConfig);
			case CONDITION:
				return new CutsceneConditionAction(unitProvider, actionConfig);
			case COLOR_TRANSITION:
				return new CutsceneColorTransitionAction(unitProvider, actionConfig, hudCamera);
			default:
				throw new IllegalStateException("Unexpected CutsceneControlledActionType in parameter actionConfig: " + actionConfig.type);
		}
	}
}
