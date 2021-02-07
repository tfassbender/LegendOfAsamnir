package net.jfabricationgames.gdx.screens.menu.dialog;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;

import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;

public abstract class GameDataServiceDialog extends InGameMenuDialog {
	
	private static final int SAVE_SLOTS = 5;
	
	protected GameDataService gameDataService;
	
	protected FocusButton buttonQuickSlot;
	protected FocusButton[] buttonsSlot;
	
	private Runnable backToGame;
	private Consumer<String> playMenuSoundConsumer;
	
	public GameDataServiceDialog(Runnable backToGame, Consumer<String> playMenuSoundConsumer) {
		this.backToGame = backToGame;
		this.playMenuSoundConsumer = playMenuSoundConsumer;
		gameDataService = new GameDataService();
		createControls();
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(935, 550) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
		
		float saveButtonX = 980;
		float saveButtonOneY = 400;
		float saveButtonRowOffset = 50;
		float saveButtonRowOffsetFirstLine = 30;
		float saveButtonWidth = 70;
		float saveButtonHeight = 30;
		
		buttonQuickSlot = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(saveButtonX, saveButtonOneY) //
				.setSize(saveButtonWidth, saveButtonHeight) //
				.build();
		buttonQuickSlot.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonsSlot = new FocusButton[SAVE_SLOTS];
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSlot[i] = new FocusButtonBuilder() //
					.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
					.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
					.setPosition(saveButtonX, saveButtonOneY - (i + 1) * saveButtonRowOffset - saveButtonRowOffsetFirstLine) //
					.setSize(saveButtonWidth, saveButtonHeight) //
					.build();
			buttonsSlot[i].scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		}
	}
	
	public void draw() {
		if (visible) {
			batch.begin();
			
			background.draw(batch, 40, 50, 1130, 570);
			banner.draw(batch, 80, 480, 460, 200);
			buttonBackToMenu.draw(batch);
			
			drawButtons();
			drawText();
			
			batch.end();
		}
	}
	
	private void drawButtons() {
		buttonQuickSlot.draw(batch);
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSlot[i].draw(batch);
		}
	}
	
	private void drawText() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText(getMenuTitle(), 155, 594);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 970, 593);
		
		float headlineY = 510;
		float textColOneX = 130;
		float textColTwoX = 660;
		screenTextWriter.setScale(1.2f);
		screenTextWriter.setColor(Color.BROWN);
		screenTextWriter.drawText("Saved Games", textColOneX, headlineY);
		screenTextWriter.drawText("Save Date", textColTwoX, headlineY);
		
		float textRowOneY = 440;
		float textRowOffset = 50;
		float textRowOffsetFirstLine = 30;
		screenTextWriter.setScale(1f);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText("Quicksave", textColOneX, textRowOneY);
		screenTextWriter.drawText(gameDataService.getQuickSaveDateAsString(), textColTwoX, textRowOneY);
		
		for (int i = 0; i < SAVE_SLOTS; i++) {
			String slotText = "Save Slot " + (i + 1) + (gameDataService.isGameDataSlotExisting(i + 1) ? "" : " <empty>");
			screenTextWriter.drawText(slotText, textColOneX, textRowOneY - (i + 1) * textRowOffset - textRowOffsetFirstLine);
			screenTextWriter.drawText(gameDataService.getSaveDateAsString(i + 1), textColTwoX,
					textRowOneY - (i + 1) * textRowOffset - textRowOffsetFirstLine);
		}
		
		screenTextWriter.setScale(0.6f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuickSlot) + getButtonText(), 1000, 433);
		
		for (int i = 0; i < SAVE_SLOTS; i++) {
			screenTextWriter.drawText(getButtonTextColorEncoding(buttonsSlot[i]) + getButtonText(), 1000,
					433 - (i + 1) * textRowOffset - textRowOffsetFirstLine);
		}
	}
	
	protected abstract String getMenuTitle();
	
	protected abstract String getButtonText();
	
	public abstract void setFocusTo(String stateName);
	
	protected void unfocusAll() {
		buttonBackToMenu.setFocused(false);
		buttonQuickSlot.setFocused(false);
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSlot[i].setFocused(false);
		}
	}
	
	protected void backToGame() {
		backToGame.run();
	}
	
	protected void playMenuSound(String sound) {
		playMenuSoundConsumer.accept(sound);
	}
}
