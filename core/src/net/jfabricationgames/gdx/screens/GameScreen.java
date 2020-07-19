package net.jfabricationgames.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.character.Dwarf;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationAssetManager;

public class GameScreen extends ScreenAdapter {
	
	public static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	public static final float SCENE_WIDTH = 3.20f;
	public static final float SCENE_HEIGHT = 1.80f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private CharacterAnimationAssetManager assetManager;
	
	private Dwarf dwarf;
	
	public GameScreen() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		
		assetManager = CharacterAnimationAssetManager.getInstance();
		
		dwarf = new Dwarf();
	}
	
	@Override
	public void render(float delta) {
		//clear the screen (with a black screen)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		dwarf.render(delta, batch);
		batch.end();
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		assetManager.dispose();
	}
}
