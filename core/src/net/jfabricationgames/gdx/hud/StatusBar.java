package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.screens.GameScreen;

public class StatusBar implements Disposable {
	
	private final float healthBarHeightPercent = 0.65f;
	private final Vector2 size = new Vector2(-400, -80); //negative values because it's drawn from the top right
	private final Vector2 healthBarUpperRightOffset = new Vector2(-10f, -10f);
	private final Vector2 healthBarSize = new Vector2(size.x - healthBarUpperRightOffset.x * 2,
			(size.y - (healthBarUpperRightOffset.y * 3)) * healthBarHeightPercent);
	private final Vector2 manaBarUpperRightOffset = new Vector2(-10f, -10f + healthBarUpperRightOffset.y + healthBarSize.y);
	private final Vector2 manaBarSize = new Vector2(size.x - manaBarUpperRightOffset.x * 2,
			(size.y - (healthBarUpperRightOffset.y * 3)) * (1 - healthBarHeightPercent));
	
	private final Color[] backgroundBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
			new Color(0.3f, 0.3f, 0.3f, 1f), //top-right
			new Color(0.35f, 0.35f, 0.35f, 1f), //top-left
			new Color(0.2f, 0.2f, 0.2f, 1f), //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] healthBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
			new Color(0f, 0.85f, 0f, 1f), //top-right
			Color.GREEN, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] manaBarColors = new Color[] {// positions are inverted because the size of the rectangles is negative
			new Color(0f, 0f, 0.85f, 1f), //top-right
			Color.BLUE, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.15f, 0.15f, 0.15f, 1f) //bottom-right
	
	};
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
	private Vector2 tileUpperRight = new Vector2(GameScreen.SCENE_WIDTH - 20f, GameScreen.SCENE_HEIGHT - 20f);
	
	public StatusBar(OrthographicCamera camera) {
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(float delta) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		drawStatusBar();
		shapeRenderer.end();
	}
	
	private void drawStatusBar() {
		shapeRenderer.rect(tileUpperRight.x, tileUpperRight.y, size.x, size.y, backgroundBarColors[0], backgroundBarColors[1], backgroundBarColors[2],
				backgroundBarColors[3]);
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x,
				healthBarSize.y, healthBarColors[0], healthBarColors[1], healthBarColors[2], healthBarColors[3]);
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x, tileUpperRight.y + manaBarUpperRightOffset.y, manaBarSize.x, manaBarSize.y,
				manaBarColors[0], manaBarColors[1], manaBarColors[2], manaBarColors[3]);
	}
	
	public void getPosition() {
		Vector2 position = new Vector2(tileUpperRight);
		position.x -= size.x;
		position.y -= size.y;
	}
	public void setPosition(float x, float y) {
		tileUpperRight.x = x + size.x;
		tileUpperRight.y = y + size.y;
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
