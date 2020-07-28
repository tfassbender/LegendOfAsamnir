package net.jfabricationgames.gdx.sound.config;

public class SoundConfig {
	
	private String path;
	private float volume = 1f;
	private float delay = 0f;
	
	public SoundConfig() {
		
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public float getDelay() {
		return delay;
	}
	
	public void setDelay(float delay) {
		this.delay = delay;
	}
}
