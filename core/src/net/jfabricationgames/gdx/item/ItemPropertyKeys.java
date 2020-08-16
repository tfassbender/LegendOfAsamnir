package net.jfabricationgames.gdx.item;

public enum ItemPropertyKeys {
	
	HEALTH("health"), //
	MANA("mana"), //
	VALUE("value"); //
	
	private final String propertyName;
	
	private ItemPropertyKeys(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
}
