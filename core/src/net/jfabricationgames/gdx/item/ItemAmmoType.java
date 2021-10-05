package net.jfabricationgames.gdx.item;

import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.data.handler.type.DataItemAmmoType;

public enum ItemAmmoType {
	
	ARROW, //
	BOMB; //
	
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
	
	public DataItemAmmoType toDataType() {
		return DataItemAmmoType.getByNameIgnoreCase(name());
	}
}
