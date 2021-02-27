package net.jfabricationgames.gdx.data.state;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

public interface StatefulMapObject {
	
	public static final String MAP_PROPERTIES_KEY_ID = "id";

	public static String getMapObjectId(MapProperties mapProperties) {
		Integer id = mapProperties.get(StatefulMapObject.MAP_PROPERTIES_KEY_ID, Integer.class);
		if (id == null) {
			return null;
		}
		
		return Integer.toString(id);
	}
	
	public String getMapObjectId();
	
	public default boolean isConfiguredMapObject() {
		return true;
	}
	
	public void applyState(ObjectMap<String, String> state);
}
