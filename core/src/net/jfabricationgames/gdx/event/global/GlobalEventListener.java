package net.jfabricationgames.gdx.event.global;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;

public class GlobalEventListener implements EventListener {
	
	private static final String GLOBAL_EVENTS_CONFIG_FILE = "config/events/globalListenedEvents.json";
	
	public static void create(EventHandler eventHandler) {
		new GlobalEventListener(eventHandler);
	}
	
	private Map<String, GlobalEventConfig> events;//don't use an libGDX ObjectMap here, because an iterator that works nested is needed
	
	private GlobalEventListener(EventHandler eventHandler) {
		eventHandler.registerEventListener(this);
		loadGlobalEvents();
	}
	
	@SuppressWarnings("unchecked")
	private void loadGlobalEvents() {
		Json json = new Json();
		Array<String> configFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(GLOBAL_EVENTS_CONFIG_FILE));
		
		events = new HashMap<String, GlobalEventConfig>();
		for (String configFile : configFiles) {
			Map<String, GlobalEventConfig> configuredEvents = json.fromJson(HashMap.class, GlobalEventConfig.class, Gdx.files.internal(configFile));
			
			events.putAll(configuredEvents);
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		for (GlobalEventConfig eventConfig : events.values()) {
			if (eventConfig.event.equals(event)) {
				eventConfig.executionType.execute(eventConfig);
			}
		}
	}
}
