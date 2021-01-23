package net.jfabricationgames.gdx.character.enemy;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.enemy.implementation.Bat;
import net.jfabricationgames.gdx.character.enemy.implementation.Cobra;
import net.jfabricationgames.gdx.character.enemy.implementation.Elemental;
import net.jfabricationgames.gdx.character.enemy.implementation.Gladiator;
import net.jfabricationgames.gdx.character.enemy.implementation.Imp;
import net.jfabricationgames.gdx.character.enemy.implementation.Mimic;
import net.jfabricationgames.gdx.character.enemy.implementation.MiniGolem;
import net.jfabricationgames.gdx.character.enemy.implementation.Minotaur;
import net.jfabricationgames.gdx.character.enemy.implementation.Spider;
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
	
	private Map<String, EnemyTypeConfig> typeConfigs;
	
	public EnemyFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, CONFIG_FILE);
		}
		
		loadTypeConfigs();
		loadEnemyAnimations();
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
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Enemy enemy;
		switch (type) {
			case ENEMY_NAME_BAT:
				enemy = new Bat(typeConfig, properties);
				break;
			case ENEMY_NAME_GLADIATOR:
				enemy = new Gladiator(typeConfig, properties);
				break;
			case ENEMY_NAME_MINI_GOLEM:
				enemy = new MiniGolem(typeConfig, properties);
				break;
			case ENEMY_NAME_MINOTAUR:
				enemy = new Minotaur(typeConfig, properties);
				break;
			case ENEMY_NAME_SPIDER:
				enemy = new Spider(typeConfig, properties);
				break;
			case ENEMY_NAME_MIMIC_CHEST:
			case ENEMY_NAME_MIMIC_BARREL:
				enemy = new Mimic(typeConfig, properties);
				break;
			case ENEMY_NAME_COBRA:
				enemy = new Cobra(typeConfig, properties);
				break;
			case ENEMY_NAME_FIRE_ELEMENTAL:
			case ENEMY_NAME_ICE_ELEMENTAL:
				enemy = new Elemental(typeConfig, properties);
				break;
			case ENEMY_NAME_IMP:
				enemy = new Imp(typeConfig, properties);
				break;
			default:
				throw new IllegalStateException("Unknown enemy type: " + type);
		}
		enemy.setGameMap(gameMap);
		enemy.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return enemy;
	}
	
	public void createAndAddEnemyAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			Enemy gameObject = createEnemy(type, x, y, mapProperties);
			gameMap.addEnemy(gameObject);
		});
	}
	
	public static class Config {
		
		public String enemyAtlas;
		public String enemyTypesConfig;
	}
}
