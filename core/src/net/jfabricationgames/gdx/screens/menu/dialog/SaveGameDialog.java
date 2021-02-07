package net.jfabricationgames.gdx.screens.menu.dialog;

import com.badlogic.gdx.graphics.Color;

import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;

public class SaveGameDialog extends InGameMenuDialog {
	
	private static final int SAVE_SLOTS = 5;
	
	private GameDataService gameDataService;
	
	private FocusButton buttonQuickSave;
	private FocusButton[] buttonsSaveToSlot;
	
	public SaveGameDialog() {
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
		
		buttonQuickSave = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(saveButtonX, saveButtonOneY) //
				.setSize(saveButtonWidth, saveButtonHeight) //
				.build();
		buttonQuickSave.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		buttonsSaveToSlot = new FocusButton[SAVE_SLOTS];
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSaveToSlot[i] = new FocusButtonBuilder() //
					.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
					.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
					.setPosition(saveButtonX, saveButtonOneY - (i + 1) * saveButtonRowOffset - saveButtonRowOffsetFirstLine) //
					.setSize(saveButtonWidth, saveButtonHeight) //
					.build();
			buttonsSaveToSlot[i].scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
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
	
	private void drawText() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Save Game", 155, 594);
		
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
			String slotText = "Save Slot " + (i + 1) + (gameDataService.isGameDataSlotExisting(i+1) ? "" : " <empty>");
			screenTextWriter.drawText(slotText, textColOneX, textRowOneY - (i + 1) * textRowOffset - textRowOffsetFirstLine);
			screenTextWriter.drawText(gameDataService.getSaveDateAsString(i+1), textColTwoX,
					textRowOneY - (i + 1) * textRowOffset - textRowOffsetFirstLine);
		}
		
		screenTextWriter.setScale(0.6f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuickSave) + "Save", 1000, 433);
		
		for (int i = 0; i < SAVE_SLOTS; i++) {
			screenTextWriter.drawText(getButtonTextColorEncoding(buttonsSaveToSlot[i]) + "Save", 1000,
					433 - (i + 1) * textRowOffset - textRowOffsetFirstLine);
		}
	}
	
	private void drawButtons() {
		buttonQuickSave.draw(batch);
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSaveToSlot[i].draw(batch);
		}
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "saveDialog_button_saveGameDialogBack":
				button = buttonBackToMenu;
				break;
			case "saveDialog_button_quickSave":
				button = buttonQuickSave;
				break;
			case "saveDialog_button_saveSlot1":
				button = buttonsSaveToSlot[0];
				break;
			case "saveDialog_button_saveSlot2":
				button = buttonsSaveToSlot[1];
				break;
			case "saveDialog_button_saveSlot3":
				button = buttonsSaveToSlot[2];
				break;
			case "saveDialog_button_saveSlot4":
				button = buttonsSaveToSlot[3];
				break;
			case "saveDialog_button_saveSlot5":
				button = buttonsSaveToSlot[4];
				break;
			default:
				throw new IllegalStateException("Unexpected state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
	}
	
	private void unfocusAll() {
		buttonBackToMenu.setFocused(false);
		buttonQuickSave.setFocused(false);
		for (int i = 0; i < SAVE_SLOTS; i++) {
			buttonsSaveToSlot[i].setFocused(false);
		}
	}
	
	public void quickSave() {
		gameDataService.storeGameDataToQuickSaveSlot();
	}
	
	public void saveToSlot(int slot) {
		gameDataService.storeGameDataToSaveSlot(slot);
	}
}
