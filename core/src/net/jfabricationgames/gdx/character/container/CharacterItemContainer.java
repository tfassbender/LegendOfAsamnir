package net.jfabricationgames.gdx.character.container;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.container.data.CharacterItemProperties;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.item.ItemPropertyKeys;

public class CharacterItemContainer {
	
	private static final String ITEM_NAME_KEY = "key";
	
	private CharacterPropertiesContainer characterProperties;
	private CharacterKeyContainer characterKeyContainer;
	private CharacterItemProperties properties;
	
	public CharacterItemContainer() {
		this(null);
	}
	public CharacterItemContainer(CharacterPropertiesContainer characterProperties) {
		this.characterProperties = characterProperties;
		properties = new CharacterItemProperties();
		characterKeyContainer = new CharacterKeyContainer(properties);
	}
	
	public void collectItem(Item item) {
		if (item.containsProperty(ItemPropertyKeys.HEALTH.getPropertyName())) {
			float itemHealth = item.getProperty(ItemPropertyKeys.HEALTH.getPropertyName(), Float.class);
			characterProperties.increaseHealth(itemHealth);
		}
		if (item.containsProperty(ItemPropertyKeys.MANA.getPropertyName())) {
			float itemMana = item.getProperty(ItemPropertyKeys.MANA.getPropertyName(), Float.class);
			characterProperties.increaseMana(itemMana);
		}
		if (item.containsProperty(ItemPropertyKeys.ARMOR.getPropertyName())) {
			float itemArmor = item.getProperty(ItemPropertyKeys.ARMOR.getPropertyName(), Float.class);
			characterProperties.increaseArmor(itemArmor);
		}
		if (item.containsProperty(ItemPropertyKeys.AMMO.getPropertyName())) {
			int itemAmmo = item.getProperty(ItemPropertyKeys.AMMO.getPropertyName(), Float.class).intValue();
			if (item.containsProperty(ItemPropertyKeys.AMMO_TYPE.getPropertyName())) {
				ItemAmmoType ammoType = ItemAmmoType
						.getByNameIgnoreCase(item.getProperty(ItemPropertyKeys.AMMO_TYPE.getPropertyName(), String.class));
				increaseAmmo(itemAmmo, ammoType);
			}
			else {
				throw new IllegalStateException("The ammo item has no ammo type defined. It should be added to default_values.json file.");
			}
		}
		if (item.getItemName().equals(ITEM_NAME_KEY)) {
			characterKeyContainer.addKey(item);
		}
		
		item.pickUp();
	}
	
	private void increaseAmmo(int itemAmmo, ItemAmmoType ammoType) {
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
	
	public boolean hasAmmo(ItemAmmoType ammoType) {
		return getAmmo(ammoType) > 0;
	}
	
	public int getAmmo(ItemAmmoType ammoType) {
		switch (ammoType) {
			case ARROW:
				return properties.ammoArrow;
			case BOMB:
				return properties.ammoBomb;
			default:
				throw new IllegalStateException("Unexpected ItemAmmoType: " + ammoType);
		}
	}
	
	public void decreaseAmmo(ItemAmmoType ammoType) {
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
	
	public boolean containsKey(ObjectMap<String, String> keyProperties) {
		return characterKeyContainer.containsKey(keyProperties);
	}
	
	public void takeKey(ObjectMap<String, String> keyProperties) {
		characterKeyContainer.takeKey(keyProperties);
	}
}
