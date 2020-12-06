package net.jfabricationgames.gdx.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.global.GlobalEventListener;

public class EventHandler {
	
	public static final String EVENT_GAME_STARTED = "gameStarted";
	
	private static final String EVENT_CONFIG_FILE = "config/events/events.json";
	
	private static EventHandler instance = new EventHandler();
	
	public static EventHandler getInstance() {
		return instance;
	}
	
	private Array<EventListener> listeners;
	private ObjectMap<String, EventConfig> events;
	
	private EventHandler() {
		listeners = new Array<>();
		loadEvents();
		createGlobalEventListener();
	}
	
	@SuppressWarnings("unchecked")
	private void loadEvents() {
		Json json = new Json();
		events = json.fromJson(ObjectMap.class, EventConfig.class, Gdx.files.internal(EVENT_CONFIG_FILE));
	}
	
	private void createGlobalEventListener() {
		GlobalEventListener.create(this);
	}
	
	public void fireEvent(EventConfig event) {
		for (EventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}
	
	public void registerEventListener(EventListener listener) {
		listeners.add(listener);
	}
	public void removeEventListener(EventListener listener) {
		listeners.removeValue(listener, false);
	}
	
	public EventConfig getEventByName(String name) {
		return events.get(name);
	}
}
