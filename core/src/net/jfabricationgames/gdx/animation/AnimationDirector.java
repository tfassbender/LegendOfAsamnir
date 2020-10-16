package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import net.jfabricationgames.gdx.screens.game.GameScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationDirector<T extends TextureRegion> {
	
	private float stateTime;
	private Animation<T> animation;
	
	private AnimationSpriteConfig spriteConfig;
	
	public AnimationDirector(Animation<T> animation) {
		this.animation = animation;
	}
	
	/**
	 * Draw the current key frame of this animation onto the {@link SpriteBatch}.<br>
	 * ATTENTION: This method will throw an {@link IllegalStateException} if this AnimationDirector does not contain an AnimationSpriteConfig object.
	 */
	public void draw(SpriteBatch batch) {
		if (spriteConfig == null) {
			throw new IllegalStateException("No AnimationSpriteConfig. Please add an AnimationSpriteConfig in order to use the draw method");
		}
		T keyFrame = getKeyFrame();
		float x = spriteConfig.x + ((spriteConfig.width - keyFrame.getRegionWidth()) * GameScreen.WORLD_TO_SCREEN * 0.5f);
		float y = spriteConfig.y + ((spriteConfig.height - keyFrame.getRegionHeight()) * GameScreen.WORLD_TO_SCREEN * 0.5f);
		batch.draw(keyFrame, x, y, spriteConfig.width * 0.5f, spriteConfig.height * 0.5f, keyFrame.getRegionWidth(), keyFrame.getRegionHeight(),
				GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
	}
	
	/**
	 * Get the frame at the current time.
	 */
	public T getKeyFrame() {
		return getKeyFrame(0);
	}
	
	/**
	 * Increase the state time and get the frame at the increased state time.
	 * 
	 * @param delta
	 *        The time delta from the render method.
	 */
	public T getKeyFrame(float delta) {
		increaseStateTime(delta);
		return animation.getKeyFrame(stateTime);
	}
	
	/**
	 * Increase the state time by a given delta time.
	 * 
	 * @param delta
	 *        The time delta from the render method.
	 */
	public void increaseStateTime(float delta) {
		stateTime += delta;
	}
	
	/**
	 * Reset the state time to 0 to restart the animation.
	 */
	public void resetStateTime() {
		stateTime = 0;
	}
	
	/**
	 * Set the animation state time to the end of the animation.
	 */
	public void endAnimation() {
		stateTime = animation.getAnimationDuration();
	}
	
	public void setPlayMode(PlayMode playMode) {
		animation.setPlayMode(playMode);
	}
	
	/**
	 * Get the {@link Animation} that this object holds.
	 */
	public Animation<T> getAnimation() {
		return animation;
	}
	
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(stateTime);
	}
	
	/**
	 * Flip all key frames of the animation.
	 */
	public void flip(boolean x, boolean y) {
		for (TextureRegion region : animation.getKeyFrames()) {
			region.flip(x, y);
		}
	}
	
	public AnimationSpriteConfig getSpriteConfig() {
		return spriteConfig;
	}
	
	public void setSpriteConfig(AnimationSpriteConfig spriteConfig) {
		this.spriteConfig = spriteConfig;
	}
}
