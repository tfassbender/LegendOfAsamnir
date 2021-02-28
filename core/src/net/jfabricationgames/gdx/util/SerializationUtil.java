package net.jfabricationgames.gdx.util;

import java.util.Iterator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class SerializationUtil {
	
	private static Json json = new Json();
	
	private SerializationUtil() {}
	
	public static String serializeMapProperties(MapProperties mapProperties, boolean includePosition) {
		StringBuilder sb = new StringBuilder();
		
		Array<String> excludedKeys = new Array<>();
		if (!includePosition) {
			excludedKeys.addAll("x", "y", "width", "height");
		}
		
		sb.append('{');
		Iterator<String> keys = mapProperties.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = mapProperties.get(key).toString();
			if (!excludedKeys.contains(key, false)) {
				sb.append(key).append(": ").append(value).append(", ");
			}
		}
		sb.setLength(sb.length() - 2);
		sb.append('}');
		
		return sb.toString();
	}
	
	public static MapProperties deserializeMapProperties(String serialized) {
		if (serialized.startsWith("\"") && serialized.endsWith("\"")) {
			serialized = serialized.substring(1, serialized.length() - 1);
		}
		
		@SuppressWarnings("unchecked")
		ObjectMap<String, String> properties = json.fromJson(ObjectMap.class, String.class, serialized);
		
		MapProperties mapProperties = new MapProperties();
		for (ObjectMap.Entry<String, String> property : properties.entries()) {
			mapProperties.put(property.key, property.value);
		}
		
		return mapProperties;
	}
}
