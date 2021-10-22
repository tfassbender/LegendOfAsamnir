package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.utils.Array;

public interface StatsCharacter {
	
	public float getHealth();
	public float getMana();
	public float getEndurance();
	public float getArmor();
	
	public int getNormalKeys();
	public int getCoinsForHud();
	
	public String getActiveAction();
	public Array<String> getActionList();
	
	public int getAmmo(String ammoType);
}
