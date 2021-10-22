package net.jfabricationgames.gdx.map.implementation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.animal.AnimalFactory;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyFactory;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacterFactory;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapLoader;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.util.MapUtil;

class TiledMapLoader implements GameMapLoader {
	
	private String mapAsset;
	private GameMapImplementation gameMap;
	
	protected TiledMapLoader(GameMap gameMap, String mapAsset) {
		if (!(gameMap instanceof GameMapImplementation)) {
			throw new IllegalArgumentException("This GameMapLoader implementation (" + getClass().getSimpleName() + ") needs a "
					+ GameMapImplementation.class.getName() + " to load.");
		}
		this.mapAsset = mapAsset;
		this.gameMap = (GameMapImplementation) gameMap;
	}
	
	@Override
	public void loadMap() {
		gameMap.map = AssetGroupManager.getInstance().get(mapAsset);
		loadMapObjects();
	}
	
	private void loadMapObjects() {
		Gdx.app.debug(getClass().getSimpleName(), "--- Loading map objects --------------------------------------------------------------");
		Array<Item> items = new Array<>();
		Array<GameObject> objects = new Array<>();
		Array<Enemy> enemies = new Array<>();
		Array<NonPlayableCharacter> npcs = new Array<>();
		Array<Animal> animals = new Array<>();
		
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
			
			if (isDebugObject(properties) && !Constants.DEBUG) {
				Gdx.app.debug(getClass().getSimpleName(),
						"Debug object will not be added, because the game is not in debug mode: " + MapUtil.mapPropertiesToString(properties, true));
				continue;
			}
			
			String[] parts = name.split("[.]");
			
			Gdx.app.debug(getClass().getSimpleName(), "Processing map object: " + name + " at [x: " + rectangle.x + ", y: " + rectangle.y + ", w: "
					+ rectangle.width + ", h: " + rectangle.height + "] properties: " + MapUtil.mapPropertiesToString(properties, false));
			
			switch (parts[0]) {
				case Constants.OBJECT_NAME_PLAYER:
					if (parts[1].equals("startingPosition")) {
						gameMap.playerStartingPosition = new Vector2(rectangle.x, rectangle.y).scl(Constants.WORLD_TO_SCREEN);
					}
					break;
				case Constants.OBJECT_NAME_ITEM:
					items.add(ItemFactory.createItem(parts[1], rectangle.x, rectangle.y, properties));
					break;
				case Constants.OBJECT_NAME_OBJECT:
					GameObject gameObject = GameObjectFactory.createObject(parts[1], rectangle.x, rectangle.y, properties);
					objects.add(gameObject);
					gameObject.postAddToGameMap();
					break;
				case Constants.OBJECT_NAME_ENEMY:
					enemies.add(EnemyFactory.createEnemy(parts[1], rectangle.x, rectangle.y, properties));
					break;
				case Constants.OBJECT_NAME_NPC:
					npcs.add(NonPlayableCharacterFactory.createNonPlayableCharacter(parts[1], rectangle.x, rectangle.y, properties));
					break;
				case Constants.OBJECT_NAME_ANIMAL:
					animals.add(AnimalFactory.createAnimal(parts[1], rectangle.x, rectangle.y, properties));
					break;
				default:
					throw new IllegalStateException(
							"Unknown map object found: " + name + ". Properties: " + MapUtil.mapPropertiesToString(properties, true));
			}
		}
		
		gameMap.items = items;
		gameMap.objects = objects;
		gameMap.enemies = enemies;
		gameMap.nonPlayableCharacters = npcs;
		gameMap.animals = animals;
	}
	
	private boolean isDebugObject(MapProperties properties) {
		return Boolean.parseBoolean(properties.get(GameObject.MAP_PROPERTY_KEY_DEBUG_OBJECT, "false", String.class));
	}
}
