package net.jfabricationgames.gdx.item;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ItemFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/item_factory.json";
	private static Config config;
	
	private Map<String, ItemTypeConfig> typeConfigs;
	
	public ItemFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		atlas = assetManager.get(config.itemAtlas);
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(HashMap.class, ItemTypeConfig.class, Gdx.files.internal(config.itemTypeConfig));
		Item.defaultTypeConfig = typeConfigs.get("__default");
		if (Item.defaultTypeConfig == null) {
			Gdx.app.error(getClass().getSimpleName(), "No default type config for items found. Add a type '__default' to 'config/items/types.json'.");
		}
	}
	
	public Item createItem(String name, float x, float y, MapProperties properties) {
		ItemTypeConfig typeConfig = typeConfigs.get(name);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + name
					+ ". Either the type name is wrong or you have to add it to the itemTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = createSprite(x, y, typeConfig.textureName);
		
		Item item = new Item(typeConfig, sprite, properties, gameMap);
		item.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return item;
	}
	
	public static class Config {
		
		public String itemAtlas;
		public String itemTypeConfig;
	}
}
