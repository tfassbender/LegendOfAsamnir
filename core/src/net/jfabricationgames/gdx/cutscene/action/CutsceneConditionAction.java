package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class CutsceneConditionAction extends AbstractCutsceneAction implements EventListener {
	
	private boolean conditionExecuted = false;
	private boolean eventHandlingFinished = false;
	
	private String conditionResult;
	
	public CutsceneConditionAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void execute(float delta) {
		if (!conditionExecuted) {
			actionConfig.condition.execute();
			conditionExecuted = true;
		}
	}
	
	@Override
	public boolean isExecutionFinished() {
		return eventHandlingFinished;
	}
	
	@Override
	public Array<String> getFollowingActions() {
		Array<String> chosenAction = new Array<>();
		chosenAction.add(actionConfig.conditionOptionExecutions.get(conditionResult));
		
		return chosenAction;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.CUTSCENE_CONDITION) {
			conditionResult = event.stringValue;
			eventHandlingFinished = true;
			EventHandler.getInstance().removeEventListener(this);
		}
	}
}
