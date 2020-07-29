package net.jfabricationgames.gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputProfile;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.text.FontManager;

public class DwarfScrollerGame extends Game {
	
	public static final String INPUT_PROFILE_CONFIG_PATH = "config/input/profile.xml";
	public static final String FONT_CONFIG_PATH = "font/config.json";
	
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
		
		gameInputProfile = new InputProfile(Gdx.files.internal(INPUT_PROFILE_CONFIG_PATH), multiplexer);
		
		SoundManager.getInstance().loadConfig();
		FontManager.getInstance().load(FONT_CONFIG_PATH);
		
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
	
	@Override
	public void dispose() {
		super.dispose();
		SoundManager.getInstance().dispose();
		FontManager.getInstance().dispose();
	}
}
