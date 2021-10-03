package net.jfabricationgames.gdx.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class ScreenManager {
	
	private static ScreenManager instance;
	
	public static synchronized ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}
	
	private Game game;
	
	private ScreenManager() {}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void setScreen(Screen screen) {
		Gdx.app.debug(getClass().getSimpleName(), "Changing screen to: " + screen);
		game.setScreen(screen);
	}
}
