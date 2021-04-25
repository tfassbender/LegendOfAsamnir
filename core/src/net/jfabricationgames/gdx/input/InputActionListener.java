package net.jfabricationgames.gdx.input;

public interface InputActionListener {
	
	public enum Type {
		KEY_DOWN, //
		KEY_UP, //
		KEY_TYPED, //
		BUTTON_PRESSED, //
		BUTTON_RELEASED, // 
		MOUSE_DRAGGED, //
		MOUSE_MOVED, //
		SCROLLED, //
		CONTROLLER_BUTTON_PRESSED, //
		CONTROLLER_BUTTON_RELEASED, //
		CONTROLLER_AXIS_THRESHOLD_PASSED; //
	}
	
	/**
	 * A priority for the {@link InputActionListener} let the components with higher priority handle the input events first and maybe consume the
	 * event.
	 */
	public enum Priority {
		
		NORMAL(1), // will be called with normal priority (after menus, ...)
		ON_SCREEN_TEXT(2), // will be called before normal map interactions
		MENU(3); // will always be called first
		
		public final int priority;
		
		private Priority(int priority) {
			this.priority = priority;
		}
	}
	
	public class Parameters {
		
		public int keycode;
		public int button;
		public float screenX;
		public float screenY;
		public int pointer;//the pointer for touch events
		public int scrollAmount;
		public char character;//the character for keyTyped events
		public int player;//the player that holds a controller
		public float axisValue;
		public float axisThreshold;
		
		public Parameters setKeycode(int keycode) {
			this.keycode = keycode;
			return this;
		}
		
		public Parameters setButton(int button) {
			this.button = button;
			return this;
		}
		
		public Parameters setScreenX(float screenX) {
			this.screenX = screenX;
			return this;
		}
		
		public Parameters setScreenY(float screenY) {
			this.screenY = screenY;
			return this;
		}
		
		public Parameters setPointer(int pointer) {
			this.pointer = pointer;
			return this;
		}
		
		public Parameters setScrollAmount(int scrollAmount) {
			this.scrollAmount = scrollAmount;
			return this;
		}
		
		public Parameters setCharacter(char character) {
			this.character = character;
			return this;
		}
		
		public Parameters setPlayer(int player) {
			this.player = player;
			return this;
		}
		
		public Parameters setAxisValue(float axisValue) {
			this.axisValue = axisValue;
			return this;
		}
		
		public Parameters setAxisThreshold(float axisThreshold) {
			this.axisThreshold = axisThreshold;
			return this;
		}
		
		@Override
		public String toString() {
			return "Parameters [keycode=" + keycode + ", button=" + button + ", screenX=" + screenX + ", screenY=" + screenY + ", pointer=" + pointer
					+ ", scrollAmount=" + scrollAmount + ", character=" + character + ", player=" + player + ", axisValue=" + axisValue
					+ ", axisThreshold=" + axisThreshold + "]";
		}
	}
	
	public boolean onAction(String action, Type type, Parameters parameters);
	
	public default Priority getInputPriority() {
		return Priority.NORMAL;
	};
}
