package net.jfabricationgames.gdx.character.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

public class CharacterAnimationAssetManager {
	
	public static final String CONFIG_DIR = "config/animation/";
	
	private static CharacterAnimationAssetManager instance;
	
	public static synchronized CharacterAnimationAssetManager getInstance() {
		if (instance == null) {
			instance = new CharacterAnimationAssetManager();
		}
		return instance;
	}
	
	private AssetManager assetManager;
	private Map<String, Animation<TextureRegion>> animations;
	
	private CharacterAnimationAssetManager() {
		assetManager = new AssetManager();
		animations = new HashMap<>();
	}
	
	/**
	 * Load the animations, defined in the configuration file in "assets/config/animation/__the_config_parameter__.json
	 * 
	 * Example: To load dwarf animations from file "assets/config/animation/dwarf.json" enter the parameter "dwarf"
	 * 
	 * @param config
	 *        The configuration file, that's animations are to be loaded (without the file ending)
	 */
	public void loadAnimations(String... configurations) {
		for (String config : configurations) {
			AnimationConfigList animationConfig = loadAnimationConfig(config);
			
			animationConfig.getConfigList().forEach(this::markAnimationForLoading);
			assetManager.finishLoading();
			
			animationConfig.getConfigList().forEach(this::createAnimation);
		}
	}
	
	private AnimationConfigList loadAnimationConfig(String config) {
		FileHandle configFile = Gdx.files.internal(CONFIG_DIR + config + ".json");
		Json json = new Json();
		return json.fromJson(AnimationConfigList.class, configFile);
	}
	
	private void markAnimationForLoading(AnimationConfig animation) {
		if (!animations.containsKey(animation.getName())) {
			assetManager.load(animation.getAtlas(), TextureAtlas.class);
		}
	}
	
	private void createAnimation(AnimationConfig config) {
		TextureAtlas textureAtlas = assetManager.get(config.getAtlas(), TextureAtlas.class);
		Animation<TextureRegion> animation = new Animation<>(config.getFrameDuration(), textureAtlas.findRegions(config.getName()),
				config.getPlayMode());
		animations.put(config.getName(), animation);
	}
	
	/**
	 * Get an {@link Animation} from the loaded animations.
	 * 
	 * @param name
	 *        The name of the animation that was defined in the JSON configuration file from which the animations were loaded.
	 * 		
	 * @return The {@link Animation}.
	 */
	public Animation<TextureRegion> getAnimation(String name) {
		if (!animations.containsKey(name)) {
			throw new IllegalArgumentException("The animation \"" + name + "\" doesn't exist in this asset manager");
		}
		return animations.get(name);
	}
	
	/**
	 * Get an {@link AnimationDirector} from the loaded animations.
	 * 
	 * @param name
	 *        The name of the animation that was defined in the JSON configuration file from which the animations were loaded.
	 * 		
	 * @return The {@link AnimationDirector}.
	 */
	public AnimationDirector<TextureRegion> getAnimationDirector(String name) {
		return new AnimationDirector<TextureRegion>(getAnimation(name));
	}
	
	/**
	 * Get a {@link List} of all names of animations that are currently loaded. The names can then be used to get the animations using the
	 * getAnimation(String) method.
	 * 
	 * @return A {@link List} of the names of all animations.
	 */
	public List<String> getAvailableAnimations() {
		return new ArrayList<String>(animations.keySet());
	}
	
	public void dispose() {
		assetManager.dispose();
	}
}
