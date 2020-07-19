package net.jfabricationgames.gdx.character.animation;

import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationDirector<T> {
	
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
	 * Get the {@link Animation} that this object holds.
	 * 
	 * @return The {@link Animation}
	 */
	public Animation<T> getAnimation() {
		return animation;
	}
}
