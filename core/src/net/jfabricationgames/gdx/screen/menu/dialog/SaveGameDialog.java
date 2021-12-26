package net.jfabricationgames.gdx.screen.menu.dialog;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jfabricationgames.gdx.screen.menu.components.FocusButton;

public class SaveGameDialog extends GameDataServiceDialog {
	
	public SaveGameDialog(OrthographicCamera camera, Runnable backToGame, Consumer<String> playMenuSoundConsumer) {
		super(camera, backToGame, playMenuSoundConsumer);
	}
	
	@Override
	protected String getMenuTitle() {
		return "Save Game";
	}
	
	@Override
	protected String getButtonText() {
		return "Save";
	}
	
	@Override
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "saveDialog_button_saveGameDialogBack":
				button = buttonBackToMenu;
				break;
			case "saveDialog_button_quickSave":
				button = buttonQuickSlot;
				break;
			case "saveDialog_button_saveSlot1":
				button = buttonsSlot[0];
				break;
			case "saveDialog_button_saveSlot2":
				button = buttonsSlot[1];
				break;
			case "saveDialog_button_saveSlot3":
				button = buttonsSlot[2];
				break;
			case "saveDialog_button_saveSlot4":
				button = buttonsSlot[3];
				break;
			case "saveDialog_button_saveSlot5":
				button = buttonsSlot[4];
				break;
			default:
				throw new IllegalStateException("Unexpected state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
	}
	
	public void quickSave() {
		Gdx.app.log(getClass().getSimpleName(), "'quickSave' selected");
		gameDataService.storeGameDataToQuickSaveSlot();
		backToGame();
	}
	
	public void saveToSlot(int slot) {
		Gdx.app.log(getClass().getSimpleName(), "'saveToSlot' " + slot + " selected");
		gameDataService.storeGameDataToSaveSlot(slot);
		backToGame();
	}
}
