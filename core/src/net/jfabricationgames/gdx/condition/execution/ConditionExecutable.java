package net.jfabricationgames.gdx.condition.execution;

import net.jfabricationgames.gdx.event.global.GlobalEventConfig;

public class ConditionExecutable {
	
	public ConditionExecutableType type;
	public GlobalEventConfig eventConfig;
	public ConditionalExecution conditionalExecution;
	
	public void execute() {
		type.execute(this);
	}
}
