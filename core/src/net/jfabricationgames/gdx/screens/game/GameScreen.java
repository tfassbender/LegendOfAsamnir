package net.jfabricationgames.gdx.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.camera.CameraMovementHandler;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.container.data.CharacterFastTravelProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.HeadsUpDisplay;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.menu.GameOverMenuScreen;
import net.jfabricationgames.gdx.screens.menu.PauseMenuScreen;
import net.jfabricationgames.gdx.screens.menu.ShopMenuScreen;

public class GameScreen extends ScreenAdapter implements InputActionListener, EventListener {
	
	public static final boolean DEBUG = false;
	
	public static final float WORLD_TO_SCREEN = 0.04f;
	public static final float SCREEN_TO_WORLD = 1f / WORLD_TO_SCREEN;
	public static final float SCENE_WIDTH = 12.80f;
	public static final float SCENE_HEIGHT = 8.20f;
	
	//the HUD uses a different scene size to make it easier to calculate in pixel units
	public static final float HUD_SCENE_FACTOR = 100f;
	public static final float HUD_SCENE_WIDTH = SCENE_WIDTH * HUD_SCENE_FACTOR;
	public static final float HUD_SCENE_HEIGHT = SCENE_HEIGHT * HUD_SCENE_FACTOR;
	
	public static final int VELOCITY_ITERATIONS = 6;
	public static final int POSITION_ITERATIONS = 2;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	
	private static final String ACTION_SHOW_MENU = "menu";
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	private CameraMovementHandler cameraMovementHandler;
	
	private AssetGroupManager assetManager;
	private InputContext inputContext;
	private HeadsUpDisplay hud;
	private GameMap map;
	private PlayableCharacter player;
	
	private PauseMenuScreen pauseMenu;
	private ShopMenuScreen shopMenu;
	
	private Box2DDebugRenderer debugRenderer;
	private World world;
	
	private boolean gameOver = false;
	
	public GameScreen() {
		createAssetManager();
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
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		viewportHud = new FitViewport(SCENE_WIDTH * HUD_SCENE_FACTOR, SCENE_HEIGHT * HUD_SCENE_FACTOR, cameraHud);
		
		cameraHud.position.x = HUD_SCENE_WIDTH * 0.5f;
		cameraHud.position.y = HUD_SCENE_HEIGHT * 0.5f;
		
		camera.position.x = SCENE_WIDTH;
		camera.position.y = SCENE_HEIGHT;
		camera.zoom = 1.5f;
		
		cameraHud.update();
	}
	
	private void createAssetManager() {
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
	}
	
	private void initializeInputContext() {
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	private void createBox2DWorld() {
		world = PhysicsWorld.getInstance().createWorld(new Vector2(0, 0f), true);
		debugRenderer = new Box2DDebugRenderer(true, /* bodies */
				false, /* joints */
				false, /* aabbs */
				true, /* inactive bodies */
				true, /* velocities */
				false /* contacts */);
	}
	
	private void createGameMap() {
		//map = new GameMap("map/map3.tmx", camera);
		map = new GameMap("map/level_tutorial.tmx", camera);
		player = map.getPlayer();
	}
	
	private void createHud() {
		hud = new HeadsUpDisplay(HUD_SCENE_WIDTH, HUD_SCENE_HEIGHT, cameraHud, player);
	}
	
	private void createCameraMovementHandler() {
		cameraMovementHandler = CameraMovementHandler.createInstance(camera, player);
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
		
		world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		PhysicsWorld.getInstance().afterWorldStep();
		
		map.renderBackground();
		map.processAndRenderGameObject(delta);
		map.renderTerrain();
		hud.render(delta);
		
		cameraMovementHandler.moveCamera(delta);
		
		checkGameOver();
		
		if (DEBUG) {
			debugRenderer.render(world, camera.combined);
		}
	}
	
	private void checkGameOver() {
		if (!gameOver && player.isGameOver()) {
			gameOver = true;
			showGameOverMenuScreen();
		}
	}
	
	private void showGameOverMenuScreen() {
		new GameOverMenuScreen(this, player).showMenu();
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
			pauseMenu = new PauseMenuScreen(this, player);
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
	}
	
	private void showShopMenu() {
		if (shopMenu == null) {
			shopMenu = new ShopMenuScreen(this, player);
		}
		shopMenu.showMenu();
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
		Vector2 playersRelativePosition = player.getPosition().cpy().scl(SCREEN_TO_WORLD);
		playersRelativePosition.x /= map.getMapWidth();
		playersRelativePosition.y /= map.getMapHeight();
		return playersRelativePosition;
	}
	
	public Array<CharacterFastTravelProperties> getFastTravelPositions() {
		return player.getFastTravelContainer().getFastTravelPositions();
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
		debugRenderer.dispose();
		if (pauseMenu != null) {
			pauseMenu.dispose();
		}
	}
}
