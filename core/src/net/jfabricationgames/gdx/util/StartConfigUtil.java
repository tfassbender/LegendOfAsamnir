package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.rune.RuneType;

/**
 * Used to fire events, that are configured in a configuration file, to be able to start a map with the pre-configured settings (like runes, items, ...)
 */
public class StartConfigUtil {
	
	private static final String GAME_LOADED_START_CONFIG_NAME = "game_loaded";
	
	private static StartConfigEventListener eventListener;
	
	public static void configureMapStartConfig(String startConfigPath, int initialStartingPointId) {
		initializeEventListener();
		
		if (startConfigPath == null || startConfigPath.isEmpty()) {
			Gdx.app.debug(StartConfigUtil.class.getSimpleName(), "No start settings configured.");
			return;
		}
		
		Gdx.app.debug(StartConfigUtil.class.getSimpleName(),
				"Executing events from start configuration file: " + startConfigPath + " with starting point id: " + initialStartingPointId);
		StartConfig startConfig = loadStartConfigFromPath(startConfigPath);
		executeStartConfigEvents(startConfig, initialStartingPointId);
	}
	
	public static void executeGameLoadStartConfig(String startConfigPath) {
		if (startConfigPath == null || startConfigPath.isEmpty()) {
			Gdx.app.debug(StartConfigUtil.class.getSimpleName(), "No start settings configured.");
			return;
		}
		
		Gdx.app.debug(StartConfigUtil.class.getSimpleName(),
				"Executing events from start configuration file: " + startConfigPath + " after game loaded.");
		StartConfig startConfig = loadStartConfigFromPath(startConfigPath);
		String configName = startConfig.startingPointMapping.get(GAME_LOADED_START_CONFIG_NAME);
		if (configName != null) {
			executeStartConfigEvents(startConfig, configName);
		}
	}
	
	private static void initializeEventListener() {
		if (eventListener == null) {
			eventListener = new StartConfigEventListener();
			EventHandler.getInstance().registerEventListener(eventListener);
		}
	}
	
	private static StartConfig loadStartConfigFromPath(String startConfigPath) {
		Json json = new Json();
		StartConfig startConfig = json.fromJson(StartConfig.class, Gdx.files.internal(startConfigPath));
		return startConfig;
	}
	
	private static void executeStartConfigEvents(StartConfig startConfig, int initialStartingPointId) {
		if (startConfig.startingPointMapping != null) {
			String configName = startConfig.startingPointMapping.get(Integer.toString(initialStartingPointId));
			executeStartConfigEvents(startConfig, configName);
		}
	}
	
	private static void executeStartConfigEvents(StartConfig startConfig, String configName) {
		if (configName == null) {
			Gdx.app.debug(StartConfigUtil.class.getSimpleName(), "The given config name is null. The start config events cannot be executed.");
			return;
		}
		
		StartConfigEventList eventList = startConfig.eventsLists.get(configName);
		EventHandler eventHandler = EventHandler.getInstance();
		
		if (eventList.events != null) {
			for (EventConfig event : eventList.events) {
				eventHandler.fireEvent(event);
			}
		}
		
		executeSubListConfigEvents(startConfig, eventList);
	}
	
	private static void executeSubListConfigEvents(StartConfig startConfig, StartConfigEventList eventList) {
		if (eventList.subConfigName == null) {
			Gdx.app.debug(StartConfigUtil.class.getSimpleName(), "No sub event list configured. Ending start configuration events.");
			return;
		}
		
		String subConfigPath = eventList.subConfigPath;
		Gdx.app.debug(StartConfigUtil.class.getSimpleName(), "Executing sub event list from start configuration file: " + subConfigPath
				+ " (null means same file) with config name: " + eventList.subConfigName);
		
		StartConfig subConfig = startConfig;
		if (eventList.subConfigPath != null) {
			subConfig = loadStartConfigFromPath(subConfigPath);
		}
		
		executeStartConfigEvents(subConfig, eventList.subConfigName);
	}
	
	//*************************************************
	//*** Event Listener
	//*************************************************
	
	private static class StartConfigEventListener implements EventListener {
		
		@Override
		public void handleEvent(EventConfig event) {
			if (event.eventType == EventType.COLLECT_RUNE) {
				RuneType type = RuneType.getByContainingName(event.stringValue);
				GameMapManager.getInstance().getMap().processRunePickUp(type);
				GlobalValuesDataHandler.getInstance().put(type.globalValueKeyCollected, true);
			}
		}
	}
	
	//*************************************************
	//*** Config
	//*************************************************
	
	public static class StartConfig {
		
		public ObjectMap<String, String> startingPointMapping;
		public ObjectMap<String, StartConfigEventList> eventsLists;
	}
	
	public static class StartConfigEventList {
		
		public String subConfigPath;
		public String subConfigName;
		public Array<EventConfig> events;
	}
}
