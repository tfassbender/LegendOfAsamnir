package net.jfabricationgames.gdx.object.spawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.enemy.EnemyFactory;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.screens.game.GameScreen;
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
		
		addGameStartEvents(spawnConfigs);
		
		return spawnConfigs;
	}
	
	private static void addGameStartEvents(ObjectMap<String, SpawnConfig> spawnConfigs) {
		for (SpawnConfig config : spawnConfigs.values()) {
			if (config.spawnOnGameStart) {
				if (config.events == null) {
					config.events = new Array<>();
				}
				config.events.add(EventHandler.EVENT_GAME_STARTED);
			}
		}
	}
	
	private SpawnConfig spawnConfig;
	
	public SpawnPoint(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties) {
		super(typeConfig, sprite, mapProperties);
		loadSpawnConfigFromMapProperties();
		EventHandler.getInstance().registerEventListener(this);
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
		if (spawnConfig.events == null) {
			throw new IllegalStateException("The spawn config of this SpawnPoint doesn't contain any events. SpawnPoint: " + mapPropertiesToString());
		}
		for (String eventName : spawnConfig.events) {
			if (EventHandler.getInstance().getEventByName(eventName) == null) {
				throw new IllegalStateException("The spawn config of this SpawnPoint contains an event key that can't be found: " + eventName
						+ ". SpawnConfig: " + mapPropertiesToString());
			}
		}
	}
	
	private String mapPropertiesToString() {
		return MapUtil.mapPropertiesToString(mapProperties, true);
	}
	
	@Override
	protected void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		changeBodyToSensor();
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (isEventHandled(event)) {
			spawn();
		}
	}
	
	private boolean isEventHandled(EventConfig event) {
		for (String eventName : spawnConfig.events) {
			EventConfig handledEvent = EventHandler.getInstance().getEventByName(eventName);
			if (handledEvent != null && handledEvent.equals(event)) {
				return true;
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
		if (spawnConfig.spawnTypeMapProperties != null) {
			mapProperties = MapUtil.createMapPropertiesFromString(spawnConfig.spawnTypeMapProperties);
		}
		
		switch (parts[0]) {
			case GameMap.OBJECT_NAME_ITEM:
				ItemFactory.createAndAddItemAfterWorldStep(parts[1], body.getPosition().x * GameScreen.SCREEN_TO_WORLD,
						body.getPosition().y * GameScreen.SCREEN_TO_WORLD, mapProperties, true);
				break;
			case GameMap.OBJECT_NAME_OBJECT:
				GameObjectFactory.createAndAddObjectAfterWorldStep(parts[1], body.getPosition().x * GameScreen.SCREEN_TO_WORLD,
						body.getPosition().y * GameScreen.SCREEN_TO_WORLD, mapProperties);
				break;
			case GameMap.OBJECT_NAME_ENEMY:
				EnemyFactory.createAndAddEnemyAfterWorldStep(parts[1], body.getPosition().x * GameScreen.SCREEN_TO_WORLD,
						body.getPosition().y * GameScreen.SCREEN_TO_WORLD, mapProperties);
				break;
			default:
				throw new IllegalStateException("Unknown spawn type: " + spawnConfig.spawnType);
		}
	}
	
	@Override
	public void remove() {
		super.remove();
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
