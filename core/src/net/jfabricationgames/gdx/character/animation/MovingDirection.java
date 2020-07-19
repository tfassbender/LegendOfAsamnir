package net.jfabricationgames.gdx.character.animation;

public enum MovingDirection {
	
	NONE(true), //
	UP(true), //
	DOWN(false), //
	LEFT(false), //
	RIGHT(true);
	
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
}
