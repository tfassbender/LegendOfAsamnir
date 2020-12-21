package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.container.CharacterItemContainer;
import net.jfabricationgames.gdx.character.container.data.KeyItem;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class LockedObject extends InteractiveObject {
	
	private static final String MAP_PROPERTY_KEY_LOCKED = "locked";
	
	private static final String LOCK_MESSAGE_HEADER = "Locked";
	private static final String LOCK_MESSAGE_TEXT_SIMPLE_KEY = "I'll need a key to unlock this.";
	private static final String LOCK_MESSAGE_TEXT_SPECIAL_KEY = "I'll need a special key to unlock this.";
	
	private ObjectMap<String, String> keyProperties;
	
	public LockedObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
		
		keyProperties = KeyItem.getKeyProperties(properties);
	}
	
	@Override
	protected boolean canBeExecuted(CharacterItemContainer itemContainer) {
		if (!super.canBeExecuted(itemContainer)) {
			return false;
		}
		if (!typeConfig.defaultLocked && !lockedByMapProperty()) {
			return true;
		}
		if (canBeUnlocked(itemContainer)) {
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
	
	private boolean canBeUnlocked(CharacterItemContainer itemContainer) {
		if (itemContainer.containsKey(keyProperties)) {
			itemContainer.takeKey(keyProperties);
			return true;
		}
		
		return false;
	}
	
	private void showLockMessage() {
		OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
		String messageText;
		
		if (KeyItem.isSpecialKey(keyProperties)) {
			messageText = LOCK_MESSAGE_TEXT_SPECIAL_KEY + '\n' + KeyItem.getSpecialKeyPropertiesAsString(keyProperties);
		}
		else {
			messageText = LOCK_MESSAGE_TEXT_SIMPLE_KEY;
		}
		
		onScreenTextBox.setHeaderText(LOCK_MESSAGE_HEADER);
		onScreenTextBox.setText(messageText);
	}
}
