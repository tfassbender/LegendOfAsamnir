package net.jfabricationgames.gdx.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public abstract class AbstractFactory {
	
	protected Json json;
	protected TextureAtlas atlas;
	protected World world;
	
	public AbstractFactory() {
		json = new Json();
	}
	
	public <T> T loadConfig(Class<T> clazz, String configFile) {
		return json.fromJson(clazz, Gdx.files.internal(configFile));
	}
	
	protected Sprite createSprite(float x, float y, String textureName) {
		if (textureName == null) {
			return null;
		}
		
		AnimationFrame animationFrame = AnimationFrame.getAnimationFrame(textureName);
		TextureRegion textureRegion = animationFrame.findRegion(atlas);
		
		Sprite sprite = new Sprite(textureRegion);
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		return sprite;
	}
	
	protected AnimationDirector<TextureRegion> createAnimation(float x, float y, String animationName) {
		if (animationName == null) {
			return null;
		}
		
		AnimationDirector<TextureRegion> animation = AnimationManager.getInstance().getTextureAnimationDirectorCopy(animationName);
		AnimationSpriteConfig spriteConfig = createSpriteConfig(animation.getKeyFrame(), x, y);
		animation.setSpriteConfig(spriteConfig);
		return animation;
	}
	
	protected AnimationSpriteConfig createSpriteConfig(TextureRegion texture, float x, float y) {
		AnimationSpriteConfig spriteConfig = new AnimationSpriteConfig();
		spriteConfig.width = texture.getRegionWidth();
		spriteConfig.height = texture.getRegionHeight();
		spriteConfig.x = x * GameScreen.WORLD_TO_SCREEN - spriteConfig.width * 0.5f;
		spriteConfig.y = y * GameScreen.WORLD_TO_SCREEN - spriteConfig.width * 0.5f;
		
		return spriteConfig;
	}
}
