package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.screens.menu.control.ControlledMenu;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public abstract class MenuScreen<T extends ControlledMenu<T>> extends ControlledMenu<T> {
	
	public static final String TEXT_COLOR_ENCODING_NORMAL = "[#000000]";
	public static final String TEXT_COLOR_ENCODING_FOCUS = "[#C8441B]";
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 837;//820 seems to be smaller here than in the GameScreen...
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final String FONT_NAME = "vikingMedium";
	public static final String SOUND_ERROR = "error";
	public static final String ANIMATION_CONFIG_FILE = "config/animation/menu.json";
	
	protected AssetGroupManager assetManager;
	protected ScreenTextWriter screenTextWriter;
	protected Viewport viewport;
	protected SpriteBatch batch;
	
	public MenuScreen(String... stateConfigFiles) {
		super(stateConfigFiles);
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		batch = new SpriteBatch();
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(getAssetGroupName());
		assetManager.finishLoading();
	}
	
	protected String getAssetGroupName() {
		return ASSET_GROUP_NAME;
	}
	
	@Override
	public void showMenu() {
		Game game = Game.getInstance();
		game.changeInputContext(getInputContextName());
		game.getInputContext().addListener(this);
		game.setScreen(this);
	}
	
	protected abstract String getInputContextName();
	
	protected void removeInputListener() {
		Game.getInstance().getInputContext().removeListener(this);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		assetManager.unloadGroup(getAssetGroupName());
		removeInputListener();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void quitGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Quit Game' selected");
		Gdx.app.exit();
	}
}
