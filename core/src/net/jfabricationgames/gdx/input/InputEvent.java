package net.jfabricationgames.gdx.input;

public enum InputEvent {
	
	KEY_DOWN(true), //
	KEY_UP(false), //
	KEY_TYPED(false), //
	TOUCH_DOWN(true), //
	TOUCH_UP(false), // 
	TOUCH_DRAGGED(false), //
	MOUSE_MOVED(false), //
	SCROLLED(false), //
	CONTROLLER_BUTTON_DOWN(true), //
	CONTROLLER_BUTTON_UP(false), //
	CONTROLLER_POV_CHANGED(true), //
	CONTROLLER_AXIS_THRESHOLD_PASSED(true); //
	
	private final boolean isDefault;
	
	private InputEvent(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
}
