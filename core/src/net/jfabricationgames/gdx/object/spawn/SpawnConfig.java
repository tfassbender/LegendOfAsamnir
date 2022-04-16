package net.jfabricationgames.gdx.object.spawn;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.event.EventConfig;

public class SpawnConfig {
	
	public String spawnType;
	public String spawnTypeMapProperties;
	public Array<String> events;
	public Array<EventConfig> complexEvents;
}
