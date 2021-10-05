package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class BuyableItem extends Item {
	
	public BuyableItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation,
			MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
	}
	
	@Override
	public boolean canBePicked() {
		if (!super.canBePicked()) {
			return false;
		}
		return Player.getInstance().getCoins() >= typeConfig.costs;
	}
	
	@Override
	public void pickUp() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TAKE_PLAYERS_COINS).setIntValue(typeConfig.costs));
		super.pickUp();
	}
}
