package net.jfabricationgames.gdx.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.state.BeforePersistState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.util.AnnotationUtil;

public class GameDataService implements EventListener {
	
	public static final String GAME_DATA_SAVE_DIRECTORY = ".DwarfScrollerGDX/saves/";
	public static final String GAME_DATA_SAVE_FILENAME_QUICKSAVE = "quicksave.json";
	public static final String GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER = "<index>";
	public static final String GAME_DATA_SAVE_FILENAME = "save_" + GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER + ".json";
	
	public static void fireQuickSaveEvent() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.QUICKSAVE));
	}
	
	public static void initializeEventListener() {
		GameDataService service = new GameDataService();
		EventHandler.getInstance().registerEventListener(service);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.QUICKSAVE) {
			storeGameDataToQuickSaveSlot();
		}
	}
	
	public void storeGameDataToQuickSaveSlot() {
		store(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	public void storeGameDataToSaveSlot(int slot) {
		store(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	private void store(String fileName) {
		Gdx.app.log(getClass().getSimpleName(), "saving game to file: " + fileName);
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		
		executeAnnotatedMethodsBeforePersisting();
		GameDataContainer gameData = GameDataHandler.getInstance().getGameData();
		Json json = new Json(OutputType.json);
		json.setUsePrototypes(false);
		String serializedGameData = json.prettyPrint(gameData);
		
		fileHandle.writeString(serializedGameData, false);
	}
	
	private void executeAnnotatedMethodsBeforePersisting() {
		AnnotationUtil.executeAnnotatedMethods(BeforePersistState.class, GameMap.getInstance());
		AnnotationUtil.executeAnnotatedMethods(BeforePersistState.class, GameMap.getInstance().getPlayer());
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
		GameMap.getInstance().updateAfterLoadingGameState();
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
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
