package net.jfabricationgames.gdx.data.handler;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.container.MapObjectDataContainer;
import net.jfabricationgames.gdx.data.properties.MapObjectStateProperties;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class MapObjectDataHandler {
	
	public static final String TYPE_DESCRIPTION_MAP_KEY = "__mapObjectType";
	
	private static MapObjectDataHandler instance;
	
	public static synchronized MapObjectDataHandler getInstance() {
		if (instance == null) {
			instance = new MapObjectDataHandler();
		}
		return instance;
	}
	
	private MapObjectDataContainer properties;
	
	private Json json = new Json();
	
	public void updateData(GameDataContainer gameDataContainer) {
		properties = gameDataContainer.mapObjectDataContainer;
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.UPDATE_MAP_OBJECT_STATES).setParameterObject(this));
	}
	
	public ObjectMap<String, String> getStateById(String mapObjectId) {
		if (mapObjectId == null) {
			return null;
		}
		
		MapObjectStateProperties mapObjectStateProperties = properties.mapObjectStates.get(mapObjectId);
		if (mapObjectStateProperties == null) {
			return null;
		}
		
		return mapObjectStateProperties.state;
	}
	
	public void addStatefulMapObject(StatefulMapObject mapObject) {
		String mapObjectId = mapObject.getMapObjectId();
		if (mapObjectId != null) {
			try {
				MapObjectStateProperties serializedState = serializeMapObjectState(mapObject);
				if (!serializedState.state.isEmpty()) {
					addObjectTypeDescription(mapObject, serializedState);
					properties.mapObjectStates.put(mapObjectId, serializedState);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				Gdx.app.error(getClass().getSimpleName(), "Exception during serialization of StatefulMapObject", e);
			}
		}
	}

	private MapObjectStateProperties serializeMapObjectState(StatefulMapObject mapObject) throws IllegalArgumentException, IllegalAccessException {
		ObjectMap<String, String> state = new ObjectMap<>();
		Array<Field> stateFields = getStateFields(mapObject);
		
		for (Field field : stateFields) {
			boolean accessible = field.canAccess(mapObject);
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
}
