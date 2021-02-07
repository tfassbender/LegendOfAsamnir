package net.jfabricationgames.gdx.screens.menu.dialog;

import com.badlogic.gdx.graphics.Color;

import net.jfabricationgames.gdx.screens.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;

public class LoadGameDialog extends InGameMenuDialog {
	
	public LoadGameDialog() {
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED).setPosition(935, 550).setSize(110, 40).build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.begin();
			
			background.draw(batch, 40, 50, 1130, 570);
			banner.draw(batch, 80, 480, 460, 200);
			buttonBackToMenu.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Load Game", 155, 594);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS + "Back", 970, 593);
	}
	
	public void setFocusToBackButton() {
		buttonBackToMenu.setFocused(true);
	}
}
