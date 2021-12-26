package net.jfabricationgames.gdx.screen.menu.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jfabricationgames.gdx.screen.menu.InGameMenuScreen;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class GameControlsDialog extends InGameMenuDialog {
	
	public GameControlsDialog(OrthographicCamera camera) {
		super(camera);
		
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED).setPosition(935, 670).setSize(110, 40).build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 20, -20, 1150, 800);
			banner.draw(batch, 80, 595, 400, 200);
			buttonBackToMenu.draw(batch);
			
			drawText();
			
			batch.end();
		}
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Controls", 155, 711);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(InGameMenuScreen.TEXT_COLOR_ENCODING_FOCUS + "Back", 970, 713);
		
		float headlineY = 650;
		float textColOneX = 100;
		float textColTwoX = 440;
		float textColThreeX = 790;
		screenTextWriter.setScale(1.2f);
		screenTextWriter.setColor(Color.BROWN);
		screenTextWriter.drawText("Action", textColOneX, headlineY);
		screenTextWriter.drawText("Keyboard", textColTwoX, headlineY);
		screenTextWriter.drawText("Controller", textColThreeX, headlineY);
		
		float textRowOneY = 590;
		float textRowOffset = 50;
		screenTextWriter.setScale(1f);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText("Move Up", textColOneX, textRowOneY);
		screenTextWriter.drawText("Move Down", textColOneX, textRowOneY - 1f * textRowOffset);
		screenTextWriter.drawText("Move Left", textColOneX, textRowOneY - 2f * textRowOffset);
		screenTextWriter.drawText("Move Right", textColOneX, textRowOneY - 3f * textRowOffset);
		screenTextWriter.drawText("Special", textColOneX, textRowOneY - 4f * textRowOffset);
		screenTextWriter.drawText("Interact", textColOneX, textRowOneY - 5f * textRowOffset);
		screenTextWriter.drawText("Attack", textColOneX, textRowOneY - 6f * textRowOffset);
		screenTextWriter.drawText("Block", textColOneX, textRowOneY - 7f * textRowOffset);
		screenTextWriter.drawText("Change Item", textColOneX, textRowOneY - 8f * textRowOffset);
		screenTextWriter.drawText("Menu", textColOneX, textRowOneY - 9f * textRowOffset);
		
		screenTextWriter.drawText("W", textColTwoX, textRowOneY);
		screenTextWriter.drawText("S", textColTwoX, textRowOneY - 1f * textRowOffset);
		screenTextWriter.drawText("A", textColTwoX, textRowOneY - 2f * textRowOffset);
		screenTextWriter.drawText("D", textColTwoX, textRowOneY - 3f * textRowOffset);
		screenTextWriter.drawText("Space", textColTwoX, textRowOneY - 4f * textRowOffset);
		screenTextWriter.drawText("E", textColTwoX, textRowOneY - 5f * textRowOffset);
		screenTextWriter.drawText("Mouse Left", textColTwoX, textRowOneY - 6f * textRowOffset);
		screenTextWriter.drawText("Mouse Right", textColTwoX, textRowOneY - 7f * textRowOffset);
		screenTextWriter.drawText("R / T", textColTwoX, textRowOneY - 8f * textRowOffset);
		screenTextWriter.drawText("Esc", textColTwoX, textRowOneY - 9f * textRowOffset);
		
		screenTextWriter.drawText("UP", textColThreeX, textRowOneY);
		screenTextWriter.drawText("DOWN", textColThreeX, textRowOneY - 1f * textRowOffset);
		screenTextWriter.drawText("LEFT", textColThreeX, textRowOneY - 2f * textRowOffset);
		screenTextWriter.drawText("RIGHT", textColThreeX, textRowOneY - 3f * textRowOffset);
		screenTextWriter.drawText("X", textColThreeX, textRowOneY - 4f * textRowOffset);
		screenTextWriter.drawText("Y", textColThreeX, textRowOneY - 5f * textRowOffset);
		screenTextWriter.drawText("B / RT", textColThreeX, textRowOneY - 6f * textRowOffset);
		screenTextWriter.drawText("A / LT", textColThreeX, textRowOneY - 7f * textRowOffset);
		screenTextWriter.drawText("LB / RB", textColThreeX, textRowOneY - 8f * textRowOffset);
		screenTextWriter.drawText("START", textColThreeX, textRowOneY - 9f * textRowOffset);
	}
}
