package net.jfabricationgames.gdx.object.spawn;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.EnemySpawnFactory;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.object.ItemSpawnFactory;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.util.MapUtil;

public class SpawnPoint extends GameObject implements EventListener, Disposable {
	
	private static final String MAP_PROPERTY_KEY_SPAWN_CONFIG = "spawn";
	
	private static final String SPAWN_CONFIG_FILE = "config/spawn/spawnConfigs.json";
	
	private static ObjectMap<String, SpawnConfig> spawnConfigs = loadSpawnConfigs();
	
	@SuppressWarnings("unchecked")
	private static ObjectMap<String, SpawnConfig> loadSpawnConfigs() {
		Json json = new Json();
		
		Array<String> spawnConfigFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(SPAWN_CONFIG_FILE));
		
		ObjectMap<String, SpawnConfig> spawnConfigs = new ObjectMap<String, SpawnConfig>();
		for (String configFile : spawnConfigFiles) {
			ObjectMap<String, SpawnConfig> configs = json.fromJson(ObjectMap.class, SpawnConfig.class, Gdx.files.internal(configFile));
			for (String key : configs.keys()) {
				if (spawnConfigs.containsKey(key)) {
					throw new IllegalStateException("The key '" + key + "' from the spawn config file '" + configFile
							+ "' was already added by another spawn config file. Duplicate keys are not allowed.");
				}
			}
			spawnConfigs.putAll(configs);
		}
		
		return spawnConfigs;
	}
	
	private SpawnConfig spawnConfig;
	
	private EnemySpawnFactory enemySpawnFactory;
	private ItemSpawnFactory itemSpawnFactory;
	
	@MapObjectState
	private boolean spawnedObjectPresentInMap;
	
	public SpawnPoint(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		loadSpawnConfigFromMapProperties();
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {
		super.applyState(state);
		
		spawnedObjectPresentInMap = Boolean.parseBoolean(state.get("spawnedObjectPresentInMap", "false"));
		return;
	}
	
	private void loadSpawnConfigFromMapProperties() {
		if (!mapProperties.containsKey(MAP_PROPERTY_KEY_SPAWN_CONFIG)) {
			throw new IllegalStateException("This SpawnPoint has no spawn config in it's map properties. SpawnPoint: " + mapPropertiesToString());
		}
		String spawnConfigName = mapProperties.get(MAP_PROPERTY_KEY_SPAWN_CONFIG, String.class);
		spawnConfig = spawnConfigs.get(spawnConfigName);
		if (spawnConfig == null) {
			throw new IllegalStateException(
					"This SpawnPoint has a spawn config in it's map properties, that can't be found. SpawnPoint: " + mapPropertiesToString());
		}
		
		checkWhetherSpawnEventsExist();
	}
	
	private void checkWhetherSpawnEventsExist() {
		if (spawnConfig.events == null && spawnConfig.complexEvents == null) {
			throw new IllegalStateException("The spawn config of this SpawnPoint doesn't contain any events. SpawnPoint: " + mapPropertiesToString());
		}
		if (spawnConfig.events != null) {
			for (String eventName : spawnConfig.events) {
				if (EventHandler.getInstance().getEventByName(eventName) == null) {
					throw new IllegalStateException("The spawn config of this SpawnPoint contains an event key that can't be found: " + eventName
							+ ". SpawnConfig: " + mapPropertiesToString());
				}
			}
		}
		if (spawnConfig.complexEvents != null) {
			for (EventConfig event : spawnConfig.complexEvents) {
				if (event.eventType == null) {
					throw new IllegalStateException(
							"The spawn config of this SpawnPoint contains a complex event without an event type. SpawnConfig: "
									+ mapPropertiesToString());
				}
				// if the event type is unknown, it would have let to a json parse exception, so this does not need to be checked
			}
		}
	}
	
	private String mapPropertiesToString() {
		return MapUtil.mapPropertiesToString(mapProperties, true);
	}
	
	public void setEnemySpawnFactory(EnemySpawnFactory enemySpawnFactory) {
		this.enemySpawnFactory = enemySpawnFactory;
	}
	
	public void setItemSpawnFactory(ItemSpawnFactory itemSpawnFactory) {
		this.itemSpawnFactory = itemSpawnFactory;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		changeBodyToSensor();
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (isEventHandled(event)) {
			Gdx.app.debug(getClass().getSimpleName(), "Received event, that is handled by this spawn point: " + event);
			spawn();
			MapObjectDataHandler.getInstance().addStatefulMapObject(this);
		}
	}
	
	private boolean isEventHandled(EventConfig event) {
		if (event.eventType == EventType.GAME_LOADED) {
			if (!spawnedObjectPresentInMap) {
				//don't respawn after loading the game, if the spawned object was already removed
				return false;
			}
		}
		else if (spawnedObjectPresentInMap && event.eventType != EventType.MAP_ENTERED) {
			/** don't spawn the object twice, except for 
			 * - GAME_LOADED, because the state is set by the loading mechanisms but the objects are not spawned yet
			 * - MAP_ENTERED, because no objects will be spawned yet
			 */
			return false;
		}
		
		if (spawnConfig.events != null) {
			for (String eventName : spawnConfig.events) {
				EventConfig handledEvent = EventHandler.getInstance().getEventByName(eventName);
				if (handledEvent != null && handledEvent.eventType == event.eventType) {
					return true;
				}
			}
		}
		if (spawnConfig.complexEvents != null) {
			for (EventConfig handledEvent : spawnConfig.complexEvents) {
				if (handledEvent.equals(event)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void spawn() {
		if (spawnConfig.spawnType == null) {
			throw new IllegalStateException("The spawn config doesn't contain a spawnType. SpawnPoint: " + mapPropertiesToString());
		}
		
		String[] parts = spawnConfig.spawnType.split("[.]");
		if (parts.length != 2) {
			throw new IllegalStateException("Object name couldn't be parsed (unexpected format): " + spawnConfig.spawnType);
		}
		
		MapProperties mapProperties = new MapProperties();
		addPropertiesFromSpawn(mapProperties);
		if (spawnConfig.spawnTypeMapProperties != null) {
			mapProperties = MapUtil.createMapPropertiesFromString(spawnConfig.spawnTypeMapProperties);
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Spawning object of type '" + spawnConfig.spawnType + "' with map properties: "
				+ MapUtil.mapPropertiesToString(mapProperties, false));
		
		switch (parts[0]) {
			case Constants.OBJECT_NAME_ITEM:
				createAndAddItemAfterWorldStep(parts[1], body.getPosition().x * Constants.SCREEN_TO_WORLD,
						body.getPosition().y * Constants.SCREEN_TO_WORLD, mapProperties, true);
				break;
			case Constants.OBJECT_NAME_OBJECT:
				createAndAddObjectAfterWorldStep(parts[1], body.getPosition().x * Constants.SCREEN_TO_WORLD,
						body.getPosition().y * Constants.SCREEN_TO_WORLD, mapProperties);
				break;
			case Constants.OBJECT_NAME_ENEMY:
				createAndAddEnemyAfterWorldStep(parts[1], body.getPosition().x * Constants.SCREEN_TO_WORLD,
						body.getPosition().y * Constants.SCREEN_TO_WORLD, mapProperties);
				break;
			default:
				throw new IllegalStateException("Unknown spawn type: " + spawnConfig.spawnType);
		}
		
		spawnedObjectPresentInMap = true;
	}
	
	private void addPropertiesFromSpawn(MapProperties properties) {
		Iterator<String> iter = mapProperties.getKeys();
		while (iter.hasNext()) {
			String key = iter.next();
			properties.put(key, mapProperties.get(key));
		}
	}
	
	private void createAndAddItemAfterWorldStep(String type, float x, float y, MapProperties mapProperties, boolean renderAboveGameObjects) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			itemSpawnFactory.createAndAddItem(type, x, y, mapProperties, renderAboveGameObjects, this::spawnObjectRemovedFromMap);
		});
	}
	
	private void createAndAddEnemyAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			enemySpawnFactory.createAndAddEnemy(type, x, y, mapProperties, this::spawnObjectRemovedFromMap);
		});
	}
	
	private void createAndAddObjectAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			GameObjectFactory.createAndAddObject(type, x, y, mapProperties, this::spawnObjectRemovedFromMap);
		});
	}
	
	private void spawnObjectRemovedFromMap() {
		spawnedObjectPresentInMap = false;
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		removeEventListener();
	}
	
	private void removeEventListener() {
		EventHandler.getInstance().removeEventListener(this);
	}
	
	@Override
	public void dispose() {
		removeEventListener();
	}
}
