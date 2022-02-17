package net.jfabricationgames.gdx.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.camera.CameraMovementHandler;
import net.jfabricationgames.gdx.character.animal.AnimalFactory;
import net.jfabricationgames.gdx.character.enemy.EnemyFactory;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacterFactory;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.cutscene.action.CutsceneActionFactory;
import net.jfabricationgames.gdx.data.GameDataHandler;
import net.jfabricationgames.gdx.data.handler.FastTravelDataHandler;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.global.GlobalEventExecutionType;
import net.jfabricationgames.gdx.hud.implementation.HeadsUpDisplay;
import net.jfabricationgames.gdx.hud.implementation.OnScreenTextBox;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.interaction.InteractionManager;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.object.GameObjectFactory;
import net.jfabricationgames.gdx.object.interactive.InteractiveAction;
import net.jfabricationgames.gdx.object.interactive.StateSwitchObject;
import net.jfabricationgames.gdx.object.moveable.MovableObject;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.projectile.ProjectileFactory;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.menu.GameOverMenuScreen;
import net.jfabricationgames.gdx.screen.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screen.menu.LoadingScreen;
import net.jfabricationgames.gdx.screen.menu.PauseMenuScreen;
import net.jfabricationgames.gdx.screen.menu.ShopMenuScreen;
import net.jfabricationgames.gdx.state.GameStateManager;
import net.jfabricationgames.gdx.util.StartConfigUtil;

public class GameScreen extends ScreenAdapter implements InputActionListener, EventListener, InGameMenuScreen.MenuGameScreen {
	
	private static final float WORLD_TIME_STEP = 1f / 60f;
	
	private static final String ACTION_SHOW_MENU = "menu";
	
	private static GameScreen instance;
	
	public static void loadAndShowGameScreen(Runnable afterCreatingGameScreen) {
		AssetGroupManager assetManager = AssetGroupManager.getInstance();
		showLoadingScreen(afterCreatingGameScreen);
		assetManager.loadGroup(ScreenManager.ASSET_GROUP_NAME);
	}
	
	private static void showLoadingScreen(Runnable afterCreatingGameScreen) {
		new LoadingScreen(() -> {
			createGameScreen();
			ScreenManager.getInstance().setScreen(instance);
			if (afterCreatingGameScreen != null) {
				afterCreatingGameScreen.run();
			}
		}).showMenu();
	}
	
	private static void createGameScreen() {
		if (instance == null) {
			instance = new GameScreen();
		}
	}
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	private CameraMovementHandler cameraMovementHandler;
	
	private InputContext inputContext;
	private HeadsUpDisplay hud;
	private GameMap map;
	
	private PauseMenuScreen pauseMenu;
	private ShopMenuScreen shopMenu;
	
	private PhysicsWorld physicsWorld;
	
	private boolean gameOver = false;
	
	private GameScreen() {
		initializeCamerasAndViewports();
		initializeInputContext();
		createBox2DWorld();
		createHud();
		
		createGameMap();
		initializeGameMapObjectProcessing();
		initializeFactories();
		loadGameMap();
		
		createCameraMovementHandler();
		initializeEventHandling();
		initializeCutsceneHandler();
		initializeConditionType();
		initializeInteractiveAction();
		
		ScreenManager.getInstance().setGameScreen(this);
	}
	
	private void initializeCamerasAndViewports() {
		camera = new OrthographicCamera();
		cameraHud = new OrthographicCamera();
		viewport = new FitViewport(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, camera);
		viewportHud = new FitViewport(Constants.SCENE_WIDTH * Constants.HUD_SCENE_FACTOR, Constants.SCENE_HEIGHT * Constants.HUD_SCENE_FACTOR,
				cameraHud);
		
		cameraHud.position.x = Constants.HUD_SCENE_WIDTH * 0.5f;
		cameraHud.position.y = Constants.HUD_SCENE_HEIGHT * 0.5f;
		
		camera.position.x = Constants.SCENE_WIDTH;
		camera.position.y = Constants.SCENE_HEIGHT;
		camera.zoom = 1.5f;
		
		cameraHud.update();
	}
	
	private void initializeInputContext() {
		inputContext = InputManager.getInstance().changeInputContext(ScreenManager.INPUT_CONTEXT_NAME);
		inputContext.addListener(this);
	}
	
	private void createBox2DWorld() {
		physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.createWorld();
	}
	
	private void createGameMap() {
		GameMapManager.getInstance().createGameMap(camera);
		map = GameMapManager.getInstance().getMap();
	}
	
	private void initializeGameMapObjectProcessing() {
		GameMapManager.getInstance().getMap().addPostAddObjectProcessing(MovableObject::sortMovableGameObjectsLast);
	}
	
	private void initializeFactories() {
		GameMap gameMap = GameMapManager.getInstance().getMap();
		
		EnemyFactory.setGameMap(gameMap);
		AnimalFactory.setGameMap(gameMap);
		NonPlayableCharacterFactory.setGameMap(gameMap);
		
		GameObjectFactory.setGameMap(gameMap);
		GameObjectFactory.setEnemySpawnFactory(EnemyFactory.asInstance());
		GameObjectFactory.setItemSpawnFactory(ItemFactory.asInstance());
		GameObjectFactory.setItemDropUtil(ItemDropUtil.asInstance());
		GameObjectFactory.setPlayerObjectClass(PlayableCharacter.class);
		
		ItemFactory.setItemMap(map);
		ItemFactory.setItemTextBox(OnScreenTextBox.getInstance());
		ItemFactory.setItemSpecialActionByNameFunction(SpecialAction::getByContainingName);
		ItemFactory.setPlayerCoinsSupplier(Player.getInstance()::getCoins);
		
		ProjectileFactory.setGameMap(gameMap);
		
		CutsceneActionFactory.setHudCamera(cameraHud);
	}
	
	private void loadGameMap() {
		GameMapManager gameMapManager = GameMapManager.getInstance();
		
		if (isNewGame()) {
			String initialMapIdentifier = gameMapManager.getInitialMapIdentifier();
			gameMapManager.showMap(initialMapIdentifier, gameMapManager.getInitialStartingPointId());
			StartConfigUtil.configureMapStartConfig(gameMapManager.getDebugStartConfig(), gameMapManager.getInitialStartingPointId());
		}
		else {
			GameDataHandler gameDataHandler = GameDataHandler.getInstance();
			
			String currentMapIdentifier = gameDataHandler.getCurrentMapIdentifier();
			gameMapManager.showMap(currentMapIdentifier, 0); // use starting point 0 (which exists on every map) and change to the correct position afterwards
			
			Vector2 playerPosition = gameDataHandler.getPlayerPosition();
			Player.getInstance().setPosition(playerPosition.x, playerPosition.y);
		}
	}
	
	private boolean isNewGame() {
		return GameDataHandler.getInstance().getCurrentMapIdentifier() == null;
	}
	
	private void changeMap(String mapIdentifier, int playerStartingPointId) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			GameMapManager.getInstance().showMap(mapIdentifier, playerStartingPointId);
			
			// reset interactions that would not be removed, because the player contact doesn't end when the map is changed (or at least it seems to not end for the box2d world)
			InteractionManager.getInstance().resetInteractions();
			
			// fire a MAP_ENTERED event for the spawn points to know that they need to add their objects to the world
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.MAP_ENTERED).setStringValue(mapIdentifier));
			
			// fire configured events, that are needed at a specific entry point
			String mapStartConfig = GameMapManager.getInstance().getMapStartConfig(mapIdentifier);
			if (mapStartConfig != null) {
				StartConfigUtil.configureMapStartConfig(mapStartConfig, playerStartingPointId);
			}
		});
	}
	
	private void createHud() {
		hud = new HeadsUpDisplay(Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT, cameraHud, Player.getInstance());
	}
	
	private void createCameraMovementHandler() {
		cameraMovementHandler = CameraMovementHandler.createInstanceIfAbsent(camera, () -> Player.getInstance().getPosition(),
				() -> CutsceneHandler.getInstance().isCameraControlledByCutscene());
	}
	
	private void initializeEventHandling() {
		EventHandler.getInstance().registerEventListener(this);
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_STARTED));
		GlobalEventExecutionType.setGlobalEventTextBox(OnScreenTextBox.getInstance());
		GlobalEventExecutionType.setConditionalExecutor(ConditionHandler.getInstance());
	}
	
	private void initializeCutsceneHandler() {
		CutsceneHandler.getInstance().setUnitProvider(map);
		CutsceneHandler.getInstance().setTextDisplayedSupplier(() -> OnScreenTextBox.getInstance().isDisplaying());
	}
	
	private void initializeConditionType() {
		ConditionType.setIsStateSwitchActive(StateSwitchObject::isStateSwitchActive);
	}
	
	private void initializeInteractiveAction() {
		InteractiveAction.setTextBox(OnScreenTextBox.getInstance());
		InteractiveAction.setPlayer(Player.getInstance());
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		map.executeBeforeWorldStep();
		physicsWorld.step(WORLD_TIME_STEP);
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
		if (!gameOver && GameStateManager.getInstance().isGameOver()) {
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
			showShopMenu(event.stringValue);
		}
		if (event.eventType == EventType.CHANGE_MAP) {
			changeMap(event.stringValue, event.intValue);
		}
		if (event.eventType == EventType.GAME_LOADED) {
			String currentMapIdentifier = map.getCurrentMapIdentifier();
			String mapStartConfig = GameMapManager.getInstance().getMapStartConfig(currentMapIdentifier);
			if (mapStartConfig != null) {
				StartConfigUtil.executeGameLoadStartConfig(mapStartConfig);
			}
		}
	}
	
	private void showShopMenu(String shopConfigFilePath) {
		if (shopMenu == null) {
			shopMenu = new ShopMenuScreen(this);
		}
		shopMenu.loadBuyableItemConfig(shopConfigFilePath);
		shopMenu.showMenu();
	}
	
	/**
	 * Get the path of the configuration file, for the mini-map from the current game map or null if the property is not set.
	 */
	@Override
	public String getGameMapConfigPath() {
		return map.getGlobalMapProperties().get(GameMap.GlobalMapPropertyKeys.MINI_MAP_CONFIG_PATH.getKey(), String.class);
	}
	
	/**
	 * The players relative position on the map ((0, 0) -> lower left, (1, 1) -> upper right)
	 */
	@Override
	public Vector2 getPlayersPositionOnMap() {
		PlayableCharacter player = Player.getInstance();
		Vector2 playersRelativePosition = player.getPosition().scl(Constants.SCREEN_TO_WORLD);
		playersRelativePosition.x /= map.getMapWidth();
		playersRelativePosition.y /= map.getMapHeight();
		return playersRelativePosition;
	}
	
	@Override
	public Array<FastTravelPointProperties> getFastTravelPositions() {
		return FastTravelDataHandler.getInstance().getFastTravelPositions();
	}
	
	@Override
	public float getMapWidth() {
		return map.getMapWidth();
	}
	
	@Override
	public float getMapHeight() {
		return map.getMapHeight();
	}
	
	@Override
	public void resize(int width, int height) {
		Gdx.app.log(getClass().getSimpleName(), "Resizing screen to: " + width + " x " + height);
		viewport.update(width, height, false);
		viewportHud.update(width, height, false);
	}
	
	@Override
	public void dispose() {
		EventHandler.getInstance().removeEventListener(this);
		inputContext.removeListener(this);
		//AssetGroupManager.getInstance().unloadGroup(ScreenManager.ASSET_GROUP_NAME);
		
		map.dispose();
		hud.dispose();
		if (pauseMenu != null) {
			pauseMenu.dispose();
		}
	}
}
