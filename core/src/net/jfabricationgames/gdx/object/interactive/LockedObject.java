package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class LockedObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_LOCKED = "locked";
	private static final String MAP_PROPERTY_KEY_UNLOCKED_BY_EVENT = "unlockedByEvent";
	private static final String MAP_PROPERTY_KEY_LOCK_ID = "lockId";
	
	private static final String LOCK_MESSAGE_HEADER = "Locked";
	private static final String LOCK_MESSAGE_TEXT_SIMPLE_KEY = "I'll need a key to unlock this.";
	private static final String LOCK_MESSAGE_TEXT_SPECIAL_KEY = "I'll need a special key to unlock this.";
	private static final String LOCK_MESSAGE_TEXT_UNLOCKED_BY_EVENT = "This does not seem to be opened with a key. There must be another way.";
	
	private ObjectMap<String, String> keyProperties;
	
	public LockedObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
		
		keyProperties = KeyItemProperties.getKeyProperties(properties);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public boolean interactionCanBeExecuted() {
		if (!super.interactionCanBeExecuted()) {
			return false;
		}
		
		if (!typeConfig.defaultLocked && !lockedByMapProperty()) {
			return true;
		}
		if (canBeUnlocked()) {
			return true;
		}
		else {
			showLockMessage();
		}
		
		return false;
	}
	
	private boolean lockedByMapProperty() {
		return mapProperties.get(MAP_PROPERTY_KEY_LOCKED, false, Boolean.class);
	}
	
	private boolean canBeUnlocked() {
		if (isUnlockedByEvent()) {
			return false;
		}
		
		CharacterItemDataHandler itemContainer = CharacterItemDataHandler.getInstance();
		if (itemContainer.containsKey(keyProperties)) {
			itemContainer.takeKey(keyProperties);
			return true;
		}
		
		return false;
	}
	
	private boolean isUnlockedByEvent() {
		return Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_UNLOCKED_BY_EVENT, "false", String.class));
	}
	
	private void showLockMessage() {
		OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
		String messageText;
		
		if (isUnlockedByEvent()) {
			messageText = LOCK_MESSAGE_TEXT_UNLOCKED_BY_EVENT;
		}
		else {
			if (KeyItemProperties.isSpecialKey(keyProperties)) {
				messageText = LOCK_MESSAGE_TEXT_SPECIAL_KEY + '\n' + KeyItemProperties.getSpecialKeyPropertiesAsString(keyProperties);
			}
			else {
				messageText = LOCK_MESSAGE_TEXT_SIMPLE_KEY;
			}
		}
		
		onScreenTextBox.setHeaderText(LOCK_MESSAGE_HEADER);
		onScreenTextBox.setText(messageText);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.OPEN_LOCK) {
			if (isUnlockedByEvent() && event.stringValue.equals(mapProperties.get(MAP_PROPERTY_KEY_LOCK_ID))) {
				if (!actionExecuted) {
					executeInteraction();
				}
			}
		}
		else if (event.eventType == EventType.CLOSE_LOCK) {
			if (isUnlockedByEvent() && event.stringValue.equals(mapProperties.get(MAP_PROPERTY_KEY_LOCK_ID))) {
				if (actionExecuted) {
					reverseInteraction();
				}
			}
		}
	}
	
	private void reverseInteraction() {
		actionExecuted = false;
		if (typeConfig.animationActionReversed != null) {
			animation = getReversedActionAnimation();
		}
		
		if (typeConfig.textureAfterAction != null) {
			sprite = createSprite(typeConfig.texture);
		}
		
		changeBodyToNonSensor();
		changedBodyToSensor = false;
		
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private AnimationDirector<TextureRegion> getReversedActionAnimation() {
		return getAnimation(typeConfig.animationActionReversed);
	}
	
	@Override
	public void remove() {
		super.remove();
		EventHandler.getInstance().removeEventListener(this);
	}
}
