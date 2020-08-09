package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class InGameMenu {
	
	public static final String DEFAULT_FONT_NAME = "vikingMedium";
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private ScreenTextWriter screenTextWriter;
	
	public InGameMenu(OrthographicCamera camera) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(DEFAULT_FONT_NAME);
	}

	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderText();
		batch.end();
	}
	
	private void renderText() {
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(2f);
		screenTextWriter.addText("Dwarf Scroller GDX", 100f, 0.1f * GameScreen.SCENE_HEIGHT);
		screenTextWriter.draw(batch);
	}
}
