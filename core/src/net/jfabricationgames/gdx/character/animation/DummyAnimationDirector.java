package net.jfabricationgames.gdx.character.animation;

public class DummyAnimationDirector<T> extends AnimationDirector<T> {
	
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
