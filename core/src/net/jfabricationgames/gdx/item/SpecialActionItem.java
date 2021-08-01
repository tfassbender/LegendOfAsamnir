package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.rune.RuneType;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class SpecialActionItem extends Item {
	
	private SpecialAction action;
	
	public SpecialActionItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation,
			MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		this.action = SpecialAction.getByContainingName(itemName);
		
		if (action == SpecialAction.FEATHER || action == SpecialAction.LANTERN) {
			//scale down the images, since these images are larger than the other
			sprite.setScale(GameScreen.WORLD_TO_SCREEN * 0.65f);
		}
	}
	
	@Override
	public void pickUp() {
		if (canUseSpecialActions()) {
			super.pickUp();
			GlobalValuesDataHandler.getInstance().put(action.actionEnabledGlobalValueKey, true);
		}
	}
	
	private boolean canUseSpecialActions() {
		return RuneType.OTHALA.isCollected();
	}
}
