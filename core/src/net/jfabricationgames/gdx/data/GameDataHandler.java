package net.jfabricationgames.gdx.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.data.container.CharacterDataContainer;
import net.jfabricationgames.gdx.data.container.CharacterItemContainer;
import net.jfabricationgames.gdx.data.container.FastTravelContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.container.GlobalValuesContainer;
import net.jfabricationgames.gdx.data.container.MapDataContainer;
import net.jfabricationgames.gdx.data.container.MapObjectDataContainer;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterKeyDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.DataHandler;
import net.jfabricationgames.gdx.data.handler.FastTravelDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.handler.MapDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;

public class GameDataHandler implements DataHandler {
	
	private static GameDataHandler instance;
	
	public static synchronized GameDataHandler getInstance() {
		if (instance == null) {
			instance = new GameDataHandler();
		}
		return instance;
	}
	
	private GameDataContainer gameDataContainer;
	
	private GameDataHandler() {}
	
	@Override
	public void updateData(GameDataContainer gameDataContainer) {
		this.gameDataContainer = gameDataContainer;
		CharacterItemDataHandler.getInstance().updateData(gameDataContainer);
		CharacterKeyDataHandler.getInstance().updateData(gameDataContainer);
		CharacterPropertiesDataHandler.getInstance().updateData(gameDataContainer);
		FastTravelDataHandler.getInstance().updateData(gameDataContainer);
		GlobalValuesDataHandler.getInstance().updateData(gameDataContainer);
		MapDataHandler.getInstance().updateData(gameDataContainer);
		MapObjectDataHandler.getInstance().updateData(gameDataContainer);
	}
	
	public void createNewGameData() {
		Gdx.app.log(getClass().getSimpleName(), "creating new GameDataContainer object");
		gameDataContainer = new GameDataContainer();
		gameDataContainer.characterDataContainer = new CharacterDataContainer();
		gameDataContainer.itemDataContainer = new CharacterItemContainer();
		gameDataContainer.fastTravelDataContainer = new FastTravelContainer();
		gameDataContainer.globalValuesDataContainer = new GlobalValuesContainer();
		gameDataContainer.mapDataContainer = new MapDataContainer();
		gameDataContainer.mapObjectDataContainer = new MapObjectDataContainer();
		updateData(gameDataContainer);
	}
	
	protected GameDataContainer getGameData() {
		return gameDataContainer;
	}
	
	public String getCurrentMapIdentifier() {
		return gameDataContainer.mapDataContainer.mapIdentifier;
	}
	
	public Vector2 getPlayerPosition() {
		return gameDataContainer.characterDataContainer.position;
	}
}
