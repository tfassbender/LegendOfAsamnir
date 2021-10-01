package net.jfabricationgames.gdx.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.camera.CameraMovementHandler;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.FastTravelDataHandler;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.HeadsUpDisplay;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.interaction.InteractionManager;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.menu.GameOverMenuScreen;
import net.jfabricationgames.gdx.screens.menu.LoadingScreen;
import net.jfabricationgames.gdx.screens.menu.PauseMenuScreen;
import net.jfabricationgames.gdx.screens.menu.ShopMenuScreen;

public class GameScreen extends ScreenAdapter implements InputActionListener, EventListener {
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	
	private static final String ACTION_SHOW_MENU = "menu";
	
	public static void loadAndShowGameScreen(Runnable afterCreatingGameScreen) {
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		showLoadingScreen(afterCreatingGameScreen);
		assetManager.loadGroup(ASSET_GROUP_NAME);
	}
	
	private static void showLoadingScreen(Runnable afterCreatingGameScreen) {
		new LoadingScreen(() -> {
			Game.getInstance().setScreen(new GameScreen());
			afterCreatingGameScreen.run();
		}).showMenu();
	}
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	private CameraMovementHandler cameraMovementHandler;
	
	private AssetGroupManager assetManager;
	private InputContext inputContext;
	private HeadsUpDisplay hud;
	private GameMap map;
	
	private PauseMenuScreen pauseMenu;
	private ShopMenuScreen shopMenu;
	
	private PhysicsWorld physicsWorld;
	
	private boolean gameOver = false;
	
	private GameScreen() {
		initializeGame();
	}
	
	private void initializeGame() {
		initializeCamerasAndViewports();
		initializeInputContext();
		createBox2DWorld();
		createGameMap();
		createHud();
		createCameraMovementHandler();
		initializeEventHandling();
	}
	
	private void initializeCamerasAndViewports() {
		camera = new OrthographicCamera();
		cameraHud = new OrthographicCamera();
		viewport = new FitViewport(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, camera);
		viewportHud = new FitViewport(Constants.SCENE_WIDTH * Constants.HUD_SCENE_FACTOR, Constants.SCENE_HEIGHT * Constants.HUD_SCENE_FACTOR, cameraHud);
		
		cameraHud.position.x = Constants.HUD_SCENE_WIDTH * 0.5f;
		cameraHud.position.y = Constants.HUD_SCENE_HEIGHT * 0.5f;
		
		camera.position.x = Constants.SCENE_WIDTH;
		camera.position.y = Constants.SCENE_HEIGHT;
		camera.zoom = 1.5f;
		
		cameraHud.update();
	}
	
	private void initializeInputContext() {
		Game.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		inputContext = Game.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	private void createBox2DWorld() {
		physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.createWorld();
	}
	
	private void createGameMap() {
		GameMap.createGameMap(camera);
		map = GameMap.getInstance();
		GameMapManager gameMapManager = GameMapManager.getInstance();
		String initialMapIdentifier = gameMapManager.getInitialMapIdentifier();
		map.showMap(initialMapIdentifier);
	}
	
	private void changeMap(String mapIdentifier) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			map.showMap(mapIdentifier);
			InteractionManager.getInstance().resetInteractions();
		});
	}
	
	private void createHud() {
		hud = new HeadsUpDisplay(Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT, cameraHud);
	}
	
	private void createCameraMovementHandler() {
		cameraMovementHandler = CameraMovementHandler.createInstanceIfAbsent(camera);
	}
	
	private void initializeEventHandling() {
		EventHandler.getInstance().registerEventListener(this);
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_START));
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		map.executeBeforeWorldStep();
		physicsWorld.step(1f / 60f);
		map.executeAfterWorldStep();
		
		map.processPlayer(delta);
		cameraMovementHandler.moveCamera(delta);
		
		map.processAndRender(delta);
		hud.render(delta);
		
		checkGameOver();
		
		if (Constants.DEBUG) {
			physicsWorld.renderDebugGraphics(camera.combined);
		}
	}
	
	private void checkGameOver() {
		if (!gameOver && Game.getInstance().isGameOver()) {
			gameOver = true;
			showGameOverMenuScreen();
		}
	}
	
	private void showGameOverMenuScreen() {
		new GameOverMenuScreen(this).showMenu();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_SHOW_MENU) && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
			showPauseMenu();
			return true;
		}
		return false;
	}
	
	private void showPauseMenu() {
		if (pauseMenu == null) {
			pauseMenu = new PauseMenuScreen(this);
		}
		pauseMenu.showMenu();
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAYER_RESPAWNED) {
			gameOver = false;
		}
		if (event.eventType == EventType.SHOW_IN_GAME_SHOP_MENU) {
			showShopMenu();
		}
		if (event.eventType == EventType.CHANGE_MAP) {
			changeMap(event.stringValue);
		}
	}
	
	private void showShopMenu() {
		if (shopMenu == null) {
			shopMenu = new ShopMenuScreen(this);
		}
		shopMenu.showMenu();
	}
	
	public void restartGame() {
		Gdx.app.log(getClass().getSimpleName(), "--- Restarting Game ------------------------------------------------------------");
		changeMap(GameMapManager.getInstance().getInitialMapIdentifier());
	}
	
	/**
	 * Get the path of the configuration file, for the mini-map from the current game map or null if the property is not set.
	 */
	public String getGameMapConfigPath() {
		return map.getGlobalMapProperties().get(GameMap.GlobalMapPropertyKeys.MINI_MAP_CONFIG_PATH.getKey(), String.class);
	}
	
	/**
	 * The players relative position on the map ((0, 0) -> lower left, (1, 1) -> upper right)
	 */
	public Vector2 getPlayersPositionOnMap() {
		PlayableCharacter player = Player.getInstance();
		Vector2 playersRelativePosition = player.getPosition().scl(Constants.SCREEN_TO_WORLD);
		playersRelativePosition.x /= map.getMapWidth();
		playersRelativePosition.y /= map.getMapHeight();
		return playersRelativePosition;
	}
	
	public Array<FastTravelPointProperties> getFastTravelPositions() {
		return FastTravelDataHandler.getInstance().getFastTravelPositions();
	}
	
	public float getMapWidth() {
		return map.getMapWidth();
	}
	
	public float getMapHeight() {
		return map.getMapHeight();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
		viewportHud.update(width, height, false);
	}
	
	@Override
	public void dispose() {
		EventHandler.getInstance().removeEventListener(this);
		inputContext.removeListener(this);
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		
		map.dispose();
		hud.dispose();
		if (pauseMenu != null) {
			pauseMenu.dispose();
		}
	}
}
