package net.jfabricationgames.gdx.character.player.implementation;

import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

class CharacterSoundHandler {
	
	private static final String SOUND_SET_KEY = "dwarf";
	
	private SoundSet soundSet;
	
	public CharacterSoundHandler() {
		soundSet = SoundManager.getInstance().loadSoundSet(SOUND_SET_KEY);
	}
	
	public void playSound(CharacterAction action) {
		if (action.getSound() != null) {
			playSound(action.getSound());
		}
	}
	
	public void playSound(String sound) {
		soundSet.playSound(sound);
	}
	
	public void dispose() {
		soundSet.dispose();
	}
}
