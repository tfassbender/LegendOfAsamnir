package net.jfabricationgames.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.character.Dwarf;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationAssetManager;
import net.jfabricationgames.gdx.debug.DebugGridRenderer;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class GameScreen extends ScreenAdapter {
	
	public static final float WORLD_TO_SCREEN = 4.0f;
	public static final float SCENE_WIDTH = 1280f;
	public static final float SCENE_HEIGHT = 720f;
	
	public static final String INPUT_CONTEXT_NAME = "game";
	
	private static final float CAMERA_SPEED = 400.0f;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 2.0f;
	private static final float CAMERA_ZOOM_MIN = 0.25f;
	
	private static final float WORLD_EDGE_SIZE = 10f;
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraHud;
	private Viewport viewport;
	private Viewport viewportHud;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private CharacterAnimationAssetManager assetManager;
	
	private Dwarf dwarf;
	
	private DebugGridRenderer debugGridRenderer;
	
	private ScreenTextWriter screenTextWriter;
	
	public GameScreen() {
		DwarfScrollerGame.getInstance().changeInputContext(INPUT_CONTEXT_NAME);
		initializeCamerasAndViewports();
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		assetManager = CharacterAnimationAssetManager.getInstance();
		
		dwarf = new Dwarf();
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(40f, 40f);
		//debugGridRenderer.stopDebug();
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont("vikingMedium");
	}
	
	private void initializeCamerasAndViewports() {
		camera = new OrthographicCamera();
		cameraHud = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		viewportHud = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, cameraHud);
		//viewport = new ExtendViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		//viewportHud = new ExtendViewport(SCENE_WIDTH, SCENE_HEIGHT, cameraHud);
		
		cameraHud.position.x += SCENE_WIDTH * 0.5;
		cameraHud.position.y += SCENE_HEIGHT * 0.5;
		
		cameraHud.update();
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		moveCamera(delta);
		renderDebugGraphics(delta);
		renderText();
		renderHUD(delta);
		renderGameGraphics(delta);
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
		assetManager.dispose();
	}
}
