package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.screens.GameScreen;

public class WorldEdge implements Disposable {
	
	private float worldEdgeSize = 10f;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	public WorldEdge(OrthographicCamera camera) {
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(float delta) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		drawWorldEdge();
		shapeRenderer.end();
	}
	
	private void drawWorldEdge() {
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(0, 0, GameScreen.SCENE_WIDTH, worldEdgeSize);
		shapeRenderer.rect(0, 0, worldEdgeSize, GameScreen.SCENE_HEIGHT);
		shapeRenderer.rect(GameScreen.SCENE_WIDTH, GameScreen.SCENE_HEIGHT, -GameScreen.SCENE_WIDTH, -worldEdgeSize);
		shapeRenderer.rect(GameScreen.SCENE_WIDTH, GameScreen.SCENE_HEIGHT, -worldEdgeSize, -GameScreen.SCENE_HEIGHT);
	}
	
	public float getWorldEdgeSize() {
		return worldEdgeSize;
	}
	
	public void setWorldEdgeSize(float worldEdgeSize) {
		this.worldEdgeSize = worldEdgeSize;
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
