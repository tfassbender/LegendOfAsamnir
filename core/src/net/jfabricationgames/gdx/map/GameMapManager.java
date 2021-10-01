package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class GameMapManager {
	
	public static final String GAME_MAPS_CONFIG_FILE_PATH = "config/map/maps.json";
	
	private static GameMapManager instance;
	
	public static synchronized GameMapManager getInstance() {
		if (instance == null) {
			instance = new GameMapManager();
		}
		return instance;
	}
	
	private Array<GameMapConfig> mapFiles;
	
	private GameMapManager() {
		loadMapsConfig();
	}
	
	@SuppressWarnings("unchecked")
	private void loadMapsConfig() {
		Json json = new Json();
		mapFiles = json.fromJson(Array.class, GameMapConfig.class, Gdx.files.internal(GAME_MAPS_CONFIG_FILE_PATH));
	}
	
	public void showMap(String mapIdentifier) {
		GameMap.getInstance().beforeLoadMap(mapIdentifier);
		
		String mapAsset = GameMapManager.getInstance().getMapFilePath(mapIdentifier);
		new TiledMapLoader(mapAsset).loadMap();
		
		GameMap.getInstance().afterLoadMap(mapIdentifier);
	}
	
	public String getMapFilePath(String mapName) {
		for (GameMapConfig config : mapFiles) {
			if (mapName.equals(config.name)) {
				return config.map;
			}
		}
		throw new IllegalStateException("A map with the name '" + mapName + "' is not found in the config file.");
	}
	
	public String getInitialMapIdentifier() {
		for (GameMapConfig config : mapFiles) {
			if (config.initial) {
				return config.name;
			}
		}
		throw new IllegalStateException("The configuration file '" + GAME_MAPS_CONFIG_FILE_PATH + "' does not configure an initial map.");
	}
}
