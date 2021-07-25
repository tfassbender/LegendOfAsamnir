package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class RuneItem extends Item {
	
	public static enum RuneType {
		
		OTHALA(0, "rune_collected__othala", "OTHALA - carry and use items"), //
		ANSUZ(1, "rune_collected__ansuz", "ANSUZ - solve puzzles"), //
		RAIDHO(2, "rune_collected__raidho", "RAIDHO - fast travel using the map"), //
		GEBO(3, "rune_collected__gebo", "GEBO - sacrifice gold to buy supplies"), //
		HAGALAZ(4, "rune_collected__hagalaz", "HAGALAZ - prevent death"), //
		ALGIZ(5, "rune_collected__algiz", "ALGIZ - see hidden paths"), //
		MANNAZ(6, "rune_collected__mannaz", "MANNAZ - reflect magical attacks"), //
		KENAZ(7, "rune_collected__kenaz", "KENAZ - prevent from freezing colds"), //
		LAGUZ(8, "rune_collected__laguz", "LAGUZ - open the last door to yggdrasiel"); //
		
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
		
		private static RuneType getByContainingName(String itemName) {
			for (RuneType type : values()) {
				if (itemName.toUpperCase().contains(type.name())) {
					return type;
				}
			}
			throw new IllegalStateException("No rune name contained in search string: " + itemName);
		}
		
		public final int order;
		public final String globalValueKey;
		public final String description;
		
		private RuneType(int order, String globalValueKey, String description) {
			this.order = order;
			this.globalValueKey = globalValueKey;
			this.description = description;
		}
		
		public boolean isCollected() {
			return GlobalValuesDataHandler.getInstance().isValueEqual(globalValueKey, "true");
		}
	}
	
	private RuneType type;
	
	public RuneItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		type = RuneType.getByContainingName(itemName);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN * 0.25f);//scale the items down, since the textures are larger than usual
	}
	
	@Override
	public void pickUp() {
		super.pickUp();
		GlobalValuesDataHandler.getInstance().put(type.globalValueKey, "true");
		
		if (type == RuneType.HAGALAZ) {
			GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED, "true");
		}
	}
}
