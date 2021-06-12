package net.jfabricationgames.gdx.hud;

import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.item.ItemAmmoType;

public interface StatsCharacter {
	
	public float getHealth();
	public float getMana();
	public float getEndurance();
	public float getArmor();
	
	public int getNormalKeys();
	public int getCoinsForHud();
	
	public SpecialAction getActiveSpecialAction();
	public int getAmmo(ItemAmmoType ammoType);
}
