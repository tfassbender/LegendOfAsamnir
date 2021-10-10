package net.jfabricationgames.gdx.item;

import net.jfabricationgames.gdx.data.handler.type.DataItemAmmoType;

public enum ItemAmmoType {
	
	ARROW, //
	BOMB; //
	
	public static ItemAmmoType getByName(String name) {
		for (ItemAmmoType ammoType : values()) {
			if (ammoType.name().equalsIgnoreCase(name)) {
				return ammoType;
			}
		}
		throw new IllegalStateException("No known ItemAmmoType for name " + name);
	}
	
	public DataItemAmmoType toDataType() {
		return DataItemAmmoType.getByNameIgnoreCase(name());
	}
}
