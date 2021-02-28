package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.enemy.implementation.Minotaur;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class EnemyFactory extends AbstractFactory {
	
	private static final String ENEMY_NAME_BAT = "bat";
	private static final String ENEMY_NAME_GLADIATOR = "gladiator";
	private static final String ENEMY_NAME_MINI_GOLEM = "mini_golem";
	private static final String ENEMY_NAME_MINOTAUR = "minotaur";
	private static final String ENEMY_NAME_SPIDER = "spider";
	private static final String ENEMY_NAME_MIMIC_CHEST = "mimic_chest";
	private static final String ENEMY_NAME_MIMIC_BARREL = "mimic_barrel";
	private static final String ENEMY_NAME_COBRA = "cobra";
	private static final String ENEMY_NAME_FIRE_ELEMENTAL = "fire_elemental";
	private static final String ENEMY_NAME_ICE_ELEMENTAL = "ice_elemental";
	private static final String ENEMY_NAME_IMP = "imp";
	
	private static final String CONFIG_FILE = "config/factory/enemy_factory.json";
	private static Config config;
	
	private ObjectMap<String, EnemyTypeConfig> typeConfigs;
	
	public EnemyFactory() {
		if (config == null) {
			config = loadConfig(Config.class, CONFIG_FILE);
		}
		
		loadTypeConfigs();
		loadEnemyAnimations();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(ObjectMap.class, EnemyTypeConfig.class, Gdx.files.internal(config.enemyTypesConfig));
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
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Enemy enemy;
		switch (type) {
			case ENEMY_NAME_BAT:
			case ENEMY_NAME_GLADIATOR:
			case ENEMY_NAME_MINI_GOLEM:
			case ENEMY_NAME_SPIDER:
			case ENEMY_NAME_MIMIC_CHEST:
			case ENEMY_NAME_MIMIC_BARREL:
			case ENEMY_NAME_COBRA:
			case ENEMY_NAME_FIRE_ELEMENTAL:
			case ENEMY_NAME_ICE_ELEMENTAL:
			case ENEMY_NAME_IMP:
				enemy = new Enemy(typeConfig, properties);
				break;
			case ENEMY_NAME_MINOTAUR:
				enemy = new Minotaur(typeConfig, properties);
				break;
			default:
				throw new IllegalStateException("Unknown enemy type: " + type);
		}
		enemy.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return enemy;
	}
	
	public void createAndAddEnemyAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			Enemy gameObject = createEnemy(type, x, y, mapProperties);
			GameMap.getInstance().addEnemy(gameObject);
		});
	}
	
	private static class Config {
		
		public String enemyTypesConfig;
	}
}
