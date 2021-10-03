package net.jfabricationgames.gdx.rune;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.map.MapObjectType;

public enum RuneType {
	
	OTHALA(0, "rune_collected__othala", "OTHALA - carry and use items", null), //
	ANSUZ(1, "rune_collected__ansuz", "ANSUZ - solve puzzles", null), //
	RAIDHO(2, "rune_collected__raidho", "RAIDHO - fast travel using the map", null), //
	GEBO(3, "rune_collected__gebo", "GEBO - sacrifice gold to buy supplies", null), //
	HAGALAZ(4, "rune_collected__hagalaz", "HAGALAZ - prevent death",
			() -> GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED, true)), //
	ALGIZ(5, "rune_collected__algiz", "ALGIZ - see hidden paths",
			() -> GameMapManager.getInstance().getMap().removePhysicsObjectsWithType(MapObjectType.INVISIBLE_PATH_BLOCKER)), //
	MANNAZ(6, "rune_collected__mannaz", "MANNAZ - reflect magical attacks", null), //
	KENAZ(7, "rune_collected__kenaz", "KENAZ - prevent from freezing colds", null), //
	LAGUZ(8, "rune_collected__laguz", "LAGUZ - open the last door to yggdrasiel", null); //
	
	public static final String GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED = "rune_forged__hagalaz";
	public static final String RUNE_HAGALAZ_DESCRIPTION_POSTFIX_FORGED = " (reforge after use)";
	public static final String RUNE_HAGALAZ_DESCRIPTION_POSTFIX_UNFORGED = " (needs to be reforged)";
	
	public static Array<String> getNamesAsList() {
		Array<String> names = new Array<>(values().length);
		
		for (RuneType rune : values()) {
			names.add(rune.name().toLowerCase());
		}
		
		return names;
	}
	
	public static RuneType getByOrder(int order) {
		for (RuneType type : values()) {
			if (type.order == order) {
				return type;
			}
		}
		throw new IllegalStateException("No rune found for searched order: " + order);
	}
	
	public static RuneType getByContainingName(String runeName) {
		for (RuneType type : values()) {
			if (runeName.toUpperCase().contains(type.name())) {
				return type;
			}
		}
		throw new IllegalStateException("No rune name contained in search string: " + runeName);
	}
	
	public final int order;
	public final String description;
	
	private final String globalValueKeyCollected;
	private final Runnable onPickUp;
	
	private RuneType(int order, String globalValueKey, String description, Runnable onPickUp) {
		this.order = order;
		this.globalValueKeyCollected = globalValueKey;
		this.description = description;
		this.onPickUp = onPickUp;
	}
	
	public boolean isCollected() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(globalValueKeyCollected);
	}
	
	public void runeItemPickedUp() {
		GlobalValuesDataHandler.getInstance().put(globalValueKeyCollected, true);
		
		if (onPickUp != null) {
			onPickUp.run();
		}
	}
}