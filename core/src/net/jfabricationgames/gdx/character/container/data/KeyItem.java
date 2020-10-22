package net.jfabricationgames.gdx.character.container.data;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class KeyItem {
	
	public static final String KEY_PROPERTY_PREFIX = "key_";
	public static final String COMMON_REQUIRED_PROPERTY = KEY_PROPERTY_PREFIX + "level";
	
	public static ObjectMap<String, String> getKeyProperties(MapProperties properties) {
		ObjectMap<String, String> keyProperties = new ObjectMap<>();
		
		Iterator<String> propertyKeysIterator = properties.getKeys();
		while (propertyKeysIterator.hasNext()) {
			String propertyKey = propertyKeysIterator.next();
			if (propertyKey.startsWith(KEY_PROPERTY_PREFIX)) {
				Object propertyValue = properties.get(propertyKey);
				if (propertyValue instanceof String) {
					keyProperties.put(propertyKey, (String) propertyValue);
				}
				else {
					Gdx.app.error(KeyItem.class.getSimpleName(), "The map properties contain a property that has a key property prefix (\""
							+ KEY_PROPERTY_PREFIX + "\"), but is no String. MapProperties: " + properties);
				}
			}
		}
		
		return keyProperties;
	}
	
	public static KeyItem fromMapProperties(MapProperties properties) {
		KeyItem key = new KeyItem();
		
		Iterator<String> mapPropertyKeysIterator = properties.getKeys();
		while (mapPropertyKeysIterator.hasNext()) {
			String mapPropertyKey = mapPropertyKeysIterator.next();
			Object mapPropertyValue = properties.get(mapPropertyKey);
			if (mapPropertyValue instanceof String) {
				key.mapProperties.put(mapPropertyKey, (String) mapPropertyValue);
			}
			else {
				Gdx.app.error(KeyItem.class.getSimpleName(),
						"The MapProperties contains values that are no Strings. Only String values are alowed for keys.");
			}
		}
		
		return key;
	}
	
	public static boolean isSpecialKey(ObjectMap<String, String> requiredProperties) {
		if (requiredProperties.size == 0 || (requiredProperties.size == 1 && requiredProperties.containsKey(COMMON_REQUIRED_PROPERTY))) {
			return false;
		}
		
		return true;
	}
	
	public static String getSpecialKeyPropertiesAsString(ObjectMap<String, String> properties) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : properties.entries()) {
			String propertyKey = entry.key.substring(KeyItem.KEY_PROPERTY_PREFIX.length());
			String propertyValue = entry.value;
			sb.append(propertyKey).append(':').append(' ').append(propertyValue).append('\n');
		}
		return sb.substring(0, sb.length() - 1);
	}
	
	public KeyItem() {
		this(new ObjectMap<>());
	}
	public KeyItem(ObjectMap<String, String> mapProperties) {
		this.mapProperties = mapProperties;
	}
	
	public ObjectMap<String, String> mapProperties;
	
	public boolean isSpecialKey() {
		return isSpecialKey(mapProperties);
	}
}
