package net.jfabricationgames.gdx.character.animation;

public enum MovingDirection {
	
	UP("right"), //
	DOWN("left"), //
	LEFT("left"), //
	RIGHT("right");
	
	private final String animationPostfix;
	
	private MovingDirection(String animationPostfix) {
		this.animationPostfix = animationPostfix;
	}
	
	public String getAnimationPostfix() {
		return animationPostfix;
	}
}
