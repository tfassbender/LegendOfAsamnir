package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DummyAnimationDirector<T extends TextureRegion> extends AnimationDirector<T> {
	
	public DummyAnimationDirector() {
		super(null);
	}
	
	@Override
	protected void initializeSpriteConfigWithoutPosition() {}
	
	@Override
	public T getKeyFrame() {
		return null;
	}
	
	@Override
	public boolean isAnimationFinished() {
		return true;
	}
}
