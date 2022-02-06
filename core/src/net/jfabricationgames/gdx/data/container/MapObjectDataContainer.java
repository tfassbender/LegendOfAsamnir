package net.jfabricationgames.gdx.data.container;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.properties.MapObjectStates;

public class MapObjectDataContainer {
	
	//map the objects ID to the properties
	public ObjectMap<String, MapObjectStates> mapObjectStates = new ObjectMap<>();
	
	public int uniqueObjectCount;
}
