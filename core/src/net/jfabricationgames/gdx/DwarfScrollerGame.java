package net.jfabricationgames.gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputProfile;
import net.jfabricationgames.gdx.screens.GameScreen;

public class DwarfScrollerGame extends Game {
	
	private static DwarfScrollerGame instance;
	
	private InputMultiplexer multiplexer;
	
	private InputProfile gameInputProfile;
	
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
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		
		gameInputProfile = new InputProfile(Gdx.files.internal("config/input/profile.xml"), multiplexer);
		
		setScreen(new GameScreen());
	}
	
	public void addInputProcessor(InputProcessor processor) {
		multiplexer.addProcessor(processor);
	}
	public void removeInputProcessor(InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}
	
	public InputContext changeInputContext(String contextName) {
		gameInputProfile.setContext(contextName);
		return gameInputProfile.getContext();
	}
	public InputContext getInputContext() {
		return gameInputProfile.getContext();
	}
}
