package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.container.CharacterItemContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.handler.type.DataItem;
import net.jfabricationgames.gdx.data.handler.type.DataItemAmmoType;
import net.jfabricationgames.gdx.data.handler.type.DataItemPropertyKeys;

public class CharacterItemDataHandler implements DataHandler {
	
	private static final String ITEM_NAME_KEY = "key";
	
	private static CharacterItemDataHandler instance;
	
	public static synchronized CharacterItemDataHandler getInstance() {
		if (instance == null) {
			instance = new CharacterItemDataHandler();
		}
		return instance;
	}
	
	private CharacterPropertiesDataHandler characterProperties;
	private CharacterKeyDataHandler characterKeyContainer;
	private CharacterItemContainer properties;
	
	private CharacterItemDataHandler() {
		characterProperties = CharacterPropertiesDataHandler.getInstance();
		characterKeyContainer = CharacterKeyDataHandler.getInstance();
	}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.itemDataContainer;
	}
	
	public void collectItem(DataItem item) {
		if (item.canBePicked()) {
			if (item.containsProperty(DataItemPropertyKeys.HEALTH.getPropertyName())) {
				float itemHealth = item.getProperty(DataItemPropertyKeys.HEALTH.getPropertyName(), Float.class);
				characterProperties.increaseHealth(itemHealth);
			}
			if (item.containsProperty(DataItemPropertyKeys.MANA.getPropertyName())) {
				float itemMana = item.getProperty(DataItemPropertyKeys.MANA.getPropertyName(), Float.class);
				characterProperties.increaseMana(itemMana);
			}
			if (item.containsProperty(DataItemPropertyKeys.ARMOR.getPropertyName())) {
				float itemArmor = item.getProperty(DataItemPropertyKeys.ARMOR.getPropertyName(), Float.class);
				characterProperties.increaseArmor(itemArmor);
			}
			if (item.containsProperty(DataItemPropertyKeys.AMMO.getPropertyName())) {
				int itemAmmo = item.getProperty(DataItemPropertyKeys.AMMO.getPropertyName(), Float.class).intValue();
				if (item.containsProperty(DataItemPropertyKeys.AMMO_TYPE.getPropertyName())) {
					DataItemAmmoType ammoType = DataItemAmmoType
							.getByNameIgnoreCase(item.getProperty(DataItemPropertyKeys.AMMO_TYPE.getPropertyName(), String.class));
					increaseAmmo(itemAmmo, ammoType);
				}
				else {
					throw new IllegalStateException("The ammo item has no ammo type defined. It should be added to default_values.json file.");
				}
			}
			if (item.getItemName().equals(ITEM_NAME_KEY)) {
				characterKeyContainer.addKey(item);
			}
			if (item.containsProperty(DataItemPropertyKeys.VALUE.getPropertyName())) {
				int itemValue = item.getProperty(DataItemPropertyKeys.VALUE.getPropertyName(), Float.class).intValue();
				characterProperties.increaseCoins(itemValue);
			}
			
			item.pickUp();
		}
	}
	
	private void increaseAmmo(int itemAmmo, DataItemAmmoType ammoType) {
		switch (ammoType) {
			case ARROW:
				properties.ammoArrow = Math.min(properties.ammoArrow + itemAmmo, properties.maxAmmoArrow);
				break;
			case BOMB:
				properties.ammoBomb = Math.min(properties.ammoBomb + itemAmmo, properties.maxAmmoBomb);
				break;
			default:
				throw new IllegalStateException("Unexpected ItemAmmoType: " + ammoType);
		}
	}
	
	public boolean hasAmmo(DataItemAmmoType ammoType) {
		return getAmmo(ammoType) > 0;
	}
	
	public int getAmmo(DataItemAmmoType ammoType) {
		switch (ammoType) {
			case ARROW:
				return properties.ammoArrow;
			case BOMB:
				return properties.ammoBomb;
			default:
				throw new IllegalStateException("Unexpected ItemAmmoType: " + ammoType);
		}
	}
	
	public void decreaseAmmo(DataItemAmmoType ammoType) {
		switch (ammoType) {
			case ARROW:
				properties.ammoArrow = Math.max(properties.ammoArrow - 1, 0);
				break;
			case BOMB:
				properties.ammoBomb = Math.max(properties.ammoBomb - 1, 0);
				break;
			default:
				throw new IllegalStateException("Unexpected ItemAmmoType: " + ammoType);
		}
	}
	
	public int getNumNormalKeys() {
		return characterKeyContainer.getNumNormalKeys();
	}
	
	public boolean containsKey(ObjectMap<String, String> keyProperties) {
		return characterKeyContainer.containsKey(keyProperties);
	}
	
	public void takeKey(ObjectMap<String, String> keyProperties) {
		characterKeyContainer.takeKey(keyProperties);
	}
	
	public void addSpecialItem(String itemId) {
		properties.specialItems.add(itemId);
	}
	
	public boolean containsSpecialItem(String itemId) {
		return properties.specialItems.contains(itemId);
	}
	
	public void removeSpecialItem(String itemId) {
		properties.specialItems.remove(itemId);
	}
}
