package net.jfabricationgames.gdx.map.implementation;

import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapLoader;

public class GameMapLoaderFactory {
	
	public static GameMapLoader createGameMapLoader(GameMap gameMap, String mapAsset) {
		return new TiledMapLoader(gameMap, mapAsset);
	}
}
