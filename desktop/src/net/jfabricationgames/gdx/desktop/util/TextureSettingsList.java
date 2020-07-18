package net.jfabricationgames.gdx.desktop.util;

import java.util.List;

public class TextureSettingsList {
	
	private List<TexturePackSetting> settings;
	
	public TextureSettingsList(List<TexturePackSetting> settings) {
		this.settings = settings;
	}
	
	public TextureSettingsList() {}
	
	public List<TexturePackSetting> getSettings() {
		return settings;
	}
	public void setSettings(List<TexturePackSetting> settings) {
		this.settings = settings;
	}
}
