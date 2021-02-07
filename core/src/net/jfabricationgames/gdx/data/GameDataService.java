package net.jfabricationgames.gdx.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.data.container.GameDataContainer;

public class GameDataService {
	
	public static final String GAME_DATA_SAVE_DIRECTORY = ".DwarfScrollerGDX/saves/";
	public static final String GAME_DATA_SAVE_FILENAME_QUICKSAVE = "quicksave.json";
	public static final String GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER = "<index>";
	public static final String GAME_DATA_SAVE_FILENAME = "save_" + GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER + ".json";
	
	public void storeGameDataToQuickSaveSlot() {
		store(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	public void storeGameDataToSaveSlot(int slot) {
		store(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	private void store(String fileName) {
		Gdx.app.log(getClass().getSimpleName(), "saving game to file: " + fileName);
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		
		GameDataContainer gameData = GameDataHandler.getInstance().getGameData();
		Json json = new Json();
		String serializedGameData = json.prettyPrint(gameData);
		
		fileHandle.writeString(serializedGameData, false);
	}
	
	public void loadGameDataFromQuicksaveSlot() {
		loadGameData(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	public void loadGameDataFromSaveSlot(int slot) {
		loadGameData(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	private void loadGameData(String fileName) {
		Gdx.app.log(getClass().getSimpleName(), "loading game from file: " + fileName);
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		
		Json json = new Json();
		GameDataContainer gameData = json.fromJson(GameDataContainer.class, fileHandle);
		
		GameDataHandler.getInstance().updateData(gameData);
	}
	
	public boolean isGameDataSlotExisting(int slot) {
		return isGameDataSlotExisting(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	public boolean isQuickSaveGameDataSlotExisting() {
		return isGameDataSlotExisting(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	private boolean isGameDataSlotExisting(String fileName) {
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		return fileHandle.exists();
	}
	
	public String getSaveDateAsString(int slot) {
		return formattDate(getSaveDate(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot))));
	}
	
	public String getQuickSaveDateAsString() {
		return formattDate(getSaveDate(GAME_DATA_SAVE_FILENAME_QUICKSAVE));
	}
	
	private String formattDate(LocalDateTime date) {
		if (date == null) {
			return "---";
		}
		
		return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm"));
	}
	
	public LocalDateTime getSaveDate(int slot) {
		return getSaveDate(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	public LocalDateTime getQuickSaveDate() {
		return getSaveDate(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	private LocalDateTime getSaveDate(String fileName) {
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		if (!fileHandle.exists()) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(fileHandle.lastModified()), TimeZone.getDefault().toZoneId());
	}
}
