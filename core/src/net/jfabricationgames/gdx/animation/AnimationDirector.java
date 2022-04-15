package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.constants.Constants;

public abstract class AnimationDirector<T extends TextureRegion> {
	
	public static boolean isTextureRight(boolean initialAnimationDirectionIsRight, AnimationDirector<TextureRegion> animation) {
		return initialAnimationDirectionIsRight != animation.getKeyFrame().isFlipX();
	}
	
	public static boolean isTextureLeft(boolean initialAnimationDirectionIsRight, AnimationDirector<TextureRegion> animation) {
		return initialAnimationDirectionIsRight == animation.getKeyFrame().isFlipX();
	}
	
	protected float stateTime;
	protected AnimationSpriteConfig spriteConfig;
	
	protected abstract void initializeSpriteConfigWithoutPosition();
	
	/**
	 * Draw the current key frame of this animation onto the {@link SpriteBatch}.<br>
	 * ATTENTION: This method will throw an {@link IllegalStateException} if this AnimationDirector does not contain an AnimationSpriteConfig object.
	 */
	public void draw(SpriteBatch batch) {
		if (spriteConfig == null) {
			throw new IllegalStateException("No AnimationSpriteConfig. Please add an AnimationSpriteConfig in order to use the draw method");
		}
		T keyFrame = getKeyFrame();
		float x = spriteConfig.x + ((spriteConfig.width - keyFrame.getRegionWidth()) * Constants.WORLD_TO_SCREEN * 0.5f);
		float y = spriteConfig.y + ((spriteConfig.height - keyFrame.getRegionHeight()) * Constants.WORLD_TO_SCREEN * 0.5f);
		batch.draw(keyFrame, x, y, spriteConfig.width * 0.5f, spriteConfig.height * 0.5f, keyFrame.getRegionWidth(), keyFrame.getRegionHeight(),
				Constants.WORLD_TO_SCREEN, Constants.WORLD_TO_SCREEN, 0f);
	}
	
	public void scaleSprite(Sprite sprite) {}
	
	/**
	 * Get the frame at the current time.
	 */
	public abstract T getKeyFrame();
	
	/**
	 * Set the animation state time to the end of the animation.
	 */
	public abstract void endAnimation();
	
	public abstract boolean isAnimationFinished();
	
	public abstract float getAnimationDuration();
	
	public abstract void flip(boolean x, boolean y);
	
	public float getStateTime() {
		return stateTime;
	}
	
	public void increaseStateTime(float delta) {
		stateTime += delta;
	}
	
	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}
	
	/**
	 * Reset the state time to 0 to restart the animation.
	 */
	public void resetStateTime() {
		stateTime = 0;
	}
	
	public AnimationSpriteConfig getSpriteConfig() {
		return spriteConfig;
	}
	
	public void setSpriteConfig(AnimationSpriteConfig spriteConfig) {
		this.spriteConfig = spriteConfig;
	}
	
	public AnimationSpriteConfig getSpriteConfigCopy() {
		return new AnimationSpriteConfig(spriteConfig);
	}
}
