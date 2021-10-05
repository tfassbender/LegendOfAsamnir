package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.CharacterItemContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.handler.type.DataItem;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;

public class CharacterKeyDataHandler implements DataHandler {
	
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
	
	public void addKey(DataItem item) {
		KeyItemProperties key = new KeyItemProperties(item.getKeyProperties());
		properties.keys.add(key);
		
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
