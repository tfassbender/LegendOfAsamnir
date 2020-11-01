package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;

public class GlobalEventConfig {
	
	public EventConfig event;
	public GlobalEventExecutionType executionType;
	public ObjectMap<String, String> executionParameters;
}
