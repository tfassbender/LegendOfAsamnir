package net.jfabricationgames.gdx.item;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ItemFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/item_factory.json";
	private static Config config;
	
	private TextureAtlas atlas;
	private World world;
	
	private GameMap gameMap;
	
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
	}
	
	public Item createItem(String name, float x, float y, MapProperties properties) {
		ItemTypeConfig typeConfig = typeConfigs.get(name);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + name
					+ ". Either the type name is wrong or you have to add it to the itemTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = new Sprite(atlas.findRegion(typeConfig.textureName));
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		
		Item item = new Item(typeConfig, sprite, properties, gameMap);
		item.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return item;
	}
	
	public static class Config {
		
		public String itemAtlas;
		public String itemTypeConfig;
	}
}
