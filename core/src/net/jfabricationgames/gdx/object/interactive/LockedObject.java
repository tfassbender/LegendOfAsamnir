package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class LockedObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_LOCKED = "locked";
	private static final String MAP_PROPERTY_KEY_UNLOCKED_BY_EVENT = "unlockedByEvent";
	private static final String MAP_PROPERTY_KEY_LOCK_ID = "lockId";
	private static final String MAP_PROPERTY_KEY_UNLOCK_CONDITION = "unlockCondition";
	
	private static final String LOCK_MESSAGE_HEADER = "Locked";
	private static final String LOCK_MESSAGE_TEXT_SIMPLE_KEY = "I'll need a key to unlock this.";
	private static final String LOCK_MESSAGE_TEXT_SPECIAL_KEY = "This one looks different. I'll need a special key to unlock this.";
	private static final String LOCK_MESSAGE_TEXT_UNLOCKED_BY_EVENT_OR_CONDITION = "This does not seem to be opened with a key. There must be another way.";
	
	private ObjectMap<String, String> keyProperties;
	
	public LockedObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		
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
		return Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_LOCKED, "false", String.class)) //
				|| isUnlockedByCondition();
	}
	
	private boolean canBeUnlocked() {
		if (isUnlockedByEvent()) {
			return false;
		}
		
		if (isUnlockedByCondition()) {
			return isUnlockConditionMet();
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
	
	private boolean isUnlockedByCondition() {
		return mapProperties.get(MAP_PROPERTY_KEY_UNLOCK_CONDITION, String.class) != null;
	}
	
	private boolean isUnlockConditionMet() {
		String conditionId = mapProperties.get(MAP_PROPERTY_KEY_UNLOCK_CONDITION, String.class);
		return ConditionHandler.getInstance().isConditionMet(conditionId);
	}
	
	private void showLockMessage() {
		String messageText;
		
		if (isUnlockedByEvent() || isUnlockedByCondition()) {
			messageText = LOCK_MESSAGE_TEXT_UNLOCKED_BY_EVENT_OR_CONDITION;
		}
		else {
			if (KeyItemProperties.isSpecialKey(keyProperties)) {
				messageText = LOCK_MESSAGE_TEXT_SPECIAL_KEY;
			}
			else {
				messageText = LOCK_MESSAGE_TEXT_SIMPLE_KEY;
			}
		}
		
		textBox.setHeaderText(LOCK_MESSAGE_HEADER);
		textBox.setText(messageText);
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
	
	@Override
	protected void executeInteraction() {
		float animationTime = 0;
		boolean setAnimationTime = false;
		if (animation != null) {
			setAnimationTime = true;
			animationTime = animation.getStateTime();
		}
		
		super.executeInteraction();
		
		if (setAnimationTime) {
			animation.setStateTime(animation.getAnimationDuration() - animationTime);
		}
	}
	
	private void reverseInteraction() {
		actionExecuted = false;
		
		float animationTime = animation.getStateTime();
		if (animation != null) {
			animationTime = animation.getStateTime();
		}
		
		if (typeConfig.animationActionReversed != null) {
			animation = getReversedActionAnimation();
		}
		animation.setStateTime(animation.getAnimationDuration() - animationTime);
		
		if (typeConfig.textureAfterAction != null) {
			sprite = createSprite(typeConfig.texture);
		}
		
		changeBodyToNonSensor();
		changedBodyToSensor = false;
		
		playInteractionSound();
		
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private AnimationDirector<TextureRegion> getReversedActionAnimation() {
		return getAnimation(typeConfig.animationActionReversed);
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
}
