package net.jfabricationgames.gdx.state;

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
}
