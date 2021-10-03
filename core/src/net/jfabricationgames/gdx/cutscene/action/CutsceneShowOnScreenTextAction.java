package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.global.GlobalEventConfig;
import net.jfabricationgames.gdx.event.global.GlobalEventExecutionType;

public class CutsceneShowOnScreenTextAction extends AbstractCutsceneAction implements EventListener {
	
	private boolean eventFired = false;
	private boolean eventHandlingFinished = false;
	
	public CutsceneShowOnScreenTextAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void execute(float delta) {
		if (!eventFired) {
			GlobalEventConfig generatedEvent = new GlobalEventConfig();
			generatedEvent.executionType = GlobalEventExecutionType.SHOW_ON_SCREEN_TEXT;
			generatedEvent.executionParameters = actionConfig.executionParameters;
					
			executeGeneratedEvent(generatedEvent);
			eventFired = true;
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
