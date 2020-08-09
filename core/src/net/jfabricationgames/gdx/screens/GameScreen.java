package net.jfabricationgames.gdx.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
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
import net.jfabricationgames.gdx.hud.HeadsUpDisplay;

public class GameScreen extends ScreenAdapter {
	
	public static final float WORLD_TO_SCREEN = 4.0f;
	public static final float SCENE_WIDTH = 1280f;
	public static final float SCENE_HEIGHT = 720f;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	public static final String ASSET_GROUP_NAME = "game";
	
	private static final float CAMERA_SPEED = 600.0f;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float MOVEMENT_EDGE_OFFSET = 250f;
	private static final float MOVEMENT_RANGE_X = SCENE_WIDTH * 0.5f - MOVEMENT_EDGE_OFFSET;
	private static final float MOVEMENT_RANGE_Y = SCENE_HEIGHT * 0.5f - MOVEMENT_EDGE_OFFSET;
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	
	private SpriteBatch batch;
	
	private AssetGroupManager assetManager;
	
	private Dwarf dwarf;
	
	private DebugGridRenderer debugGridRenderer;
	
	private HeadsUpDisplay hud;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	private Array<Sprite> items;
	// private Array<Sprite> triggers;
	
	private TextureAtlas itemsAtlas;
	
	public GameScreen() {
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		initializeCamerasAndViewports();
		
		hud = new HeadsUpDisplay(cameraHud);
		
		batch = new SpriteBatch();
		
		dwarf = new Dwarf();
		dwarf.move(SCENE_WIDTH * 0.9f, SCENE_HEIGHT * 0.8f);
		
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(40f, 40f);
		debugGridRenderer.stopDebug();
		
		itemsAtlas = assetManager.get("packed/items/items.atlas");
		
		map = assetManager.get("map/map.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);
		
		items = new Array<Sprite>();
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
		renderDebugGraphics(delta);
		renderGameGraphics(delta);
		hud.render(delta);
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
		for (Sprite item : items) {
			item.draw(batch);
		}
	}
	
	private void renderDebugGraphics(float delta) {
		debugGridRenderer.updateCamera(camera);
		debugGridRenderer.render(delta);
	}
	
	private void renderGameGraphics(float delta) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderItems();
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
	
	private void processMapMetaData() {
		MapObjects objects = map.getLayers().get("objects").getObjects();
		
		if (objects == null) {
			throw new IllegalStateException("The 'objects' layer couldn't be loaded.");
		}
		
		for (MapObject object : objects) {
			String name = object.getName();
			String[] parts = name.split("[.]");
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle rectangle = rectangleObject.getRectangle();
			MapProperties properties = object.getProperties();
			
			Gdx.app.log(getClass().getSimpleName(), "Processing map object: " + name + " at [x: " + rectangle.x + ", y: " + rectangle.y + ", w: "
					+ rectangle.width + ", h: " + rectangle.height + "] properties: " + mapPropertiesToString(properties, false));
			
			switch (parts[0]) {
				case "player":
					if (parts[1].equals("startingPosition")) {
						dwarf.setPosition(rectangle.x, rectangle.y);
					}
					break;
				case "item":
					Sprite item = new Sprite(itemsAtlas.findRegion(parts[1]));
					item.setPosition(rectangle.x, rectangle.y);
					item.setScale(WORLD_TO_SCREEN);
					items.add(item);
					break;
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
	
	private String mapPropertiesToString(MapProperties properties, boolean includePosition) {
		StringBuilder sb = new StringBuilder();
		
		Array<String> excludedKeys = new Array<>();
		if (!includePosition) {
			excludedKeys.addAll("x", "y", "width", "height");
		}
		
		sb.append('{');
		Iterator<String> keys = properties.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = properties.get(key).toString();
			if (!excludedKeys.contains(key, false)) {
				sb.append('\"').append(key).append('\"').append(": ").append('\"').append(value).append("\", ");
			}
		}
		sb.setLength(sb.length() - 2);
		sb.append('}');
		
		return sb.toString();
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
