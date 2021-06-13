package net.jfabricationgames.gdx.condition.values;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class GlobalConditionValues implements EventListener {
	
	private static GlobalConditionValues instance;
	
	public static synchronized GlobalConditionValues getInstance() {
		if (instance == null) {
			instance = new GlobalConditionValues();
		}
		return instance;
	}
	
	private ObjectMap<String, String> globalValues;
	private Json json;
	
	private GlobalConditionValues() {
		globalValues = new ObjectMap<>();
		json = new Json();
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void put(String key, String value) {
		globalValues.put(key, value);
	}
	
	public boolean isValueEqual(String key, String value) {
		if (value == null) {
			return !globalValues.containsKey(key);
		}
		else {
			return value.equals(globalValues.get(key));
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.SET_GLOBAL_CONDITION_VALUE) {
			@SuppressWarnings("unchecked")
			ObjectMap<String, String> keyAndValue = json.fromJson(ObjectMap.class, String.class, event.stringValue);
			put(keyAndValue.get("key"), keyAndValue.get("value"));
		}
	}
}
