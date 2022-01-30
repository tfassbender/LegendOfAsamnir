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
	
	private static final String PARAMETER_KEY_GLOBAL_VALUE_KEY = "key";
	private static final String PARAMETER_KEY_GLOBAL_VALUE = "value";
	
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
	
	public void put(String key, boolean value) {
		put(key, Boolean.toString(value));
	}
	
	public boolean isValueEqual(String key, String value) {
		if (value == null) {
			return !globalValuesContainer.globalValues.containsKey(key);
		}
		else {
			return value.equals(globalValuesContainer.globalValues.get(key));
		}
	}
	
	public boolean getAsBoolean(String key) {
		return isValueEqual(key, "true");
	}
	
	public String get(String key) {
		return globalValuesContainer.globalValues.get(key);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.SET_GLOBAL_CONDITION_VALUE) {
			ObjectMap<String, String> keyAndValue = readParameterMap(event);
			String key = keyAndValue.get(PARAMETER_KEY_GLOBAL_VALUE_KEY);
			String value = keyAndValue.get(PARAMETER_KEY_GLOBAL_VALUE);
			
			Gdx.app.debug(getClass().getSimpleName(), "setting global value with key '" + key + "' to '" + value + "'");
			put(key, value);
		}
		else if (event.eventType == EventType.INCREASE_GLOBAL_CONDITION_VALUE) {
			ObjectMap<String, String> keyAndValue = readParameterMap(event);
			String key = keyAndValue.get(PARAMETER_KEY_GLOBAL_VALUE_KEY);
			int currentValue = getCurrentValueAsInteger(key);
			
			Gdx.app.debug(getClass().getSimpleName(), "increasing global value with key '" + key + "' to value '" + (currentValue + 1) + "'");
			put(key, Integer.toString(currentValue + 1));
		}
	}
	
	@SuppressWarnings("unchecked")
	private ObjectMap<String, String> readParameterMap(EventConfig event) {
		ObjectMap<String, String> keyAndValue;
		if (event.stringValue != null) {
			keyAndValue = json.fromJson(ObjectMap.class, String.class, event.stringValue);
		}
		else if (event.parameterObject instanceof ObjectMap) {
			keyAndValue = (ObjectMap<String, String>) event.parameterObject;
		}
		else {
			throw new IllegalStateException(
					"Either the stringValue or the parameterObject must be set when using a SET_GLOBAL_CONDITION_VALUE event");
		}
		return keyAndValue;
	}
	
	private int getCurrentValueAsInteger(String globalValueKey) {
		String currentValue = get(globalValueKey);
		if (currentValue == null) {
			return 0;
		}
		try {
			return Integer.parseInt(currentValue);
		}
		catch (NumberFormatException e) {
			Gdx.app.error(getClass().getSimpleName(), "The value of the global value with key '" + globalValueKey
					+ "' couldn't be interpreted as integer: " + currentValue + " (using 0 as fallback)", e);
			return 0;
		}
	}
}
