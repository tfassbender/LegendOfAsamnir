package net.jfabricationgames.gdx.data.handler.type;

public enum DataItemPropertyKeys {
	
	HEALTH("health"), //
	MANA("mana"), //
	VALUE("value"), // 
	ARMOR("armor"), //
	AMMO("ammo"), 
	AMMO_TYPE("ammoType"); //
	
	private final String propertyName;
	
	private DataItemPropertyKeys(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
}
