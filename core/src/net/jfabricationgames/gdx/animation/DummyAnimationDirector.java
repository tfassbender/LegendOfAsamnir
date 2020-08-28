package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DummyAnimationDirector<T extends TextureRegion> extends AnimationDirector<T> {
	
	public DummyAnimationDirector() {
		super(null);
	}
	
	public T getKeyFrame() {
		return null;
	}
	
	public T getKeyFrame(float delta) {
		increaseStateTime(delta);
		return null;
	}
	
	public boolean isAnimationFinished() {
		return true;
	}
}
