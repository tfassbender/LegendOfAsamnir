package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.util.FactoryUtil;

public class NonPlayableCharacterFactory {
	
	private NonPlayableCharacterFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/npc_factory.json";
	
	private static Config config;
	private static ObjectMap<String, NonPlayableCharacterTypeConfig> typeConfigs;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		loadTypeConfigs();
		loadAnimations();
	}
	
	@SuppressWarnings("unchecked")
	private static void loadTypeConfigs() {
		Json json = new Json();
		ObjectMap<String, String> typeConfigFiles = json.fromJson(ObjectMap.class, String.class, Gdx.files.internal(config.npcTypes));
		typeConfigs = new ObjectMap<>();
		
		for (Entry<String, String> config : typeConfigFiles.entries()) {
			NonPlayableCharacterTypeConfig typeConfig = json.fromJson(NonPlayableCharacterTypeConfig.class, Gdx.files.internal(config.value));
			if (typeConfig.graphicsConfigFile != null) {
				typeConfig.graphicsConfig = json.fromJson(NonPlayableCharacterGraphicsConfig.class,
						Gdx.files.internal(typeConfig.graphicsConfigFile));
			}
			
			typeConfigs.put(config.key, typeConfig);
		}
	}
	
	private static void loadAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (NonPlayableCharacterTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.graphicsConfig.animationsConfig);
		}
	}
	
	public static NonPlayableCharacter createNonPlayableCharacter(String type, float x, float y, MapProperties properties) {
		NonPlayableCharacterTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		NonPlayableCharacter npc = new NonPlayableCharacter(typeConfig, properties);
		npc.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return npc;
	}
	
	private static class Config {
		
		public String npcTypes;
	}
}
