package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.ItemSpecialAction;

public enum SpecialAction implements ItemSpecialAction {
	
	JUMP(0, 0f, 10f, 1f, "special_action_available__jump"), //
	BOW(1, 0f, 25f, 1f, "special_action_available__bow"), //
	BOMB(2, 0f, 35f, 1f, "special_action_available__bomb"), //
	BOOMERANG(3, 25f, 5f, 1f, "special_action_available__boomerang"), //
	WAND(4, 10f, 25f, 1f, "special_action_available__wand"), // 
	FEATHER(5, 0f, 0f, 0.65f, "special_action_available__feather"), //
	LANTERN(6, 10f, 0f, 0.65f, "special_action_available__lantern"), //
	RING(7, 0f, 0f, 1f, "special_action_available__ring"); //
	
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
	public final float enduranceCost;
	public final String actionEnabledGlobalValueKey;
	
	private final float textureScaleFactor;
	
	private SpecialAction(int indexInMenu, float manaCost, float enduranceCost, float textureScaleFactor, String globalValueKey) {
		this.indexInMenu = indexInMenu;
		this.manaCost = manaCost;
		this.enduranceCost = enduranceCost;
		this.textureScaleFactor = textureScaleFactor;
		this.actionEnabledGlobalValueKey = globalValueKey;
	}
	
	public boolean canBeUsed() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(actionEnabledGlobalValueKey);
	}
	
	@Override
	public float getScaleFactor() {
		return textureScaleFactor;
	}
	
	@Override
	public String getActionEnabledGlobalValueKey() {
		return actionEnabledGlobalValueKey;
	}
}
