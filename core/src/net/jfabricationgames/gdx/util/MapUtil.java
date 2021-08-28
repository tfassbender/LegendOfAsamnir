package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class MapUtil {
	
	private MapUtil() {}
	
	public static String mapPropertiesToString(MapProperties properties, boolean includePosition) {
		return SerializationUtil.serializeMapProperties(properties, includePosition);
	}
	
	public static MapProperties createMapPropertiesFromString(String jsonConfig) {
		MapProperties mapProperties = new MapProperties();
		if (jsonConfig == null) {
			return mapProperties;
		}
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		ObjectMap<String, String> properties = json.fromJson(ObjectMap.class, String.class, jsonConfig);
		
		for (ObjectMap.Entry<String, String> property : properties) {
			mapProperties.put(property.key, property.value);
		}
		
		return mapProperties;
	}
}
