package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.global.GlobalEventConfig;
import net.jfabricationgames.gdx.event.global.GlobalEventExecutionType;

public class CutscenePlayerChoiceAction extends AbstractCutsceneAction implements EventListener {
	
	private boolean eventFired = false;
	private boolean eventHandlingFinished = false;
	private int chosenOption = -1;
	
	public CutscenePlayerChoiceAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void execute(float delta) {
		if (!eventFired) {
			GlobalEventConfig generatedEvent = new GlobalEventConfig();
			generatedEvent.executionType = GlobalEventExecutionType.SHOW_PLAYER_CHOICE;
			generatedEvent.parameterObject = actionConfig.choice;
			
			EventHandler.getInstance().executeGeneratedEvent(generatedEvent);
			eventFired = true;
		}
	}
	
	@Override
	public boolean isExecutionFinished() {
		return eventHandlingFinished;
	}
	
	@Override
	public Array<String> getFollowingActions() {
		Array<String> chosenAction = new Array<>();
		chosenAction.add(actionConfig.choiceOptionExecutions.get(chosenOption));
		
		return chosenAction;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAYER_CHOICE && actionConfig.choice.choiceId.equals(event.stringValue)) {
			chosenOption = event.intValue;
			eventHandlingFinished = true;
			EventHandler.getInstance().removeEventListener(this);
		}
	}
}
