package net.jfabricationgames.gdx.debug;

import java.util.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

public class DebugGridRenderer implements DebugRenderer, Disposable {
	
	private boolean drawGrid;
	
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	
	private float minLineX = -10000;
	private float maxLineX = 10000;
	private float minLineY = -10000;
	private float maxLineY = 10000;
	
	private float lineDeltaX = 1;
	private float lineDeltaY = 1;
	
	public DebugGridRenderer() {
		drawGrid = true;
		shapeRenderer = new ShapeRenderer();
	}
	
	public void updateCamera(OrthographicCamera camera) {
		Objects.requireNonNull(camera, "The camera mussn't be null");
		this.camera = camera;
	}
	
	@Override
	public void render(float delta) {
		if (drawGrid) {
			if (camera != null) {
				shapeRenderer.setProjectionMatrix(camera.combined);
			}
			
			shapeRenderer.begin(ShapeType.Line);
			
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.line(minLineX, 0.0f, maxLineX, 0.0f);
			shapeRenderer.line(0.0f, minLineY, 0.0f, maxLineY);
			
			shapeRenderer.setColor(Color.WHITE);
			
			for (float y = minLineY; y <= maxLineY; y += lineDeltaY) {
				if (Math.abs(y) > 0.01) {
					shapeRenderer.line(minLineX, y, maxLineX, y);
				}
			}
			
			for (float x = minLineX; x <= maxLineX; x += lineDeltaX) {
				if (Math.abs(x) > 0.01) {
					shapeRenderer.line(x, minLineY, x, maxLineY);
				}
			}
			
			shapeRenderer.end();
		}
	}
	
	@Override
	public void startDebug() {
		drawGrid = true;
	}
	
	@Override
	public void stopDebug() {
		drawGrid = false;
	}
	
	public void setGridRangeX(float min, float max) {
		this.minLineX = min;
		this.maxLineX = max;
	}
	
	public void setGridRangeY(float min, float max) {
		this.minLineY = min;
		this.maxLineY = max;
	}
	
	public void setLineOffsets(float deltaX, float deltaY) {
		this.lineDeltaX = deltaX;
		this.lineDeltaY = deltaY;
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
