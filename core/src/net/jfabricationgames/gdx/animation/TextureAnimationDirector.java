package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureAnimationDirector<T extends TextureRegion> extends AnimationDirector<T> {
	
	private Animation<T> animation;
	
	public TextureAnimationDirector(Animation<T> animation) {
		this.animation = animation;
		initializeSpriteConfigWithoutPosition();
	}
	
	@Override
	protected void initializeSpriteConfigWithoutPosition() {
		T keyFrame = getKeyFrame();
		spriteConfig = new AnimationSpriteConfig().setWidth(keyFrame.getRegionWidth()).setHeight(keyFrame.getRegionHeight());
	}
	
	/**
	 * Get the frame at the current time.
	 */
	@Override
	public T getKeyFrame() {
		return animation.getKeyFrame(stateTime);
	}
	
	/**
	 * Set the animation state time to the end of the animation.
	 */
	@Override
	public void endAnimation() {
		stateTime = animation.getAnimationDuration();
	}
	
	@Override
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(stateTime);
	}
	
	@Override
	public float getAnimationDuration() {
		return animation.getAnimationDuration();
	}
	
	/**
	 * Flip all key frames of the animation.
	 */
	@Override
	public void flip(boolean x, boolean y) {
		for (TextureRegion region : animation.getKeyFrames()) {
			region.flip(x, y);
		}
	}
	
	public void drawInMenu(SpriteBatch batch) {
		if (spriteConfig == null) {
			throw new IllegalStateException("No AnimationSpriteConfig. Please add an AnimationSpriteConfig in order to use the draw method");
		}
		
		T keyFrame = getKeyFrame();
		batch.draw(keyFrame, spriteConfig.x, spriteConfig.y, spriteConfig.width, spriteConfig.height);
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
}
