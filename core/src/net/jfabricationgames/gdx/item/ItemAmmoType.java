package net.jfabricationgames.gdx.item;

public enum ItemAmmoType {
	
	ARROW,
	BOMB;
	
	public static ItemAmmoType getByNameIgnoreCase(String name) {
		for (ItemAmmoType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		throw new IllegalStateException("Unknown ItemAmmoType name: " + name);
	}
}
