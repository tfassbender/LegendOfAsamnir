package net.jfabricationgames.gdx.sound;

import com.badlogic.gdx.audio.Sound;

public class SoundHandler {
	
	private Sound sound;
	private boolean soundSoped = false;
	
	public SoundHandler(Sound sound) {
		this.sound = sound;
	}
	
	public void stop() {
		sound.stop();
		soundSoped = true;
	}
	
	public boolean isSoundStoped() {
		return soundSoped;
	}
}
