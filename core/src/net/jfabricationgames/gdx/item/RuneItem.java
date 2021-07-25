package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class RuneItem extends Item {
	
	private RuneType type;
	
	public RuneItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		type = RuneType.getByContainingName(itemName);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN * 0.25f);//scale the items down, since the textures are larger than usual
	}
	
	@Override
	public void pickUp() {
		super.pickUp();
		GlobalValuesDataHandler.getInstance().put(type.globalValueKey, true);
		
		if (type == RuneType.HAGALAZ) {
			GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED, true);
		}
	}
}
