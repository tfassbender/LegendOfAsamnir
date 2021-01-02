package net.jfabricationgames.gdx.condition.execution;

import net.jfabricationgames.gdx.condition.ConditionHandler;

public class ConditionalExecution {
	
	public String conditionId;
	public ConditionExecutable thenCase;
	public ConditionExecutable elseCase;
	
	public void execute() {
		if (ConditionHandler.getInstance().checkCondition(conditionId)) {
			thenCase.execute();
		}
		else {
			elseCase.execute();
		}
	}
}
