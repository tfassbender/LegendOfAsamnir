package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;

public enum SpecialAction {
	
	JUMP(0, 0f, "special_action_available__jump"), //
	BOW(1, 0f, "special_action_available__bow"), //
	BOMB(2, 0f, "special_action_available__bomb"), //
	BOOMERANG(3, 5f, "special_action_available__boomerang"), //
	WAND(4, 10f, "special_action_available__wand"), // 
	FEATHER(5, 0f, "special_action_available__feather"), //
	LANTERN(6, 10f, "special_action_available__lantern"); //
	
	public static SpecialAction findByNameIgnoringCase(String specialAction) {
		for (SpecialAction action : values()) {
			if (action.name().equalsIgnoreCase(specialAction)) {
				return action;
			}
		}
		return null;
	}
	
	public static SpecialAction getByContainingName(String itemName) {
		for (SpecialAction action : values()) {
			if (itemName.toUpperCase().contains(action.name())) {
				return action;
			}
		}
		throw new IllegalStateException("No special action name contained in search string: " + itemName);
	}
	
	public static Array<String> getNamesAsList() {
		Array<String> names = new Array<>(values().length);
		
		for (SpecialAction action : values()) {
			names.add(action.name().toLowerCase());
		}
		
		return names;
	}
	
	public static SpecialAction getNextSpecialAction(SpecialAction activeSpecialAction, int delta) {
		int searchedIndex = (activeSpecialAction.indexInMenu + delta + values().length) % values().length;
		for (SpecialAction action : values()) {
			if (action.indexInMenu == searchedIndex) {
				return action;
			}
		}
		throw new IllegalStateException("No special action found.");
	}
	
	public final int indexInMenu;
	public final float manaCost;
	public final String actionEnabledGlobalValueKey;
	
	private SpecialAction(int indexInMenu, float manaCost, String globalValueKey) {
		this.indexInMenu = indexInMenu;
		this.manaCost = manaCost;
		this.actionEnabledGlobalValueKey = globalValueKey;
	}
	
	public boolean canBeUsed() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(actionEnabledGlobalValueKey);
	}
}
