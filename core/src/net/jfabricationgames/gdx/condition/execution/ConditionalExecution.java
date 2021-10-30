package net.jfabricationgames.gdx.condition.execution;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.condition.ConditionHandler;

public class ConditionalExecution {
	
	public String conditionId;
	public ConditionExecutable thenCase;
	public ConditionExecutable elseCase;
	
	public void execute() {
		boolean conditionMet = ConditionHandler.getInstance().isConditionMet(conditionId);
		Gdx.app.debug(getClass().getSimpleName(), "Condition \"" + conditionId + "\" result is: " + conditionMet);
		if (conditionMet) {
			thenCase.execute();
		}
		else {
			elseCase.execute();
		}
	}
	
	@Override
	public String toString() {
		return "ConditionalExecution [conditionId=" + conditionId + "]";
	}
}
