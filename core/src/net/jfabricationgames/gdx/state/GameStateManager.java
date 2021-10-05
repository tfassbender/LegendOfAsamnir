package net.jfabricationgames.gdx.state;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class GameStateManager {
	
	private static GameStateManager instance;
	
	public static synchronized GameStateManager getInstance() {
		if (instance == null) {
			instance = new GameStateManager();
		}
		return instance;
	}
	
	private boolean gameOver;
	
	private GameStateManager() {}
	
	public boolean isGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public static void fireQuickSaveEvent() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.QUICKSAVE));
	}
}
