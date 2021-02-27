package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class ItemFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/item_factory.json";
	private static Config config;
	
	private ObjectMap<String, ItemTypeConfig> typeConfigs;
	private ObjectMap<String, ObjectMap<String, Object>> defaultValues;
	
	public ItemFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		loadDefaultValues();
		
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		AnimationManager.getInstance().loadAnimations(config.itemAnimations);
		atlas = assetManager.get(config.itemAtlas);
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(ObjectMap.class, ItemTypeConfig.class, Gdx.files.internal(config.itemTypeConfig));
		Item.defaultTypeConfig = typeConfigs.get("__default");
		if (Item.defaultTypeConfig == null) {
			Gdx.app.error(getClass().getSimpleName(), "No default type config for items found. Add a type '__default' to 'config/items/types.json'.");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadDefaultValues() {
		defaultValues = json.fromJson(ObjectMap.class, ObjectMap.class, Gdx.files.internal(config.defaultValuesConfig));
	}
	
	public void createAndDropItem(String type, float x, float y, boolean renderAboveGameObjects, float addBodyDelay) {
		createAndDropItem(type, new MapProperties(), x, y, renderAboveGameObjects, addBodyDelay);
	}
	public void createAndDropItem(String type, MapProperties mapProperties, float x, float y, boolean renderAboveGameObjects, float addBodyDelay) {
		Item item = createItem(type, x, y, mapProperties, addBodyDelay);
		if (renderAboveGameObjects) {
			gameMap.addItemAboveGameObjects(item);
		}
		else {
			gameMap.addItem(item);
		}
		
		item.setPosition(new Vector2(x, y));
		MapObjectDataHandler.getInstance().addStatefulMapObject(item);
	}
	
	public void createAndAddItemAfterWorldStep(String type, float x, float y, boolean renderAboveGameObjects) {
		createAndAddItemAfterWorldStep(type, x, y, new MapProperties(), renderAboveGameObjects);
	}
	public void createAndAddItemAfterWorldStep(String type, float x, float y, MapProperties mapProperties, boolean renderAboveGameObjects) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			Item item = createItem(type, x, y, mapProperties);
			if (renderAboveGameObjects) {
				gameMap.addItemAboveGameObjects(item);
			}
			else {
				gameMap.addItem(item);
			}
		});
	}
	
	public Item createItem(String name, float x, float y, MapProperties properties) {
		return createItem(name, x, y, properties, 0);
	}
	
	private Item createItem(String name, float x, float y, MapProperties properties, float addBodyDelay) {
		ItemTypeConfig typeConfig = typeConfigs.get(name);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + name
					+ "'. Either the type name is wrong or you have to add it to the itemTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = createSprite(x, y, typeConfig.texture);
		AnimationDirector<TextureRegion> animation = createAnimation(x, y, typeConfig.animation);
		
		addDefaultProperties(name, properties);
		Item item;
		
		switch (typeConfig.type) {
			case ITEM:
				item = new Item(name, typeConfig, sprite, animation, properties, gameMap);
				break;
			case EVENT_ITEM:
				item = new EventItem(name, typeConfig, sprite, animation, properties, gameMap);
				break;
			case BUYABLE_ITEM:
				item = new BuyableItem(name, typeConfig, sprite, animation, properties, gameMap);
				break;
			default:
				throw new IllegalStateException("Unknown ItemType \"" + typeConfig.type + "\" of object type \"" + name + "\"");
		}
		
		if (addBodyDelay > 0) {
			PhysicsWorld.getInstance().runDelayedAfterWorldStep(
					() -> item.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN), addBodyDelay);
		}
		else {
			item.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		}
		
		return item;
	}
	
	private void addDefaultProperties(String name, MapProperties properties) {
		if (defaultValues.containsKey(name)) {
			for (Entry<String, Object> entry : defaultValues.get(name).entries()) {
				if (!properties.containsKey(entry.key)) {
					properties.put(entry.key, entry.value);
				}
			}
		}
	}
	
	public static class Config {
		
		public String itemAtlas;
		public String itemAnimations;
		public String itemTypeConfig;
		public String defaultValuesConfig;
	}
}
