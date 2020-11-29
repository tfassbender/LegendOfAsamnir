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
		if (mapConfigPath == null) {
			Gdx.app.debug(getClass().getSimpleName(), "No map config given in current map. Mini-Map will not be shown.");
			config = new MapConfig();
			config.name = "Map";
			return;
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Loading map config from file: " + mapConfigPath);
		Json json = new Json();
		config = json.fromJson(MapConfig.class, Gdx.files.internal(mapConfigPath));
		
		mapTexture = new TextureLoader(config.texture).loadDefaultTexture();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(8, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED).setPosition(835, 550).setSize(110, 40).build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.begin();
			
			background.draw(batch, 120, -20, 950, 640);
			banner.draw(batch, 200, 480, 600, 200);
			buttonBackToMenu.draw(batch);
			
			if (mapTexture != null) {
				batch.draw(mapTexture, 100 + (1000 - config.textureHeight) / 2, 40 + (500 - config.textureHeight) / 2, config.textureWidth, config.textureHeight);
			}
			
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
		screenTextWriter.drawText(config.name, 275, 594, 450, Align.center, false);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS + "Back", 870, 593);
		
		if (mapTexture == null) {
			screenTextWriter.setScale(1.5f);
			screenTextWriter.drawText("No Map Available for this level", 200, 350, 790, Align.center, true);
		}
	}
}
