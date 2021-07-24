package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class RuneItem extends Item {
	
	public static enum RuneType {
		
		OTHALA(1, "rune_collected__othala"),   //possession     - enables to carry and use items
		ANSUZ(2, "rune_collected__ansuz"),     //mystery        - solve puzzles by using buttons, switches, ...
		RAIDHO(3, "rune_collected__raidho"),   //wheel          - fast travel
		GEBO(4, "rune_collected__gebo"),       //gift           - sacrifice gold to open doors
		ALGIZ(5, "rune_collected__algiz"),     //swan           - enables to see invisible paths
		MANNAZ(6, "rune_collected__mannaz"),   //mirror         - the shield reflects some magical attacks
		HAGALAZ(7, "rune_collected__hagalaz"), //hail           - revived when died (needs to be re-forged afterwards)
		KENAZ(8, "rune_collected__kenaz"),     //fire           - no continuous damage in the cold dungeon
		LAGUZ(9, "rune_collected__laguz");     //primal water   - opens the last door in yggdrasiel
		
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
		
		private RuneType(int order, String globalValueKey) {
			this.order = order;
			this.globalValueKey = globalValueKey;
		}
		
		public boolean isCollected() {
			return GlobalValuesDataHandler.getInstance().isValueEqual(globalValueKey, "true");
		}
	}
	
	private RuneType type;
	
	public RuneItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		type = RuneType.getByContainingName(itemName);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN * 0.3f);//scale the items down, since the textures are quite large
	}
	
	@Override
	public void pickUp() {
		super.pickUp();
		GlobalValuesDataHandler.getInstance().put(type.globalValueKey, "true");
	}
}
