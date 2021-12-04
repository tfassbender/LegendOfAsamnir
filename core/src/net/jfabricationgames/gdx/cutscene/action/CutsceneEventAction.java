package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class CutsceneEventAction extends AbstractCutsceneAction implements EventListener {
	
	private boolean eventFired = false;
	private boolean eventHandlingFinished = false;
	
	public CutsceneEventAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void execute(float delta) {
		if (!eventFired) {
			// eventFired needs to be set before firing the event. Otherwise a change to a menu screen will cause an infinite loop and a stack overflow
			eventFired = true;
			
			if (actionConfig.event != null) {
				EventHandler.getInstance().fireEvent(actionConfig.event);
			}
			if (actionConfig.globalEvent != null) {
				EventHandler.getInstance().fireEvent(
						new EventConfig().setEventType(EventType.CUTSCENE_EVENT).setStringValue(actionConfig.globalEvent).setParameterObject(this));
			}
		}
	}
	
	@Override
	public boolean isExecutionFinished() {
		return !actionConfig.waitForEventToFinish || eventHandlingFinished;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.ON_SCREEN_TEXT_ENDED) {
			eventHandlingFinished = true;
			EventHandler.getInstance().removeEventListener(this);
		}
	}
}
