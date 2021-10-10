package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.enemy.implementation.Bat;
import net.jfabricationgames.gdx.character.enemy.implementation.Cyclops;
import net.jfabricationgames.gdx.character.enemy.implementation.Minotaur;
import net.jfabricationgames.gdx.character.enemy.implementation.Totem;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.object.spawn.EnemySpawnFactory;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class EnemyFactory {
	
	private EnemyFactory() {}
	
	private static final String ENEMY_NAME_BAT = "bat";
	private static final String ENEMY_NAME_GLADIATOR = "gladiator";
	private static final String ENEMY_NAME_MINI_GOLEM = "mini_golem";
	private static final String ENEMY_NAME_SPIDER = "spider";
	private static final String ENEMY_NAME_MIMIC_CHEST = "mimic_chest";
	private static final String ENEMY_NAME_MIMIC_BARREL = "mimic_barrel";
	private static final String ENEMY_NAME_COBRA = "cobra";
	private static final String ENEMY_NAME_FIRE_ELEMENTAL = "fire_elemental";
	private static final String ENEMY_NAME_ICE_ELEMENTAL = "ice_elemental";
	private static final String ENEMY_NAME_IMP = "imp";
	private static final String ENEMY_NAME_MINOTAUR = "minotaur";
	private static final String ENEMY_NAME_CYCLOPS = "cyclops";
	private static final String ENEMY_NAME_TOTEM = "totem";
	
	private static final String CONFIG_FILE = "config/factory/enemy_factory.json";
	
	private static Config config;
	private static ObjectMap<String, EnemyTypeConfig> typeConfigs;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.enemyTypesConfig, EnemyTypeConfig.class);
		loadEnemyAnimations();
	}
	
	private static void loadEnemyAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (EnemyTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public static Enemy createEnemy(String type, float x, float y, MapProperties properties) {
		EnemyTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Enemy enemy;
		switch (type) {
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
			case ENEMY_NAME_BAT:
				enemy = new Bat(typeConfig, properties);
				break;
			case ENEMY_NAME_TOTEM:
				enemy = new Totem(typeConfig, properties);
				break;
			case ENEMY_NAME_MINOTAUR:
				enemy = new Minotaur(typeConfig, properties);
				break;
			case ENEMY_NAME_CYCLOPS:
				enemy = new Cyclops(typeConfig, properties);
				break;
			default:
				throw new IllegalStateException("Unknown enemy type: " + type);
		}
		enemy.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		
		return enemy;
	}
	
	public static EnemyFactoryInstance asInstance() {
		return new EnemyFactoryInstance();
	}
	
	public static class EnemyFactoryInstance implements EnemySpawnFactory {
		
		@Override
		public void createAndAddEnemy(String type, float x, float y, MapProperties mapProperties) {
			Enemy enemy = createEnemy(type, x, y, mapProperties);
			GameMapManager.getInstance().getMap().addEnemy(enemy);
		}
	}
	
	private static class Config {
		
		public String enemyTypesConfig;
	}
}
