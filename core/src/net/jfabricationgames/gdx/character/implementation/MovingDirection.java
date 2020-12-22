package net.jfabricationgames.gdx.character.implementation;

import com.badlogic.gdx.math.Vector2;

public enum MovingDirection {
	
	NONE(true, new Vector2(0f, 0f)), //
	UP(true, new Vector2(0f, 1f)), //
	DOWN(false, new Vector2(0f, -1f)), //
	LEFT(false, new Vector2(-1f, 0f)), //
	RIGHT(true, new Vector2(1f, 0f)), //
	UP_LEFT(false, new Vector2(-1f, 1f).nor()), //
	UP_RIGHT(true, new Vector2(1f, 1f).nor()), //
	DOWN_LEFT(false, new Vector2(-1f, -1f).nor()), //
	DOWN_RIGHT(true, new Vector2(1f, -1f).nor());
	
	public static final String DRAWING_DIRECTION_RIGHT_POSTFIX = "right";
	public static final String DRAWING_DIRECTION_LEFT_POSTFIX = "left";
	
	private final boolean drawingDirectionRight;
	private final Vector2 normalizedDirectionVector;
	
	private MovingDirection(boolean drawingDirectionRight, Vector2 normalizedDirectionVector) {
		this.drawingDirectionRight = drawingDirectionRight;
		this.normalizedDirectionVector = normalizedDirectionVector;
	}
	
	public String getAnimationDirectionPostfix() {
		if (drawingDirectionRight) {
			return DRAWING_DIRECTION_RIGHT_POSTFIX;
		}
		else {
			return DRAWING_DIRECTION_LEFT_POSTFIX;
		}
	}
	
	public boolean isDrawingDirectionRight() {
		return drawingDirectionRight;
	}
	
	public boolean containsDirection(MovingDirection direction) {
		switch (this) {
			case DOWN_LEFT:
				return direction == DOWN || direction == LEFT;
			case DOWN_RIGHT:
				return direction == DOWN || direction == RIGHT;
			case UP_LEFT:
				return direction == UP || direction == LEFT;
			case UP_RIGHT:
				return direction == UP || direction == RIGHT;
			default:
				return direction == this;
		}
	}
	
	public boolean isCombinedDirection() {
		return this == UP_LEFT || this == UP_RIGHT || this == DOWN_LEFT || this == DOWN_RIGHT;
	}
	
	public Vector2 getNormalizedDirectionVector() {
		return normalizedDirectionVector.cpy();
	}
}
