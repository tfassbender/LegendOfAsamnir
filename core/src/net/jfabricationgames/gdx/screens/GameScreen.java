package net.jfabricationgames.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.Dwarf;
import net.jfabricationgames.gdx.debug.DebugGridRenderer;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class GameScreen extends ScreenAdapter {
	
	public static final float WORLD_TO_SCREEN = 4.0f;
	public static final float SCENE_WIDTH = 1280f;
	public static final float SCENE_HEIGHT = 720f;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	public static final String FONT_NAME = "vikingMedium";
	
	private static final float CAMERA_SPEED = 400.0f;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float MOVEMENT_EDGE_OFFSET = 250f;
	private static final float MOVEMENT_RANGE_X = SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET;
	private static final float MOVEMENT_RANGE_Y = SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET;
	
	private static final float WORLD_EDGE_SIZE = 10f;
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private AssetGroupManager assetManager;
	
	private Dwarf dwarf;
	
	private DebugGridRenderer debugGridRenderer;
	
	private ScreenTextWriter screenTextWriter;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	private Array<Sprite> items;
	private Array<Sprite> triggers;
	
	private TextureAtlas itemsAtlas;
	
	public GameScreen() {
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		initializeCamerasAndViewports();
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		dwarf = new Dwarf();
		dwarf.move(SCENE_WIDTH * 0.9f, SCENE_HEIGHT * 0.8f);
		
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(40f, 40f);
		debugGridRenderer.stopDebug();
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		itemsAtlas = assetManager.get("packed/items/items.atlas");
		
		map = assetManager.get("map/map.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);
		
		processMapMetaData();
	}
	
	private void initializeCamerasAndViewports() {
		camera = new OrthographicCamera();
		cameraHud = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		viewportHud = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, cameraHud);
		
		cameraHud.position.x = SCENE_WIDTH * 0.5f;
		cameraHud.position.y = SCENE_HEIGHT * 0.5f;
		
		camera.position.x = SCENE_WIDTH;
		camera.position.y = SCENE_HEIGHT;
		
		cameraHud.update();
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		moveCamera(delta);
		renderer.setView(camera);
		renderer.render();
		renderItems();
		renderDebugGraphics(delta);
		renderGameGraphics(delta);
		renderText();
		renderHUD(delta);
		moveCameraToPlayer();
	}
	
	private void moveCamera(float delta) {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			camera.position.x -= CAMERA_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			camera.position.x += CAMERA_SPEED * delta;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			camera.position.y += CAMERA_SPEED * delta;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			camera.position.y -= CAMERA_SPEED * delta;
		}
		
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
	

	private void renderItems() {
		batch.begin();
		for (Sprite item : items) {
			item.draw(batch);
		}
		batch.end();
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
	
	private void renderHUD(float delta) {
		shapeRenderer.setProjectionMatrix(cameraHud.combined);
		shapeRenderer.begin(ShapeType.Filled);
		drawWorldEdge();
		drawStatsBars();
		shapeRenderer.end();
	}
	
	private void renderText() {
		batch.setProjectionMatrix(cameraHud.combined);
		batch.begin();
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(2f);
		screenTextWriter.addText("Dwarf Scroller GDX", 100f, 0.1f * SCENE_HEIGHT);
		screenTextWriter.draw(batch);
		batch.end();
	}
	
	private void drawWorldEdge() {
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(0, 0, SCENE_WIDTH, WORLD_EDGE_SIZE);
		shapeRenderer.rect(0, 0, WORLD_EDGE_SIZE, SCENE_HEIGHT);
		shapeRenderer.rect(SCENE_WIDTH, SCENE_HEIGHT, -SCENE_WIDTH, -WORLD_EDGE_SIZE);
		shapeRenderer.rect(SCENE_WIDTH, SCENE_HEIGHT, -WORLD_EDGE_SIZE, -SCENE_HEIGHT);
	}
	
	private void drawStatsBars() {
		final float healthBarHeightPercent = 0.65f;
		final Vector2 tileUpperRight = new Vector2(SCENE_WIDTH - WORLD_EDGE_SIZE * 2f, SCENE_HEIGHT - WORLD_EDGE_SIZE * 2f);
		final Vector2 tileSize = new Vector2(-400, -80f);
		final Vector2 healthBarUpperRightOffset = new Vector2(-10f, -10f);
		final Vector2 healthBarSize = new Vector2(tileSize.x - healthBarUpperRightOffset.x * 2,
				(tileSize.y - (healthBarUpperRightOffset.y * 3)) * healthBarHeightPercent);
		final Vector2 manaBarUpperRightOffset = new Vector2(-10f, -10f + healthBarUpperRightOffset.y + healthBarSize.y);
		final Vector2 manaBarSize = new Vector2(tileSize.x - manaBarUpperRightOffset.x * 2,
				(tileSize.y - (healthBarUpperRightOffset.y * 3)) * (1 - healthBarHeightPercent));
		
		final Color[] backgroundBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
				new Color(0.3f, 0.3f, 0.3f, 1f), //top-right
				new Color(0.35f, 0.35f, 0.35f, 1f), //top-left
				new Color(0.2f, 0.2f, 0.2f, 1f), //bottom-left
				new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
		};
		
		final Color[] healthBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
				new Color(0f, 0.85f, 0f, 1f), //top-right
				Color.GREEN, //top-left
				Color.DARK_GRAY, //bottom-left
				new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
		};
		
		final Color[] manaBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
				new Color(0f, 0f, 0.85f, 1f), //top-right
				Color.BLUE, //top-left
				Color.DARK_GRAY, //bottom-left
				new Color(0.15f, 0.15f, 0.15f, 1f) //bottom-right
		
		};
		
		shapeRenderer.rect(tileUpperRight.x, tileUpperRight.y, tileSize.x, tileSize.y, backgroundBarColors[0], backgroundBarColors[1],
				backgroundBarColors[2], backgroundBarColors[3]);
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x,
				healthBarSize.y, healthBarColors[0], healthBarColors[1], healthBarColors[2], healthBarColors[3]);
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x, tileUpperRight.y + manaBarUpperRightOffset.y, manaBarSize.x, manaBarSize.y,
				manaBarColors[0], manaBarColors[1], manaBarColors[2], manaBarColors[3]);
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
	
	private void processMapMetaData() {
		// Load entities
		items = new Array<Sprite>();
		triggers = new Array<Sprite>();
		
		MapObjects objects = map.getLayers().get("objects").getObjects();
		
		for (MapObject object : objects) {
			String name = object.getName();
			String[] parts = name.split("_");
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle rectangle = rectangleObject.getRectangle();
			
			if (name.equals("starting_position_player")) {
				dwarf.setPosition(rectangle.x, rectangle.y);
			}
			else if (parts.length > 1 && parts[0].equals("item")) {
				Sprite item = new Sprite(itemsAtlas.findRegion(name));
				item.setPosition(rectangle.x, rectangle.y);
				items.add(item);
			}
			//			else if (parts.length > 0 && parts[0].equals("trigger")) {
			//				Sprite trigger = new Sprite(atlas.findRegion("pixel"));
			//				trigger.setColor(1.0f, 1.0f, 1.0f, 0.5f);
			//				trigger.setScale(rectangle.width, rectangle.height);
			//				trigger.setPosition(rectangle.x - rectangle.width * 0.5f, rectangle.y + rectangle.height * 0.5f);
			//				triggers.add(trigger);
			//			}
		}
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
		assetManager.unloadGroup(ASSET_GROUP_NAME);
	}
}
