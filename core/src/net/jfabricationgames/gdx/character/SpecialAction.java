package net.jfabricationgames.gdx.character;

public enum SpecialAction {
	
	JUMP(0), //
	BOW(1); //
	
	public final int indexInMenu;
	
	private SpecialAction(int indexInMenu) {
		this.indexInMenu = indexInMenu;
	}
	
	public static SpecialAction findByNameIgnoringCase(String specialAction) {
		for (SpecialAction action : values()) {
			if (action.name().equalsIgnoreCase(specialAction)) {
				return action;
			}
		}
		return null;
	}
}
