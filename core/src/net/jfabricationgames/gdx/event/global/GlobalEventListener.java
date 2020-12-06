package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;

public class GlobalEventListener implements EventListener {
	
	private static final String GLOBAL_EVENTS_CONFIG_FILE = "config/events/globalListenedEvents.json";
	
	public static void create(EventHandler eventHandler) {
		new GlobalEventListener(eventHandler);
	}
	
	private ObjectMap<String, GlobalEventConfig> events;
	
	private GlobalEventListener(EventHandler eventHandler) {
		eventHandler.registerEventListener(this);
		loadGlobalEvents();
	}
	
	@SuppressWarnings("unchecked")
	private void loadGlobalEvents() {
		Json json = new Json();
		events = json.fromJson(ObjectMap.class, GlobalEventConfig.class, Gdx.files.internal(GLOBAL_EVENTS_CONFIG_FILE));
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		for (GlobalEventConfig eventConfig : events.values()) {
			if (eventConfig.event.equals(event)) {
				eventConfig.executionType.execute(eventConfig.executionParameters);
			}
		}
	}
}
