package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.execution.ConditionalExecution;
import net.jfabricationgames.gdx.event.EventConfig;

public class GlobalEventConfig {
	
	public EventConfig event;
	public GlobalEventExecutionType executionType;
	public ConditionalExecution condition;
	
	public ObjectMap<String, String> executionParameters;
	public Object parameterObject;
}
