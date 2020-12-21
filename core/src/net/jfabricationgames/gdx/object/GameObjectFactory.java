package net.jfabricationgames.gdx.object;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.object.destroyable.DestroyableObject;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.object.interactive.InteractiveObject;
import net.jfabricationgames.gdx.object.interactive.LockedObject;
import net.jfabricationgames.gdx.object.spawn.SpawnPoint;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class GameObjectFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/object_factory.json";
	private static Config config;
	
	private Map<String, GameObjectTypeConfig> typeConfigs;
	
	public GameObjectFactory(GameMap gameMap) {
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
		typeConfigs = json.fromJson(HashMap.class, GameObjectTypeConfig.class, Gdx.files.internal(config.objectTypesConfig));
	}
	
	public GameObject createObject(String type, float x, float y, MapProperties properties) {
		GameObjectTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + configFile + "\")");
		}
		
		Sprite sprite = createSprite(x, y, typeConfig.texture);
		sprite.setScale(typeConfig.textureSizeFactorX * GameScreen.WORLD_TO_SCREEN, typeConfig.textureSizeFactorY * GameScreen.WORLD_TO_SCREEN);
		
		GameObject object;
		switch (typeConfig.type) {
			case DESTROYABLE:
				object = new DestroyableObject(typeConfig, sprite, properties);
				break;
			case INTERACTIVE:
				object = new InteractiveObject(typeConfig, sprite, properties);
				break;
			case LOCKED:
				object = new LockedObject(typeConfig, sprite, properties);
				break;
			case SPAWN_POINT:
				object = new SpawnPoint(typeConfig, sprite, properties);
				break;
			case EVENT_OBJECT:
				object = new EventObject(typeConfig, sprite, properties);
				break;
			default:
				throw new IllegalStateException("Unknown GameObjectType \"" + typeConfig.type + "\" of object type \"" + type + "\"");
		}
		object.setGameMap(gameMap);
		object.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		object.setTextureAtlas(atlas);
		
		return object;
	}
	
	public void createAndAddObjectAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			GameObject gameObject = createObject(type, x, y, mapProperties);
			gameMap.addObject(gameObject);
		});
	}
	
	public static class Config {
		
		public String objectAtlas;
		public String objectAnimations;
		public String objectTypesConfig;
	}
}
