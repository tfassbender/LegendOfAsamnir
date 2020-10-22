package net.jfabricationgames.gdx.character.container;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.container.data.CharacterItemProperties;
import net.jfabricationgames.gdx.character.container.data.KeyItem;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.item.Item;

public class CharacterKeyContainer {
	
	private static final String SPECIAL_KEY_MESSAGE_HEADER = "Special Key";
	
	private CharacterItemProperties properties;
	private int numNormalKeys = 0;
	
	public CharacterKeyContainer(CharacterItemProperties properties) {
		this.properties = properties;
	}
	
	public void addKey(Item item) {
		KeyItem key = new KeyItem(item.getKeyProperties());
		properties.keys.add(key);
		
		if (KeyItem.isSpecialKey(key.mapProperties)) {
			displaySpecialKeyProperties(key);
		}
		
		countKeys();
	}

	private void countKeys() {
		numNormalKeys = 0;
		for (KeyItem key : properties.keys) {
			if (!key.isSpecialKey()) {
				numNormalKeys++;
			}
		}
	}
	
	public int getNumNormalKeys() {
		return numNormalKeys;
	}
	
	private void displaySpecialKeyProperties(KeyItem key) {
		OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
		onScreenTextBox.setHeaderText(SPECIAL_KEY_MESSAGE_HEADER);
		onScreenTextBox.setText(KeyItem.getSpecialKeyPropertiesAsString(key.mapProperties));
	}
	
	public boolean containsKey(ObjectMap<String, String> keyProperties) {
		return getKeyItem(keyProperties) != null;
	}
	
	public void takeKey(ObjectMap<String, String> keyProperties) {
		KeyItem keyItem = getKeyItem(keyProperties);
		if (keyItem == null) {
			throw new IllegalStateException("The required key was not found. Required Properties: " + keyProperties);
		}
		
		properties.keys.removeValue(keyItem, true);
		
		countKeys();
	}
	
	private KeyItem getKeyItem(ObjectMap<String, String> requiredProperties) {
		for (KeyItem keyItem : properties.keys) {
			ObjectMap<String, String> keyProperties = keyItem.mapProperties;
			if (keyProperties.size == requiredProperties.size) {
				boolean propertiesMatch = true;
				for (String propertyKey : keyProperties.keys()) {
					String propertyValue = keyProperties.get(propertyKey);
					String requiredValue = requiredProperties.get(propertyKey);
					
					propertiesMatch &= propertyValue.equals(requiredValue);
				}
				
				if (propertiesMatch) {
					return keyItem;
				}
			}
		}
		
		return null;
	}
}
