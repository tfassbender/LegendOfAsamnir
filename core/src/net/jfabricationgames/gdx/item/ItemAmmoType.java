package net.jfabricationgames.gdx.item;

import net.jfabricationgames.gdx.character.SpecialAction;

public enum ItemAmmoType {
	
	ARROW, //
	BOMB; //
	
	public static ItemAmmoType getByNameIgnoreCase(String name) {
		for (ItemAmmoType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		throw new IllegalStateException("Unknown ItemAmmoType name: " + name);
	}
	
	public static ItemAmmoType fromSpecialAction(SpecialAction specialAction) {
		switch (specialAction) {
			case BOMB:
				return BOMB;
			case BOW:
				return ARROW;
			default:
				throw new IllegalStateException("No known ItemAmmoType for SpecialAction " + specialAction);
		}
	}
}
