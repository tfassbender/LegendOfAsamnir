package net.jfabricationgames.gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputProfile;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.MainMenuScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.text.FontManager;

public class DwarfScrollerGame extends Game {
	
	public static final String ASSET_GROUP_MANAGER_CONFIG_PATH = "config/assets/asset_groups.json";
	public static final String INPUT_PROFILE_CONFIG_PATH = "config/input/profile.xml";
	public static final String SOUND_CONFIG_PATH = "config/sound/sound_sets.json";
	public static final String FONT_CONFIG_PATH = "config/font/fonts.json";
	
	private static DwarfScrollerGame instance;
	
	private Runnable preGameConfigurator;
	
	private InputMultiplexer multiplexer;
	private InputProfile gameInputProfile;

	public static synchronized DwarfScrollerGame createInstance(Runnable preGameConfigurator) {
		if (instance == null) {
			instance = new DwarfScrollerGame(preGameConfigurator);
		}
		return instance;
	}
	
	public static DwarfScrollerGame getInstance() {
		return instance;
	}
	
	private DwarfScrollerGame(Runnable preGameConfigurator) {
		this.preGameConfigurator = preGameConfigurator;
	}
	
	@Override
	public void create() {
		preGameConfigurator.run();
		
		AssetGroupManager.initialize(ASSET_GROUP_MANAGER_CONFIG_PATH);
		SoundManager.getInstance().loadConfig(SOUND_CONFIG_PATH);
		FontManager.getInstance().load(FONT_CONFIG_PATH);
		
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		gameInputProfile = new InputProfile(Gdx.files.internal(INPUT_PROFILE_CONFIG_PATH), multiplexer);
		
		setScreen(new MainMenuScreen());
	}
	
	public void setScreen(Screen screen) {
		Gdx.app.debug(getClass().getSimpleName(), "Changing screen to: " + screen);
		super.setScreen(screen);
	}
	
	public GameScreen getGameScreen() {
		if (screen instanceof GameScreen) {
			return (GameScreen) screen;
		}
		return null;
	}
	
	public void addInputProcessor(InputProcessor processor) {
		Gdx.app.debug(getClass().getSimpleName(), "Adding InputProcessor: " + processor);
		multiplexer.addProcessor(processor);
	}
	public void removeInputProcessor(InputProcessor processor) {
		Gdx.app.debug(getClass().getSimpleName(), "Removing InputProcessor: " + processor);
		multiplexer.removeProcessor(processor);
	}
	
	public InputContext changeInputContext(String contextName) {
		Gdx.app.debug(getClass().getSimpleName(), "Changing InputContext to \"" + contextName + "\"");
		gameInputProfile.setContext(contextName);
		return gameInputProfile.getContext();
	}
	public InputContext getInputContext() {
		return gameInputProfile.getContext();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		AssetGroupManager.getInstance().dispose();
		SoundManager.getInstance().dispose();
		FontManager.getInstance().dispose();
	}
}
