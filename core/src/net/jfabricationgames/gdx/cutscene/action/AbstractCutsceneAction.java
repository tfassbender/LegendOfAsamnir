package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.cutscene.function.CutsceneFunctionAction;
import net.jfabricationgames.gdx.event.global.GlobalEventConfig;

public abstract class AbstractCutsceneAction implements CutsceneFunctionAction, Disposable {
	
	protected CutsceneControlledActionConfig actionConfig;
	protected float executionTimeInSeconds = 0f;
	
	private CutsceneUnitProvider unitProvider;
	
	public AbstractCutsceneAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		this.unitProvider = unitProvider;
		this.actionConfig = actionConfig;
	}
	
	public abstract void execute(float delta);
	public abstract boolean isExecutionFinished();
	
	public void increaseExecutionTime(float delta) {
		executionTimeInSeconds += delta;
	}
	
	public boolean isExecutionDelayPassed() {
		return executionTimeInSeconds >= actionConfig.executionDelayInSeconds;
	}
	
	@Override
	public boolean isMoveAction() {
		return false;
	}
	
	public Array<String> getFollowingActions() {
		return actionConfig.executes;
	}
	
	protected <T extends CutsceneControlledUnit> T getControlledUnitAs(Class<T> clazz) {
		return getUnitAs(actionConfig.controlledUnitId, clazz);
	}
	
	protected <T extends CutsceneControlledUnit> T getUnitAs(String unitId, Class<T> clazz) {
		CutsceneControlledUnit controlledUnit = null;
		controlledUnit = unitProvider.getUnitById(unitId);
		
		if (controlledUnit == null) {
			throw new IllegalStateException("The controlled unit with the id '" + unitId + "' was not found.");
		}
		if (!clazz.isAssignableFrom(controlledUnit.getClass())) {
			throw new IllegalStateException(
					"The controlled unit with the id '" + unitId + "' cannot be cast to the requested class '" + clazz.getSimpleName() + "'");
		}
		return clazz.cast(controlledUnit);
	}
	
	@Override
	public String getControlledUnitId() {
		return actionConfig.controlledUnitId;
	}
	
	protected void executeGeneratedEvent(GlobalEventConfig generatedEvent) {
		generatedEvent.executionType.execute(generatedEvent);
	}
	
	@Override
	public void dispose() {}
}
