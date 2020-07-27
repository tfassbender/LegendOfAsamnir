package net.jfabricationgames.gdx.sound.config;

import java.util.HashMap;

public class SoundManagerConfig {
	
	private HashMap<String, SoundSetConfig> soundSets;
	
	public SoundManagerConfig() {
		
	}
	
	public HashMap<String, SoundSetConfig> getSoundSets() {
		return soundSets;
	}
	
	public void setSoundSets(HashMap<String, SoundSetConfig> soundSets) {
		this.soundSets = soundSets;
	}
}
