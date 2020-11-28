package net.jfabricationgames.gdx.screens.menu.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.screens.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.config.MapConfig;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class GameMapDialog extends InGameMenuDialog {
	
	private MapConfig config;
	
	private TextureRegion mapTexture;
	
	public GameMapDialog(String mapConfigPath) {
		loadConfig(mapConfigPath);
		createControls();
	}
	
	private void loadConfig(String mapConfigPath) {
		Gdx.app.debug(getClass().getSimpleName(), "Loading map config from file: " + mapConfigPath);
		Json json = new Json();
		config = json.fromJson(MapConfig.class, Gdx.files.internal(mapConfigPath));
		
		mapTexture = new TextureLoader(config.texture).loadDefaultTexture();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(8, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED).setPosition(935, 550).setSize(110, 40).build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.begin();
			
			background.draw(batch, 20, -20, 1150, 640);
			banner.draw(batch, 80, 480, 600, 200);
			buttonBackToMenu.draw(batch);
			
			batch.draw(mapTexture, 100 + (1000 - config.textureHeight) / 2, 40 + (500 - config.textureHeight) / 2, config.textureWidth, config.textureHeight);
			
			batch.end();
			
			//drawing the map and the text in the same batch won't work for some reason
			batch.begin();
			drawText();
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText(config.name, 155, 594, 450, Align.center, false);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS + "Back", 970, 593);
	}
}
