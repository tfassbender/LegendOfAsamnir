package net.jfabricationgames.gdx.desktop.util;

import java.util.HashMap;

public class TextureSettingsMap {
	
	private HashMap<String, TexturePackSetting> levels;
	
	public TextureSettingsMap() {
		
	}
	
	public HashMap<String, TexturePackSetting> getLevels() {
		return levels;
	}
	
	public void setLevels(HashMap<String, TexturePackSetting> levels) {
		this.levels = levels;
	}
}
