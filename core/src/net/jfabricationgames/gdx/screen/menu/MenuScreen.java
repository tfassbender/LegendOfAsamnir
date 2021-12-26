package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.menu.control.ControlledMenu;
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
	protected SpriteBatch batch;
	
	protected OrthographicCamera camera;
	private Viewport viewport;
	
	public MenuScreen(String... stateConfigFiles) {
		super(stateConfigFiles);
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		camera = new OrthographicCamera();
		camera.position.x = VIRTUAL_WIDTH / 2;
		camera.position.y = VIRTUAL_HEIGHT / 2;
		
		//adapt the camera position to center the menu on the screen (instead of changing all components)
		camera.position.x -= 40;
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
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
		setInputContext();
		ScreenManager.getInstance().setScreen(this);
	}
	
	protected void setInputContext() {
		InputContext inputContext = InputManager.getInstance().changeInputContext(getInputContextName());
		inputContext.addListener(this);
	}
	
	protected abstract String getInputContextName();
	
	protected void removeInputListener() {
		InputManager.getInstance().getInputContext().removeListener(this);
	}
	
	protected void setProjectionMatrixBeforeRendering() {
		batch.setProjectionMatrix(camera.combined);
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
	}
	
	@Override
	public void resize(int width, int height) {
		Gdx.app.log(getClass().getSimpleName(), "Resizing screen to: " + width + " x " + height);
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		//do not unload the asset group, because the menu will still be needed (at least for loading screens)
		
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
