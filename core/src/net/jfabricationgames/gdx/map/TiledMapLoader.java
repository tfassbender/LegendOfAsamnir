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

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.screens.GameScreen;

public class TiledMapLoader {
	
	private String mapAsset;
	private GameMap gameMap;
	
	private ItemFactory itemFactory;
	
	public TiledMapLoader(String mapAsset, GameMap gameMap) {
		this.mapAsset = mapAsset;
		this.gameMap = gameMap;
		itemFactory = new ItemFactory();
	}
	
	public void load() {
		gameMap.map = AssetGroupManager.getInstance().get(mapAsset);
		loadMapProperties();
	}
	
	private void loadMapProperties() {
		loadMapObjects();
		loadMapPhysics();
	}
	
	private void loadMapObjects() {
		Gdx.app.log(getClass().getSimpleName(), "--- Loading map objects --------------------------------------------------------------");
		Array<Item> items = new Array<>();
		
		MapObjects objects = gameMap.map.getLayers().get("objects").getObjects();
		
		if (objects == null) {
			throw new IllegalStateException("The 'objects' layer couldn't be loaded.");
		}
		
		for (MapObject object : objects) {
			String name = object.getName();
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle rectangle = rectangleObject.getRectangle();
			MapProperties properties = object.getProperties();
			
			if (name == null) {
				Gdx.app.error(getClass().getSimpleName(), "Unnamed object in tiled map at " + rectangle.x + "," + rectangle.y);
				continue;
			}
			
			String[] parts = name.split("[.]");
			
			if (parts.length != 2) {
				throw new IllegalStateException("Object name couldn't be parsed (unexpected format): " + name);
			}
			
			Gdx.app.log(getClass().getSimpleName(), "Processing map object: " + name + " at [x: " + rectangle.x + ", y: " + rectangle.y + ", w: "
					+ rectangle.width + ", h: " + rectangle.height + "] properties: " + mapPropertiesToString(properties, false));
			
			switch (parts[0]) {
				case "player":
					if (parts[1].equals("startingPosition")) {
						gameMap.playerStartingPosition = new Vector2(rectangle.x, rectangle.y).scl(GameScreen.WORLD_TO_SCREEN);
					}
					break;
				case "item":
					items.add(itemFactory.createItem(parts[1], rectangle.x, rectangle.y, properties));
					break;
			}
		}
		
		gameMap.items = items;
	}
	
	private void loadMapPhysics() {
		//TODO
	}
	
	private String mapPropertiesToString(MapProperties properties, boolean includePosition) {
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
}
