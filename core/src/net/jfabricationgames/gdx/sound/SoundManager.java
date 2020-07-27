package net.jfabricationgames.gdx.sound;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.sound.config.SoundManagerConfig;
import net.jfabricationgames.gdx.sound.config.SoundSetConfig;

/**
 * Manages {@link SoundSet}s for all types
 */
public class SoundManager implements Disposable {
	
	public static final String SOUND_CONFIG_PATH = "sound/config.json";
	
	private static SoundManager instance;
	
	public static synchronized SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	private ArrayMap<String, SoundSet> soundSets;
	
	private SoundManager() {
		
	}
	
	public void loadConfig() {
		soundSets = new ArrayMap<>();
		FileHandle configFile = Gdx.files.internal(SOUND_CONFIG_PATH);
		Json json = new Json();
		SoundManagerConfig config = json.fromJson(SoundManagerConfig.class, HashMap.class, configFile);
		for (Entry<String, SoundSetConfig> configEntry : config.getSoundSets().entrySet()) {
			soundSets.put(configEntry.getKey(), loadSoundSetConfig(configEntry.getValue(), configEntry.getKey()));
		}
	}
	
	private SoundSet loadSoundSetConfig(SoundSetConfig config, String name) {
		return new SoundSet(name, config.getSounds());
	}
	
	public SoundSet loadSoundSet(String name) {
		return getSoundSetChecked(name).load();
	}
	
	public void disposeSoundSet(String name) {
		getSoundSetChecked(name).dispose();
	}
	
	private SoundSet getSoundSetChecked(String name) {
		SoundSet soundSet = soundSets.get(name);
		if (soundSet == null) {
			throw new IllegalArgumentException("A sound set named '" + name + "' doesn't exist.");
		}
		return soundSet;
	}
	
	@Override
	public void dispose() {
		soundSets.values().forEach(SoundSet::dispose);
	}
}
