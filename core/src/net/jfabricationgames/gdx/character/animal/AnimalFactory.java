package net.jfabricationgames.gdx.character.animal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class AnimalFactory extends AbstractFactory {
	
	private static final String CONFIG_FILE = "config/factory/animal_factory.json";
	private static Config config;
	
	private ObjectMap<String, AnimalTypeConfig> typeConfigs;
	
	public AnimalFactory() {
		if (config == null) {
			config = loadConfig(Config.class, CONFIG_FILE);
		}
		
		loadTypeConfigs();
		loadEnemyAnimations();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		typeConfigs = json.fromJson(ObjectMap.class, AnimalTypeConfig.class, Gdx.files.internal(config.animalTypesConfig));
	}
	
	private void loadEnemyAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (AnimalTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public Animal createAnimal(String type, float x, float y, MapProperties properties) {
		AnimalTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Animal animal = new Animal(typeConfig, properties);
		animal.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return animal;
	}
	
	private static class Config {
		
		public String animalTypesConfig;
	}
}
