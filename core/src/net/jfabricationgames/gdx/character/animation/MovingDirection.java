package net.jfabricationgames.gdx.character.animation;

import com.badlogic.gdx.math.Vector2;

public enum MovingDirection {
	
	NONE(true), //
	UP(true), //
	DOWN(false), //
	LEFT(false), //
	RIGHT(true), //
	UP_LEFT(false), //
	UP_RIGHT(true), //
	DOWN_LEFT(false), //
	DOWN_RIGHT(true);
	
	public static final String DRAWING_DIRECTION_RIGHT_POSTFIX = "right";
	public static final String DRAWING_DIRECTION_LEFT_POSTFIX = "left";
	
	private final boolean drawingDirectionRight;
	
	private MovingDirection(boolean drawingDirectionRight) {
		this.drawingDirectionRight = drawingDirectionRight;
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
		switch (this) {
			case DOWN:
				return new Vector2(0f, -1f);
			case DOWN_LEFT:
				return new Vector2(-1f, -1f).nor();
			case DOWN_RIGHT:
				return new Vector2(1f, -1f).nor();
			case LEFT:
				return new Vector2(-1f, 0f);
			case NONE:
				return new Vector2(0f, 0f);
			case RIGHT:
				return new Vector2(1f, 0f);
			case UP:
				return new Vector2(0f, 1f);
			case UP_LEFT:
				return new Vector2(-1f, 1f).nor();
			case UP_RIGHT:
				return new Vector2(1f, 1f).nor();
			default:
				throw new IllegalStateException("Unknown MovingDirection: " + this);
		}
	}
}
