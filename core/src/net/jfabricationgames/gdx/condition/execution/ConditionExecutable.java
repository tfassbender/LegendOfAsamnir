package net.jfabricationgames.gdx.condition.execution;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.global.GlobalEventConfig;

public class ConditionExecutable {
	
	public ConditionExecutableType type;
	public GlobalEventConfig eventConfig;
	public ConditionalExecution conditionalExecution;
	public ObjectMap<String, String> executionParameters;
	
	public void execute() {
		type.execute(this);
	}
}
