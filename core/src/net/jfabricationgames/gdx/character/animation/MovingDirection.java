package net.jfabricationgames.gdx.character.animation;

public enum MovingDirection {
	
	UP("right"), //
	DOWN("left"), //
	LEFT("left"), //
	RIGHT("right");
	
	private final String animationDirectionPostfix;
	
	private MovingDirection(String animationDirectionPostfix) {
		this.animationDirectionPostfix = animationDirectionPostfix;
	}
	
	public String getAnimationDirectionPostfix() {
		return animationDirectionPostfix;
	}
}
