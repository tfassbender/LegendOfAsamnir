package net.jfabricationgames.gdx.screens;

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
import net.jfabricationgames.gdx.hud.HeadsUpDisplay;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class GameScreen extends ScreenAdapter implements InputActionListener {
	
	public static final float WORLD_TO_SCREEN = 0.04f;
	public static final float SCREEN_TO_WORLD = 1f / WORLD_TO_SCREEN;
	public static final float SCENE_WIDTH = 12.80f;
	public static final float SCENE_HEIGHT = 8.20f;
	
	//the HUD uses a different scene size to make it easier to calculate in pixel units
	public static final float HUD_SCENE_FACTOR = 100f;
	public static final float HUD_SCENE_WIDTH = SCENE_WIDTH * HUD_SCENE_FACTOR;
	public static final float HUD_SCENE_HEIGHT = SCENE_HEIGHT * HUD_SCENE_FACTOR;
	
	public static final boolean RENDER_DEBUG_GRAPHICS = false;
	public static final int VELOCITY_ITERATIONS = 6;
	public static final int POSITION_ITERATIONS = 2;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	
	private static final float CAMERA_SPEED = 150.0f * WORLD_TO_SCREEN;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float MOVEMENT_EDGE_OFFSET = 50f;
	private static final float MOVEMENT_RANGE_X = SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET * WORLD_TO_SCREEN;
	private static final float MOVEMENT_RANGE_Y = SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET * WORLD_TO_SCREEN;
	
	private static final String INPUT_AXIS_CAMERA_VERTICAL_MOVMENT = "camera_vertical_move_axis";
	private static final String INPUT_AXIS_CAMERA_HORIZONTAL_MOVMENT = "camera_horizontal_move_axis";
	private static final float INPUT_AXIS_CAMERA_MOVEMENT_THRESHOLD = 0.3f;
	
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
	
	private Box2DDebugRenderer debugRenderer;
	private World world;
	
	public GameScreen() {
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
		
		dwarf = new Dwarf();
		Vector2 playerStartingPosition = map.getPlayerStartingPosition();
		dwarf.setPosition(playerStartingPosition.x, playerStartingPosition.y);
		
		//take some damage to test the items (only for demo)
		dwarf.takeDamage(50f);
		dwarf.takeArmorDamage(60f);
		
		hud = new HeadsUpDisplay(HUD_SCENE_WIDTH, HUD_SCENE_HEIGHT, cameraHud, dwarf);
		
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(40f, 40f);
		debugGridRenderer.stopDebug();
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
		PhysicsWorld.getInstance().removeBodiesAndFixtures();
		
		map.render(delta);
		renderDebugGraphics(delta);
		renderGameGraphics(delta);
		hud.render(delta);
		moveCamera(delta);
		moveCameraToPlayer();
		
		if (RENDER_DEBUG_GRAPHICS) {
			debugRenderer.render(world, camera.combined);
		}
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		// dummy implementation, because only controllerAxes are used yet
		return false;
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
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
		viewportHud.update(width, height, false);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		map.dispose();
		PhysicsWorld.getInstance().disposeWorld();
		debugRenderer.dispose();
		debugGridRenderer.dispose();
		assetManager.unloadGroup(ASSET_GROUP_NAME);
	}
}
