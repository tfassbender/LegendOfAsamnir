package net.jfabricationgames.gdx.sound;

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import net.jfabricationgames.gdx.sound.config.SoundConfig;

/**
 * A set of sound effects that can be played.
 */
public class SoundSet implements Disposable {
	
	private String name;
	
	private ArrayMap<String, String> soundFiles;
	private ArrayMap<String, Sound> sounds;
	private ArrayMap<String, SoundConfig> soundConfigs;
	
	private boolean loaded = false;
	
	public SoundSet(String name, Map<String, SoundConfig> soundConfigs) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("The name of a SoundSet must not be null or empty");
		}
		this.name = name;
		
		loadSoundSetFromConfig(soundConfigs);
	}
	
	private void loadSoundSetFromConfig(Map<String, SoundConfig> configs) {
		soundConfigs = new ArrayMap<String, SoundConfig>();
		soundFiles = new ArrayMap<String, String>();
		sounds = new ArrayMap<String, Sound>();
		
		for (Entry<String, SoundConfig> config : configs.entrySet()) {
			soundFiles.put(config.getKey(), config.getValue().getPath());
			soundConfigs.put(config.getKey(), config.getValue());
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
			for (ObjectMap.Entry<String, String> soundFile : soundFiles.entries()) {
				Sound sound = Gdx.audio.newSound(Gdx.files.internal(soundFile.value));
				sounds.put(soundFile.key, sound);
			}
		}
		loaded = true;
		return this;
	}
	
	private Sound getSound(String name) {
		if (!loaded) {
			throw new IllegalStateException("The sounds were not loaded yet or have been disposed.");
		}
		Sound sound = sounds.get(name);
		if (sound == null) {
			throw new IllegalArgumentException("A sound named '" + name + "' doesn't exist in this sound set.");
		}
		return sound;
	}
	
	public SoundHandler playSound(String name) {
		Sound sound = getSound(name);
		SoundHandler soundHandler = new SoundHandler(sound);
		
		float volume = soundConfigs.get(name).getVolume();
		float delay = soundConfigs.get(name).getDelay();
		if (delay > 0.01) {
			Timer.schedule(new Task() {
				
				@Override
				public void run() {
					if (!soundHandler.isSoundStoped()) {
						sound.play(volume);
					}
				}
			}, delay);
		}
		else {
			sound.play(volume);
		}
		
		return soundHandler;
	}
	
	public Sound loopSound(String name) {
		Sound sound = getSound(name);
		SoundHandler soundHandler = new SoundHandler(sound);
		
		float volume = soundConfigs.get(name).getVolume();
		float delay = soundConfigs.get(name).getDelay();
		if (delay > 0.01) {
			Timer.schedule(new Task() {
				
				@Override
				public void run() {
					if (!soundHandler.isSoundStoped()) {
						sound.loop(volume);
					}
				}
			}, delay);
		}
		else {
			sound.loop(volume);
		}
		
		return sound;
	}
	
	@Override
	public void dispose() {
		sounds.values().forEach(Sound::dispose);
		loaded = false;
	}
}
