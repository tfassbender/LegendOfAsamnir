package net.jfabricationgames.gdx.data.handler;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.container.MapObjectDataContainer;
import net.jfabricationgames.gdx.data.properties.MapObjectStateProperties;
import net.jfabricationgames.gdx.data.properties.MapObjectStates;
import net.jfabricationgames.gdx.data.state.BeforePersistState;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.util.AnnotationUtil;

public class MapObjectDataHandler {
	
	public static final String TYPE_DESCRIPTION_MAP_KEY = "__mapObjectType";
	public static final String OBJECT_NOT_CONFIGURED_IN_MAP_PREFIX = "__not_configured_in_map__";
	
	private static MapObjectDataHandler instance;
	
	private AtomicInteger objectCounter = new AtomicInteger();
	
	public static synchronized MapObjectDataHandler getInstance() {
		if (instance == null) {
			instance = new MapObjectDataHandler();
		}
		return instance;
	}
	
	private MapObjectDataContainer properties;
	
	private Json json = new Json();
	
	private MapObjectDataHandler() {}
	
	public void updateData(GameDataContainer gameDataContainer) {
		properties = gameDataContainer.mapObjectDataContainer;
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.UPDATE_MAP_OBJECT_STATES));
	}
	
	public ObjectMap<String, MapObjectStateProperties> getCurrentMapStates() {
		MapObjectStates mapObjectStates = properties.mapObjectStates.get(getCurrentMapIdentifier());
		if (mapObjectStates == null) {
			return null;
		}
		
		return mapObjectStates.states;
	}
	
	public ObjectMap<String, String> getStateById(String mapObjectId) {
		if (mapObjectId == null) {
			return null;
		}
		
		MapObjectStateProperties mapObjectStateProperties = getMapObjectProperties(mapObjectId);
		if (mapObjectStateProperties == null) {
			return null;
		}
		
		return mapObjectStateProperties.state;
	}
	
	private MapObjectStateProperties getMapObjectProperties(String mapObjectId) {
		MapObjectStates currentMapStates = properties.mapObjectStates.get(getCurrentMapIdentifier());
		if (currentMapStates == null) {
			return null;
		}
		
		return currentMapStates.states.get(mapObjectId);
	}
	
	private String getCurrentMapIdentifier() {
		return GameMap.getInstance().getCurrentMapIdentifier();
	}
	
	public void addStatefulMapObject(StatefulMapObject mapObject) {
		String mapObjectId = mapObject.getMapObjectId();
		if (mapObjectId != null) {
			if (!mapObject.isConfiguredInMap()) {
				mapObjectId = OBJECT_NOT_CONFIGURED_IN_MAP_PREFIX + mapObjectId;
			}
			try {
				callBeforePersistStateMethods(mapObject);
				MapObjectStateProperties serializedState = serializeMapObjectState(mapObject);
				if (!serializedState.state.isEmpty()) {
					addObjectTypeDescription(mapObject, serializedState);
					setMapObjectProperties(mapObjectId, serializedState);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				Gdx.app.error(getClass().getSimpleName(), "Exception during serialization of StatefulMapObject", e);
			}
		}
	}
	
	private void callBeforePersistStateMethods(StatefulMapObject mapObject) {
		AnnotationUtil.executeAnnotatedMethods(BeforePersistState.class, mapObject);
	}
	
	private MapObjectStateProperties serializeMapObjectState(StatefulMapObject mapObject) throws IllegalArgumentException, IllegalAccessException {
		ObjectMap<String, String> state = new ObjectMap<>();
		Array<Field> stateFields = getStateFields(mapObject);
		
		for (Field field : stateFields) {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(mapObject);
			field.setAccessible(accessible);
			
			state.put(field.getName(), json.prettyPrint(value));
		}
		
		MapObjectStateProperties stateProperties = new MapObjectStateProperties();
		stateProperties.state = state;
		return stateProperties;
	}
	
	private Array<Field> getStateFields(StatefulMapObject mapObject) {
		Array<Field> stateFields = new Array<Field>();
		
		Class<?> clazz = mapObject.getClass();
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(MapObjectState.class)) {
					stateFields.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		} while (!clazz.equals(Object.class));
		
		return stateFields;
	}
	
	private void addObjectTypeDescription(StatefulMapObject mapObject, MapObjectStateProperties serializedState) {
		serializedState.state.put(TYPE_DESCRIPTION_MAP_KEY, mapObject.getClass().getSimpleName());
	}
	
	private void setMapObjectProperties(String mapObjectId, MapObjectStateProperties serializedState) {
		MapObjectStates currentMapStates = properties.mapObjectStates.get(getCurrentMapIdentifier());
		if (currentMapStates == null) {
			currentMapStates = new MapObjectStates();
			currentMapStates.states = new ObjectMap<>();
			properties.mapObjectStates.put(getCurrentMapIdentifier(), currentMapStates);
		}
		
		currentMapStates.states.put(mapObjectId, serializedState);
	}
	
	public synchronized int getUniqueObjectCount() {
		return objectCounter.addAndGet(1);
	}
}
