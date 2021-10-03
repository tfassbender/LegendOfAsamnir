package net.jfabricationgames.gdx.map.implementation;

import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jfabricationgames.gdx.map.GameMap;

public class GameMapFactory {
	
	public static GameMap createGameMap(OrthographicCamera camera) {
		return new GameMapImplementation(camera);
	}
}
