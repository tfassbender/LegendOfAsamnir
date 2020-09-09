package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationDirector<T extends TextureRegion> {
	
	private float stateTime;
	private Animation<T> animation;
	
	public AnimationDirector(Animation<T> animation) {
		this.animation = animation;
	}
	
	/**
	 * Get the frame at the current time.
	 * 
	 * @return The current frame.
	 */
	public T getKeyFrame() {
		return getKeyFrame(0);
	}
	
	/**
	 * Increase the state time and get the frame at the increased state time.
	 * 
	 * @param delta
	 *        The time delta from the render method.
	 * 		
	 * @return The frame after the increased state time.
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
	
	/**
	 * Set the {@link PlayMode} of the underlying animation.
	 */
	public void setPlayMode(PlayMode playMode) {
		animation.setPlayMode(playMode);
	}
	
	/**
	 * Get the {@link Animation} that this object holds.
	 * 
	 * @return The {@link Animation}
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
}
