package net.jfabricationgames.gdx.map;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyFactory;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class TiledMapLoader {
	
	public static final String OBJECT_NAME_PLAYER = "player";
	public static final String OBJECT_NAME_ITEM = "item";
	public static final String OBJECT_NAME_OBJECT = "object";
	public static final String OBJECT_NAME_ENEMY = "enemy";
	
	public static String mapPropertiesToString(MapProperties properties, boolean includePosition) {
		StringBuilder sb = new StringBuilder();
		
		Array<String> excludedKeys = new Array<>();
		if (!includePosition) {
			excludedKeys.addAll("x", "y", "width", "height");
		}
		
		sb.append('{');
		Iterator<String> keys = properties.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = properties.get(key).toString();
			if (!excludedKeys.contains(key, false)) {
				sb.append('\"').append(key).append('\"').append(": ").append('\"').append(value).append("\", ");
			}
		}
		sb.setLength(sb.length() - 2);
		sb.append('}');
		
		return sb.toString();
	}
	
	public static MapProperties createMapPropertiesFromString(String jsonConfig) {
		MapProperties mapProperties = new MapProperties();
		if (jsonConfig == null) {
			return mapProperties;
		}
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		ObjectMap<String, String> properties = json.fromJson(ObjectMap.class, String.class, jsonConfig);
		
		for (ObjectMap.Entry<String, String> property : properties) {
			mapProperties.put(property.key, property.value);
		}
		
		return mapProperties;
	}
	
	private String mapAsset;
	private GameMap gameMap;
	
	private ItemFactory itemFactory;
	private GameObjectFactory objectFactory;
	private EnemyFactory enemyFactory;
	
	public TiledMapLoader(String mapAsset, GameMap gameMap) {
		this.mapAsset = mapAsset;
		this.gameMap = gameMap;
		
		this.itemFactory = gameMap.itemFactory;
		this.objectFactory = gameMap.objectFactory;
		this.enemyFactory = gameMap.enemyFactory;
	}
	
	public void loadMap() {
		gameMap.map = AssetGroupManager.getInstance().get(mapAsset);
		loadMapObjects();
	}
	
	private void loadMapObjects() {
		Gdx.app.log(getClass().getSimpleName(), "--- Loading map objects --------------------------------------------------------------");
		Array<Item> items = new Array<>();
		Array<GameObject> objects = new Array<>();
		Array<Enemy> enemies = new Array<>();
		
		MapObjects mapObjects = gameMap.map.getLayers().get("objects").getObjects();
		
		if (mapObjects == null) {
			throw new IllegalStateException("The 'objects' layer couldn't be loaded.");
		}
		
		for (MapObject object : mapObjects) {
			String name = object.getName();
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle rectangle = rectangleObject.getRectangle();
			MapProperties properties = object.getProperties();
			
			if (name == null) {
				Gdx.app.error(getClass().getSimpleName(), "Unnamed object in tiled map at " + rectangle.x + "," + rectangle.y);
				continue;
			}
			
			if (isDebugObject(properties) && !GameScreen.DEBUG) {
				Gdx.app.debug(getClass().getSimpleName(),
						"Debug object will not be added, because the game is not in debug mode: " + mapPropertiesToString(properties, true));
				continue;
			}
			
			String[] parts = name.split("[.]");
			
			if (parts.length != 2) {
				throw new IllegalStateException("Object name couldn't be parsed (unexpected format): " + name);
			}
			
			Gdx.app.debug(getClass().getSimpleName(), "Processing map object: " + name + " at [x: " + rectangle.x + ", y: " + rectangle.y + ", w: "
					+ rectangle.width + ", h: " + rectangle.height + "] properties: " + mapPropertiesToString(properties, false));
			
			switch (parts[0]) {
				case OBJECT_NAME_PLAYER:
					if (parts[1].equals("startingPosition")) {
						gameMap.playerStartingPosition = new Vector2(rectangle.x, rectangle.y).scl(GameScreen.WORLD_TO_SCREEN);
					}
					break;
				case OBJECT_NAME_ITEM:
					items.add(itemFactory.createItem(parts[1], rectangle.x, rectangle.y, properties));
					break;
				case OBJECT_NAME_OBJECT:
					GameObject gameObject = objectFactory.createObject(parts[1], rectangle.x, rectangle.y, properties);
					objects.add(gameObject);
					gameObject.postAddToGameMap();
					break;
				case OBJECT_NAME_ENEMY:
					enemies.add(enemyFactory.createEnemy(parts[1], rectangle.x, rectangle.y, properties));
					break;
				default:
					throw new IllegalStateException("Unknown map object found: " + name + ". Properties: " + mapPropertiesToString(properties, true));
			}
		}
		
		gameMap.items = items;
		gameMap.objects = objects;
		gameMap.enemies = enemies;
	}
	
	private boolean isDebugObject(MapProperties properties) {
		return Boolean.parseBoolean(properties.get(GameObject.MAP_PROPERTY_KEY_DEBUG_OBJECT, "false", String.class));
	}
}
