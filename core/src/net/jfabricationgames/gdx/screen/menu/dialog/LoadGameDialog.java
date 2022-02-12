package net.jfabricationgames.gdx.screen.menu.dialog;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.screen.game.GameScreen;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.control.ControlledMenu;

public class LoadGameDialog extends GameDataServiceDialog {
	
	public LoadGameDialog(OrthographicCamera camera, Runnable backToGame, Consumer<String> playMenuSoundConsumer) {
		super(camera, backToGame, playMenuSoundConsumer);
	}
	
	@Override
	protected String getMenuTitle() {
		return "Load Game";
	}
	
	@Override
	protected String getButtonText() {
		return "Load";
	}
	
	@Override
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		switch (stateName) {
			case "loadDialog_button_loadGameDialogBack":
				button = buttonBackToMenu;
				break;
			case "loadDialog_button_quickSaveSlot":
				button = buttonQuickSlot;
				break;
			case "loadDialog_button_saveSlot1":
				button = buttonsSlot[0];
				break;
			case "loadDialog_button_saveSlot2":
				button = buttonsSlot[1];
				break;
			case "loadDialog_button_saveSlot3":
				button = buttonsSlot[2];
				break;
			case "loadDialog_button_saveSlot4":
				button = buttonsSlot[3];
				break;
			case "loadDialog_button_saveSlot5":
				button = buttonsSlot[4];
				break;
			default:
				throw new IllegalStateException("Unexpected state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
	}
	
	public void loadFromQuickSaveSlot() {
		Gdx.app.log(getClass().getSimpleName(), "'loadFromQuickSaveSlot' selected");
		if (gameDataService.isQuickSaveGameDataSlotExisting()) {
			gameDataService.loadGameDataFromQuicksaveSlot();
			loadGame();
		}
		else {
			playMenuSound(ControlledMenu.SOUND_ERROR);
		}
	}
	
	public void loadFromSlot(int slot) {
		Gdx.app.log(getClass().getSimpleName(), "'loadFromSlot' " + slot + " selected");
		if (gameDataService.isGameDataSlotExisting(slot)) {
			gameDataService.loadGameDataFromSaveSlot(slot);
			loadGame();
		}
		else {
			playMenuSound(ControlledMenu.SOUND_ERROR);
		}
	}

	private void loadGame() {
		GameScreen.loadAndShowGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
			backToGame();
		});
	}
}
