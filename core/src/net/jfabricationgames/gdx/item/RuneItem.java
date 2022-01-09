package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.rune.RuneType;

public class RuneItem extends Item {
	
	private RuneType type;
	
	public RuneItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		type = RuneType.getByContainingName(itemName);
		sprite.setScale(Constants.WORLD_TO_SCREEN * 0.25f);//scale the items down, since the textures are larger than usual
	}
	
	@Override
	public void pickUp() {
		super.pickUp();
		itemMap.processRunePickUp(type);
		GlobalValuesDataHandler.getInstance().put(type.globalValueKeyCollected, true);
		EventHandler.getInstance()
				.fireEvent(new EventConfig().setEventType(EventType.RUNE_FOUND).setStringValue(type.name()).setParameterObject(type));
	}
}
