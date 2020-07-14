package net.jfabricationgames.gdx;

import com.badlogic.gdx.Game;

import net.jfabricationgames.gdx.screens.GameScreen;

public class DwarfScrollerGame extends Game {

	private static DwarfScrollerGame instance;
	
	public static synchronized DwarfScrollerGame getInstance() {
		if (instance == null) {
			instance = new DwarfScrollerGame();
		}
		return instance;
	}
	
	private DwarfScrollerGame() {
		
	}
	
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}