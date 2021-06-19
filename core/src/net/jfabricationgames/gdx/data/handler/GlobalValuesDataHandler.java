package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.container.GlobalValuesContainer;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class GlobalValuesDataHandler implements DataHandler, EventListener {
	
	private static final String globalValuesAfterLoadingConfig = "config/data/globalValuesAfterLoading.json";
	
	private static GlobalValuesDataHandler instance;
	
	public static synchronized GlobalValuesDataHandler getInstance() {
		if (instance == null) {
			instance = new GlobalValuesDataHandler();
		}
		return instance;
	}
	
	private GlobalValuesContainer globalValuesContainer;
	private Json json;
	
	private GlobalValuesDataHandler() {
		json = new Json();
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		globalValuesContainer = dataContainer.globalValuesDataContainer;
		resetConfiguredGlobalValuesAfterLoad();
	}
	
	@SuppressWarnings("unchecked")
	private void resetConfiguredGlobalValuesAfterLoad() {
		FileHandle fileHandle = Gdx.files.internal(globalValuesAfterLoadingConfig);
		ObjectMap<String, String> valuesAfterLoading = json.fromJson(ObjectMap.class, String.class, fileHandle);
		
		for (ObjectMap.Entry<String, String> entry : valuesAfterLoading.entries()) {
			put(entry.key, entry.value);
		}
	}
	
	public void put(String key, String value) {
		globalValuesContainer.globalValues.put(key, value);
	}
	
	public boolean isValueEqual(String key, String value) {
		if (value == null) {
			return !globalValuesContainer.globalValues.containsKey(key);
		}
		else {
			return value.equals(globalValuesContainer.globalValues.get(key));
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
