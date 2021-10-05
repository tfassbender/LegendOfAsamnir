package net.jfabricationgames.gdx.data.handler.type;

public enum DataItemAmmoType {
	
	ARROW, //
	BOMB; //
	
	public static DataItemAmmoType getByNameIgnoreCase(String name) {
		for (DataItemAmmoType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		throw new IllegalStateException("Unknown ItemAmmoType name: " + name);
	}
}
