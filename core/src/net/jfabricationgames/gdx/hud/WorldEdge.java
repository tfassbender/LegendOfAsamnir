package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.screens.game.GameScreen;

public class WorldEdge implements Disposable {
	
	private float worldEdgeSize = 10f;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	public WorldEdge(HeadsUpDisplay hud) {
		this.camera = hud.getCamera();
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
		shapeRenderer.rect(0, 0, GameScreen.HUD_SCENE_WIDTH, worldEdgeSize);
		shapeRenderer.rect(0, 0, worldEdgeSize, GameScreen.HUD_SCENE_HEIGHT);
		shapeRenderer.rect(GameScreen.HUD_SCENE_WIDTH, GameScreen.HUD_SCENE_HEIGHT, -GameScreen.HUD_SCENE_WIDTH, -worldEdgeSize);
		shapeRenderer.rect(GameScreen.HUD_SCENE_WIDTH, GameScreen.HUD_SCENE_HEIGHT, -worldEdgeSize, -GameScreen.HUD_SCENE_HEIGHT);
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
