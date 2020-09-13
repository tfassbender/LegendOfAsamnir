package net.jfabricationgames.gdx.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;

public class TextureLoader {
	
	private static Json json = new Json();
	
	private String configFile;
	private ObjectMap<String, TextureConfig> textureConfigs;
	
	public TextureLoader(String configFile) {
		this.configFile = configFile;
		textureConfigs = loadConfigFile(configFile);
	}
	
	@SuppressWarnings("unchecked")
	private ObjectMap<String, TextureConfig> loadConfigFile(String configFile) {
		return json.fromJson(ObjectMap.class, TextureConfig.class, Gdx.files.internal(configFile));
	}
	
	/**
	 * Loads a texture that is identified by the name in the configuration file.
	 */
	public TextureRegion loadTexture(String texture) {
		if (!textureConfigs.containsKey(texture)) {
			throw new IllegalArgumentException(
					"The texture '" + texture + "' doesn't exist in this configuration. Configuration file was: " + configFile);
		}
		
		TextureConfig config = textureConfigs.get(texture);
		TextureAtlas atlas = AssetGroupManager.getInstance().get(config.atlas);
		switch (config.type) {
			case ANIMATION_FRAME:
				AnimationFrame frame = AnimationFrame.getAnimationFrame(config.texture);
				return frame.findRegion(atlas);
			case TEXTURE_REGION:
				return atlas.findRegion(config.texture);
			default:
				throw new IllegalStateException("Unexpected TextureType: " + config.type);
		}
	}
}
