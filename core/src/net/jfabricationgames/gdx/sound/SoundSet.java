package net.jfabricationgames.gdx.sound;

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.sound.config.SoundConfig;

/**
 * A set of sound effects that can be played.
 */
public class SoundSet implements Disposable {
	
	private String name;
	
	private ArrayMap<String, String> soundFiles;
	private ArrayMap<String, Sound> sounds;
	private ArrayMap<String, Float> defaultVolumes;
	
	private boolean loaded = false;
	
	public SoundSet(String name, Map<String, SoundConfig> soundConfigs) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("The name of a SoundSet must not be null or empty");
		}
		this.name = name;
		
		loadSoundSetFromConfig(soundConfigs);
	}
	
	private void loadSoundSetFromConfig(Map<String, SoundConfig> soundConfigs) {
		soundFiles = new ArrayMap<String, String>();
		sounds = new ArrayMap<String, Sound>();
		defaultVolumes = new ArrayMap<String, Float>();
		
		for (Entry<String, SoundConfig> config : soundConfigs.entrySet()) {
			soundFiles.put(config.getKey(), config.getValue().getPath());
			defaultVolumes.put(config.getKey(), config.getValue().getVolume());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	protected SoundSet load() {
		if (!loaded) {
			for (com.badlogic.gdx.utils.ObjectMap.Entry<String, String> soundFile : soundFiles.entries()) {
				Sound sound = Gdx.audio.newSound(Gdx.files.internal(soundFile.value));
				sounds.put(soundFile.key, sound);
			}
		}
		loaded = true;
		return this;
	}
	
	public boolean containsSound(String name) {
		return sounds.containsKey(name);
	}
	
	public Sound getSound(String name) {
		return getSoundChecked(name);
	}
	
	public void playSound(String name) {
		Sound sound = getSoundChecked(name);
		float volume = defaultVolumes.get(name);
		sound.play(volume);
	}
	
	public void loopSound(String name) {
		Sound sound = getSoundChecked(name);
		float volume = defaultVolumes.get(name);
		sound.loop(volume);
	}
	
	private Sound getSoundChecked(String name) {
		if (!loaded) {
			throw new IllegalStateException("The sounds were not loaded yet or have been disposed.");
		}
		Sound sound = sounds.get(name);
		if (sound == null) {
			throw new IllegalArgumentException("A sound named '" + name + "' doesn't exist in this sound set.");
		}
		return sound;
	}
	
	public void dispose() {
		sounds.values().forEach(Sound::dispose);
		loaded = false;
	}
}
