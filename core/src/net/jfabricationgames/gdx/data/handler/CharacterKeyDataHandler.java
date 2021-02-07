package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.CharacterItemContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.item.Item;

public class CharacterKeyDataHandler implements DataHandler {
	
	private static final String SPECIAL_KEY_MESSAGE_HEADER = "Special Key";
	
	private static CharacterKeyDataHandler instance;
	
	public static synchronized CharacterKeyDataHandler getInstance() {
		if (instance == null) {
			instance = new CharacterKeyDataHandler();
		}
		return instance;
	}
	
	private CharacterItemContainer properties;
	
	private CharacterKeyDataHandler() {}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.itemDataContainer;
	}
	
	public void addKey(Item item) {
		KeyItemProperties key = new KeyItemProperties(item.getKeyProperties());
		properties.keys.add(key);
		
		if (KeyItemProperties.isSpecialKey(key.mapProperties)) {
			displaySpecialKeyProperties(key);
		}
		
		countKeys();
	}
	
	private void countKeys() {
		properties.numNormalKeys = 0;
		for (KeyItemProperties key : properties.keys) {
			if (!key.isSpecialKey()) {
				properties.numNormalKeys++;
			}
		}
	}
	
	public int getNumNormalKeys() {
		return properties.numNormalKeys;
	}
	
	private void displaySpecialKeyProperties(KeyItemProperties key) {
		OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
		onScreenTextBox.setHeaderText(SPECIAL_KEY_MESSAGE_HEADER);
		onScreenTextBox.setText(KeyItemProperties.getSpecialKeyPropertiesAsString(key.mapProperties));
	}
	
	public boolean containsKey(ObjectMap<String, String> keyProperties) {
		return getKeyItem(keyProperties) != null;
	}
	
	public void takeKey(ObjectMap<String, String> keyProperties) {
		KeyItemProperties keyItem = getKeyItem(keyProperties);
		if (keyItem == null) {
			throw new IllegalStateException("The required key was not found. Required Properties: " + keyProperties);
		}
		
		properties.keys.removeValue(keyItem, true);
		
		countKeys();
	}
	
	private KeyItemProperties getKeyItem(ObjectMap<String, String> requiredProperties) {
		for (KeyItemProperties keyItem : properties.keys) {
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
