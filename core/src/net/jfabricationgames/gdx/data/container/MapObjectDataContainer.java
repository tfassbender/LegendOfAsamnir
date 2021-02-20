package net.jfabricationgames.gdx.data.container;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.properties.MapObjectStateProperties;

public class MapObjectDataContainer {

	//map the objects ID to the properties
	public ObjectMap<String, MapObjectStateProperties> mapObjectStates = new ObjectMap<>();
}
