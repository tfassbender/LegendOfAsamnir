package net.jfabricationgames.gdx.character.player.implementation;

public enum SpecialAction {
	
	JUMP(0, 0f), //
	BOW(1, 0f), //
	BOMB(2, 0f), //
	BOOMERANG(3, 5f), //
	WAND(4, 10f); //
	
	public static SpecialAction findByNameIgnoringCase(String specialAction) {
		for (SpecialAction action : values()) {
			if (action.name().equalsIgnoreCase(specialAction)) {
				return action;
			}
		}
		return null;
	}
	
	public final int indexInMenu;
	public final float manaCost;
	
	private SpecialAction(int indexInMenu, float manaCost) {
		this.indexInMenu = indexInMenu;
		this.manaCost = manaCost;
	}
}
