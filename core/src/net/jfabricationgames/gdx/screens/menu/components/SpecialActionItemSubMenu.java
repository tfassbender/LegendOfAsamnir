package net.jfabricationgames.gdx.screens.menu.components;

import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;

public class SpecialActionItemSubMenu extends ItemSubMenu {
	
	private static final int ITEM_MENU_ITEMS_PER_LINE = 4;
	private static final int ITEM_MENU_LINES = 2;
	
	private boolean[] specialActionAvailable;
	
	public SpecialActionItemSubMenu() {
		super(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, SpecialAction.getNamesAsList());
	}
	
	@Override
	public void updateStateAfterMenuShown() {
		specialActionAvailable = new boolean[ITEM_MENU_ITEMS_PER_LINE * ITEM_MENU_LINES];
		
		GlobalValuesDataHandler globalValues = GlobalValuesDataHandler.getInstance();
		for (SpecialAction action : SpecialAction.values()) {
			specialActionAvailable[action.indexInMenu] = globalValues.getAsBoolean(action.actionEnabledGlobalValueKey);
		}
	}
	
	@Override
	protected boolean isItemKnown(int index) {
		return specialActionAvailable[index];
	}
	
	@Override
	public void selectHoveredItem() {
		if (specialActionAvailable[getHoveredIndex()]) {
			super.selectHoveredItem();
		}
	}
}
