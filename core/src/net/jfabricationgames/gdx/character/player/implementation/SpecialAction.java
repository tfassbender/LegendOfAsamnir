package net.jfabricationgames.gdx.character.player.implementation;

public enum SpecialAction {
	
	JUMP(0), //
	BOW(1), //
	BOMB(2), //
	BOOMERANG(3); //
	
	public static SpecialAction findByNameIgnoringCase(String specialAction) {
		for (SpecialAction action : values()) {
			if (action.name().equalsIgnoreCase(specialAction)) {
				return action;
			}
		}
		return null;
	}
	
	public final int indexInMenu;
	
	private SpecialAction(int indexInMenu) {
		this.indexInMenu = indexInMenu;
	}
}
