package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.factory.AbstractFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class NonPlayableCharacterFactory extends AbstractFactory {
	
	private static final String CONFIG_FILE = "config/factory/npc_factory.json";
	private static Config config;
	
	private ObjectMap<String, NonPlayableCharacterTypeConfig> typeConfigs;
	
	public NonPlayableCharacterFactory(GameMap gameMap) {
		this.gameMap = gameMap;
		
		if (config == null) {
			config = loadConfig(Config.class, CONFIG_FILE);
		}
		
		loadTypeConfigs();
		loadAnimations();
	}
	
	@SuppressWarnings("unchecked")
	private void loadTypeConfigs() {
		Json json = new Json();
		ObjectMap<String, String> typeConfigFiles = json.fromJson(ObjectMap.class, String.class, Gdx.files.internal(config.npcTypes));
		typeConfigs = new ObjectMap<>();
		
		for (Entry<String, String> config : typeConfigFiles.entries()) {
			NonPlayableCharacterTypeConfig typeConfig = json.fromJson(NonPlayableCharacterTypeConfig.class, Gdx.files.internal(config.value));
			typeConfigs.put(config.key, typeConfig);
		}
	}
	
	private void loadAnimations() {
		AnimationManager animationManager = AnimationManager.getInstance();
		for (NonPlayableCharacterTypeConfig config : typeConfigs.values()) {
			animationManager.loadAnimations(config.animationsConfig);
		}
	}
	
	public NonPlayableCharacter createNpc(String type, float x, float y, MapProperties properties) {
		NonPlayableCharacterTypeConfig typeConfig = typeConfigs.get(type);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + type
					+ "'. Either the type name is wrong or you have to add it to the objectTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		NonPlayableCharacter npc = new NonPlayableCharacter(properties);
		npc.setGameMap(gameMap);
		npc.createPhysicsBody(x * GameScreen.WORLD_TO_SCREEN, y * GameScreen.WORLD_TO_SCREEN);
		
		return npc;
	}
	
	public void createAndAddEnemyAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			NonPlayableCharacter gameObject = createNpc(type, x, y, mapProperties);
			gameMap.addNpc(gameObject);
		});
	}
	
	private static class Config {
		
		public String npcTypes;
	}
}
