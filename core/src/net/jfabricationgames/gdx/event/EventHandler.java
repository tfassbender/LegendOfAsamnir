package net.jfabricationgames.gdx.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class EventHandler {
	
	public static final String EVENT_GAME_STARTED = "NEW_GAME_STARTED";
	
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
	}
	
	@SuppressWarnings("unchecked")
	private void loadEvents() {
		Json json = new Json();
		events = json.fromJson(ObjectMap.class, EventConfig.class, Gdx.files.internal(EVENT_CONFIG_FILE));
	}
	
	public void fireEvent(EventConfig event) {
		Gdx.app.debug(getClass().getSimpleName(), "Firing event: " + event);
		// create a copy of the event listeners list, so it can be iterated (without problems because the iterator cannot be used nested) 
		// and the items in the real list can be removed while processing the whole list
		Array<EventListener> iterableListeners = new Array<>(listeners);
		for (EventListener listener : iterableListeners) {
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
