package net.jfabricationgames.gdx.screens.menu.components;

import com.badlogic.gdx.utils.Array;

public class ShopItemSubMenu extends ItemSubMenu {
	
	private static final int ITEM_MENU_ITEMS_PER_LINE = 4;
	private static final int ITEM_MENU_LINES = 2;
	
	private static final Array<String> SHOP_ITEMS = new Array<>(new String[] {"health", "shield", "mana", null, "arrow", "bomb"});
	
	public ShopItemSubMenu() {
		super(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, SHOP_ITEMS);
	}
}
