package net.jfabricationgames.gdx.sound.config;

import java.util.HashMap;

public class SoundSetConfig {
	
	private HashMap<String, SoundConfig> sounds;
	
	public SoundSetConfig() {
		
	}
	
	public HashMap<String, SoundConfig> getSounds() {
		return sounds;
	}
	
	public void setSounds(HashMap<String, SoundConfig> sounds) {
		this.sounds = sounds;
	}
}
