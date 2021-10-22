package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.constants.Constants;

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
		shapeRenderer.rect(0, 0, Constants.HUD_SCENE_WIDTH, worldEdgeSize);
		shapeRenderer.rect(0, 0, worldEdgeSize, Constants.HUD_SCENE_HEIGHT);
		shapeRenderer.rect(Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT, -Constants.HUD_SCENE_WIDTH, -worldEdgeSize);
		shapeRenderer.rect(Constants.HUD_SCENE_WIDTH, Constants.HUD_SCENE_HEIGHT, -worldEdgeSize, -Constants.HUD_SCENE_HEIGHT);
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
