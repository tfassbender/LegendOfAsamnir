package net.jfabricationgames.gdx.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.Dwarf;
import net.jfabricationgames.gdx.debug.DebugGridRenderer;
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
	
	public static final float WORLD_TO_SCREEN = 0.04f;
	public static final float SCREEN_TO_WORLD = 1f / WORLD_TO_SCREEN;
	public static final float SCENE_WIDTH = 12.80f;
	public static final float SCENE_HEIGHT = 8.20f;
	
	//the HUD uses a different scene size to make it easier to calculate in pixel units
	public static final float HUD_SCENE_FACTOR = 100f;
	public static final float HUD_SCENE_WIDTH = SCENE_WIDTH * HUD_SCENE_FACTOR;
	public static final float HUD_SCENE_HEIGHT = SCENE_HEIGHT * HUD_SCENE_FACTOR;
	
	public static final boolean RENDER_DEBUG_GRAPHICS = true;
	public static final int VELOCITY_ITERATIONS = 6;
	public static final int POSITION_ITERATIONS = 2;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	
	private static final float CAMERA_SPEED = 150.0f * WORLD_TO_SCREEN;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float MOVEMENT_EDGE_OFFSET = 75f;
	private static final float MOVEMENT_RANGE_X = SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET * WORLD_TO_SCREEN;
	private static final float MOVEMENT_RANGE_Y = SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET * WORLD_TO_SCREEN;
	
	private static final String INPUT_AXIS_CAMERA_VERTICAL_MOVMENT = "camera_vertical_move_axis";
	private static final String INPUT_AXIS_CAMERA_HORIZONTAL_MOVMENT = "camera_horizontal_move_axis";
	private static final float INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD = 0.3f;
	
	private static final String ACTION_SHOW_MENU = "menu";
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	private SpriteBatch batch;
	
	private AssetGroupManager assetManager;
	private InputContext inputContext;
	private Dwarf dwarf;
	private DebugGridRenderer debugGridRenderer;
	private HeadsUpDisplay hud;
	private GameMap map;
	
	private PauseMenuScreen pauseMenu;
	private ShopMenuScreen shopMenu;
	
	private Box2DDebugRenderer debugRenderer;
	private World world;
	
	private boolean gameOver;
	
	public GameScreen() {
		gameOver = false;
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		initializeCamerasAndViewports();
		
		inputContext = DwarfScrollerGame.getInstance().getInputContext();
		inputContext.addListener(this);
		
		batch = new SpriteBatch();
		
		world = PhysicsWorld.getInstance().createWorld(new Vector2(0, 0f), true);
		debugRenderer = new Box2DDebugRenderer(true, /* bodies */
				false, /* joints */
				false, /* aabbs */
				true, /* inactive bodies */
				true, /* velocities */
				false /* contacts */);
		
		map = new GameMap("map/map3.tmx", camera);
		//map = new GameMap("map/level_tutorial.tmx", camera);
		
		dwarf = new Dwarf();
		Vector2 playerStartingPosition = map.getPlayerStartingPosition();
		dwarf.setPosition(playerStartingPosition.x, playerStartingPosition.y);
		
		hud = new HeadsUpDisplay(HUD_SCENE_WIDTH, HUD_SCENE_HEIGHT, cameraHud, dwarf);
		
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(40f, 40f);
		debugGridRenderer.stopDebug();
		
		EventHandler.getInstance().registerEventListener(this);
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_START));
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
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		PhysicsWorld.getInstance().afterWorldStep();
		
		map.renderBackground();
		map.processAndRenderGameObject(delta);
		renderDebugGraphics(delta);
		renderGameGraphics(delta);
		map.renderTerrain();
		hud.render(delta);
		moveCamera(delta);
		moveCameraToPlayer();
		
		checkGameOver();
		
		if (RENDER_DEBUG_GRAPHICS) {
			debugRenderer.render(world, camera.combined);
		}
	}
	
	private void renderDebugGraphics(float delta) {
		debugGridRenderer.updateCamera(camera);
		debugGridRenderer.render(delta);
	}
	
	private void renderGameGraphics(float delta) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		dwarf.render(delta, batch);
		batch.end();
	}
	
	private void moveCamera(float delta) {
		float cameraMovementAxisVertically = inputContext.getControllerAxisValue(INPUT_AXIS_CAMERA_VERTICAL_MOVMENT);
		float cameraMovementAxisHorizontally = inputContext.getControllerAxisValue(INPUT_AXIS_CAMERA_HORIZONTAL_MOVMENT);
		float cameraMovementSpeedX = 0;
		float cameraMovementSpeedY = 0;
		if (Math.abs(cameraMovementAxisHorizontally) > INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD) {
			cameraMovementSpeedX = CAMERA_SPEED * cameraMovementAxisHorizontally * delta;
		}
		if (Math.abs(cameraMovementAxisVertically) > INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD) {
			cameraMovementSpeedY = -CAMERA_SPEED * cameraMovementAxisVertically * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			cameraMovementSpeedX = -CAMERA_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			cameraMovementSpeedX = CAMERA_SPEED * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			cameraMovementSpeedY = CAMERA_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			cameraMovementSpeedY = -CAMERA_SPEED * delta;
		}
		
		camera.position.x += cameraMovementSpeedX;
		camera.position.y += cameraMovementSpeedY;
		
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom -= CAMERA_ZOOM_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom += CAMERA_ZOOM_SPEED * delta;
		}
		
		camera.zoom = MathUtils.clamp(camera.zoom, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
		
		//update the camera to re-calculate the matrices
		camera.update();
	}
	
	private void moveCameraToPlayer() {
		Vector2 dwarfPosition = dwarf.getPosition();
		
		//movement in positive X and Y direction
		float deltaX = camera.position.x - dwarfPosition.x;
		float deltaY = camera.position.y - dwarfPosition.y;
		float movementXPos = deltaX - MOVEMENT_RANGE_X;
		float movementYPos = deltaY - MOVEMENT_RANGE_Y;
		
		//movement in negative X and Y direction
		deltaX = dwarfPosition.x - camera.position.x;
		deltaY = dwarfPosition.y - camera.position.y;
		float movementXNeg = deltaX - MOVEMENT_RANGE_X;
		float movementYNeg = deltaY - MOVEMENT_RANGE_Y;
		
		camera.position.x -= Math.max(movementXPos, 0);
		camera.position.y -= Math.max(movementYPos, 0);
		
		camera.position.x += Math.max(movementXNeg, 0);
		camera.position.y += Math.max(movementYNeg, 0);
		
		camera.update();
	}
	
	private void checkGameOver() {
		if (!gameOver && dwarf.isGameOver()) {
			gameOver = true;
			showGameOverMenuScreen();
		}
	}
	
	private void showGameOverMenuScreen() {
		new GameOverMenuScreen(this, dwarf).showMenu();
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
			pauseMenu = new PauseMenuScreen(this, dwarf);
		}
		pauseMenu.showMenu();
	}
	
	@Override
	public void eventFired(EventConfig event) {
		if (event.eventType == EventType.PLAYER_RESPAWNED) {
			gameOver = false;
		}
		if (event.eventType == EventType.SHOW_IN_GAME_SHOP_MENU) {
			showShopMenu();
		}
	}
	
	private void showShopMenu() {
		if (shopMenu == null) {
			shopMenu = new ShopMenuScreen(this, dwarf);
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
		Vector2 playersRelativePosition = dwarf.getPosition().cpy().scl(SCREEN_TO_WORLD);
		playersRelativePosition.x /= map.getMapWidth();
		playersRelativePosition.y /= map.getMapHeight();
		return playersRelativePosition;
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
		
		batch.dispose();
		map.dispose();
		hud.dispose();
		debugRenderer.dispose();
		debugGridRenderer.dispose();
		if (pauseMenu != null) {
			pauseMenu.dispose();
		}
	}
}
