package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.constants.Constants;

public class FactoryUtil {
	
	private static Json json = new Json();
	
	public static <T> T loadConfig(Class<T> clazz, String configFile) {
		return json.fromJson(clazz, Gdx.files.internal(configFile));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ObjectMap<String, T> loadTypeConfigs(String configFile, Class<T> configTypeClass) {
		return json.fromJson(ObjectMap.class, configTypeClass, Gdx.files.internal(configFile));
	}
	
	public static Sprite createSprite(TextureAtlas atlas, float x, float y, String textureName) {
		if (textureName == null) {
			return null;
		}
		
		AnimationFrame animationFrame = AnimationFrame.getAnimationFrame(textureName);
		TextureRegion textureRegion = animationFrame.findRegion(atlas);
		
		Sprite sprite = new Sprite(textureRegion);
		sprite.setX(x * Constants.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * Constants.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(Constants.WORLD_TO_SCREEN);
		return sprite;
	}
	
	public static AnimationDirector<TextureRegion> createAnimation(float x, float y, String animationName) {
		if (animationName == null) {
			return null;
		}
		
		AnimationDirector<TextureRegion> animation = AnimationManager.getInstance().getTextureAnimationDirectorCopy(animationName);
		AnimationSpriteConfig spriteConfig = createSpriteConfig(animation.getKeyFrame(), x, y);
		animation.setSpriteConfig(spriteConfig);
		return animation;
	}
	
	private static AnimationSpriteConfig createSpriteConfig(TextureRegion texture, float x, float y) {
		AnimationSpriteConfig spriteConfig = new AnimationSpriteConfig();
		spriteConfig.width = texture.getRegionWidth();
		spriteConfig.height = texture.getRegionHeight();
		spriteConfig.x = x * Constants.WORLD_TO_SCREEN - spriteConfig.width * 0.5f;
		spriteConfig.y = y * Constants.WORLD_TO_SCREEN - spriteConfig.width * 0.5f;
		
		return spriteConfig;
	}
}
