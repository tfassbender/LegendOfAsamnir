package net.jfabricationgames.gdx.object;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;

public class ObjectFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/object_factory.json";
	private static Config config;
	
	private TextureAtlas atlas;
	private World world;
	
	private GameMap gameMap;
	
	private Map<String, ObjectTypeConfig> typeConfigs;
	
	public ObjectFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		AnimationManager.getInstance().loadAnimations(config.objectAnimations);
		atlas = assetManager.get(config.objectAtlas);
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(HashMap.class, ObjectTypeConfig.class, Gdx.files.internal(config.objectTypesConfig));
	}
	
	public GameObject createObject(String type, float x, float y, MapProperties properties) {
		ObjectTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + type
					+ ". Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = new Sprite(atlas.findRegion(typeConfig.textureName));
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		
		GameObject object;
		switch (type) {
			case "barrel":
				object = new Barrel(typeConfig, sprite, properties);
				break;
			case "box":
				object = new Box(typeConfig, sprite, properties);
				break;
			case "chest":
				object = new Chest(typeConfig, sprite, properties);
				break;
			case "pot":
				object = new Pot(typeConfig, sprite, properties);
				break;
			default:
				throw new IllegalStateException("Unknown object type: " + type);
		}
		object.setGameMap(gameMap);
		object.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return object;
	}
	
	public static class Config {
		
		public String objectAtlas;
		public String objectAnimations;
		public String objectTypesConfig;
	}
}
