package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.cutscene.CutsceneControlledActionConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class CutsceneEventAction extends AbstractCutsceneAction {
	
	public CutsceneEventAction(CutsceneControlledActionConfig actionConfig) {
		super(actionConfig);
	}
	
	@Override
	public void execute() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CUTSCENE_EVENT).setStringValue(actionConfig.globalEvent));
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
