package net.jfabricationgames.gdx.enemy;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.enemy.implementation.Bat;
import net.jfabricationgames.gdx.enemy.implementation.Gladiator;
import net.jfabricationgames.gdx.enemy.implementation.MiniGolem;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class EnemyFactory extends AbstractFactory {
	
	private static final String configFile = "config/factory/enemy_factory.json";
	private static Config config;
	
	private Map<String, EnemyTypeConfig> typeConfigs;
	
	public EnemyFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, configFile);
		}
		
		loadTypeConfigs();
		loadEnemyAnimations();
		
		world = PhysicsWorld.getInstance().getWorld();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(HashMap.class, EnemyTypeConfig.class, Gdx.files.internal(config.enemyTypesConfig));
	}
	
	private void loadEnemyAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (EnemyTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public Enemy createEnemy(String type, float x, float y, MapProperties properties) {
		EnemyTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: " + type
					+ ". Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + configFile + "\")");
		}
		
		Enemy enemy;
		switch (type) {
			case "bat":
				enemy = new Bat(typeConfig, properties);
				break;
			case "gladiator":
				enemy = new Gladiator(typeConfig, properties);
				break;
			case "mini_golem":
				enemy = new MiniGolem(typeConfig, properties);
				break;
			default:
				throw new IllegalStateException("Unknown enemy type: " + type);
		}
		enemy.gameMap = gameMap;
		enemy.createPhysicsBody(world, x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return enemy;
	}
	
	public static class Config {
		
		public String enemyAtlas;
		public String enemyTypesConfig;
	}
}
