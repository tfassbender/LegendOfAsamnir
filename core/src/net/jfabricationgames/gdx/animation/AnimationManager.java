package net.jfabricationgames.gdx.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.assets.AssetGroupManager;

public class AnimationManager {
	
	private static AnimationManager instance;
	
	public static synchronized AnimationManager getInstance() {
		if (instance == null) {
			instance = new AnimationManager();
		}
		return instance;
	}
	
	private AssetGroupManager assetManager;
	private Map<String, Animation<TextureRegion>> animations;
	private Map<String, AnimationConfig> animationConfigurations;
	
	private Array<String> configFiles;
	
	private AnimationManager() {
		assetManager = AssetGroupManager.getInstance();
		animations = new HashMap<>();
		animationConfigurations = new HashMap<>();
		
		configFiles = new Array<>();
	}
	
	/**
	 * Load the animations, defined in the configuration file.
	 * 
	 * @param config
	 *        The configuration file, that's animations are to be loaded
	 */
	public void loadAnimations(String... configurations) {
		Gdx.app.log(getClass().getSimpleName(), "Loading animations from config: " + Arrays.toString(configurations));
		configFiles.addAll(configurations);
		for (String config : configurations) {
			AnimationConfigList animationConfig = loadAnimationConfig(config);
			
			animationConfigurations
					.putAll(animationConfig.getConfigList().stream().collect(Collectors.toMap(AnimationConfig::getName, Function.identity())));
			
			animationConfig.getConfigList().forEach(this::createAnimation);
		}
	}
	
	private AnimationConfigList loadAnimationConfig(String config) {
		FileHandle configFile = Gdx.files.internal(config);
		Json json = new Json();
		return json.fromJson(AnimationConfigList.class, configFile);
	}
	
	private void createAnimation(AnimationConfig config) {
		TextureAtlas textureAtlas = assetManager.get(config.getAtlas(), TextureAtlas.class);
		Animation<TextureRegion> animation = new Animation<>(config.getFrameDuration(), textureAtlas.findRegions(config.getName()),
				config.getPlayMode());
		if (animation.getKeyFrames().length == 0) {
			Gdx.app.error(getClass().getSimpleName(), "Animation loaded, but with 0 key frames. Animation was '" + config.getName()
					+ "'. Maybe the animation was configured, but the images were not packed into the atlas?");
		}
		animations.put(config.getName(), animation);
	}
	
	/**
	 * Get an {@link Animation} from the loaded animations.<br>
	 * <b>WARNING:</b> This {@link Animation} will always be the same instance. Use getAnimationCopy(String) for a unique animation instance.
	 * 
	 * @param name
	 *        The name of the animation that was defined in the JSON configuration file from which the animations were loaded.
	 * 		
	 * @return The {@link Animation}.
	 */
	public Animation<TextureRegion> getAnimation(String name) {
		if (!animations.containsKey(name)) {
			throw new IllegalArgumentException("The animation \"" + name + "\" doesn't exist in this asset manager. "
					+ "Please add it to the animation configuration (see config files: " + configFiles + ")");
		}
		return animations.get(name);
	}
	/**
	 * Get a copy of an {@link Animation} from the loaded animations. The copy is a new, independent instance of the animation.<br>
	 * 
	 * @param name
	 *        The name of the animation that was defined in the JSON configuration file from which the animations were loaded.
	 * 		
	 * @return The {@link Animation}.
	 */
	public Animation<TextureRegion> getAnimationCopy(String name) {
		Animation<TextureRegion> animation = getAnimation(name);
		return new Animation<TextureRegion>(animation.getFrameDuration(), new Array<>(animation.getKeyFrames()), animation.getPlayMode());
	}
	
	/**
	 * Get an {@link AnimationDirector} from the loaded animations.<br>
	 * <b>WARNING:</b> The underlying {@link Animation} will always be the same instance. Use getAnimationDirectorCopy(String) for a unique animation
	 * instance.
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
	 * Get an {@link AnimationDirector} from the loaded animations. The underlying {@link Animation} is a new independent instance of a loaded
	 * {@link Animation}.
	 * 
	 * @param name
	 *        The name of the animation that was defined in the JSON configuration file from which the animations were loaded.
	 * 		
	 * @return The {@link AnimationDirector}.
	 */
	public AnimationDirector<TextureRegion> getAnimationDirectorCopy(String name) {
		return new AnimationDirector<TextureRegion>(getAnimationCopy(name));
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
	
	/**
	 * Get a {@link TextureAtlas} from the asset manager.
	 * 
	 * @param name
	 *        The atlas' name
	 * 		
	 * @return The {@link TextureAtlas}
	 */
	public TextureAtlas getAtlas(String name) {
		return assetManager.get(name, TextureAtlas.class);
	}
	
	/**
	 * Get some {@link AtlasRegion}s as an {@link Array} from a {@link TextureAtlas}.
	 * 
	 * @param atlas
	 *        The name of the {@link TextureAtlas}
	 * 		
	 * @param region
	 *        The name of the {@link AtlasRegion}s within the {@link TextureAtlas}
	 * 		
	 * @return An {@link Array} of {@link AtlasRegion} objects.
	 */
	public Array<AtlasRegion> getAtlasRegions(String atlas, String regions) {
		return getAtlas(atlas).findRegions(regions);
	}
	
	/**
	 * Get an {@link AnimationConfig} by it's name.
	 * 
	 * @param configName
	 *        The name of the configuration that is defined in the configuration's JSON file.
	 * 		
	 * @return The {@link AnimationConfig}
	 */
	public AnimationConfig getAnimationConfig(String configName) {
		return animationConfigurations.get(configName);
	}
}
