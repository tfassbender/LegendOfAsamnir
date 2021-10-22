package net.jfabricationgames.gdx.character.animal;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class AnimalFactory {
	
	private AnimalFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/animal_factory.json";
	
	private static Config config;
	private static ObjectMap<String, AnimalTypeConfig> typeConfigs;
	
	private static AnimalCharacterMap gameMap;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.animalTypesConfig, AnimalTypeConfig.class);
		loadAnimations();
	}
	
	private static void loadAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (AnimalTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public static void setGameMap(AnimalCharacterMap gameMap) {
		AnimalFactory.gameMap = gameMap;
	}
	
	public static Animal createAnimal(String type, float x, float y, MapProperties properties) {
		AnimalTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Animal animal = new Animal(typeConfig, properties);
		animal.setGameMap(gameMap);
		animal.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		
		return animal;
	}
	
	private static class Config {
		
		public String animalTypesConfig;
	}
}
