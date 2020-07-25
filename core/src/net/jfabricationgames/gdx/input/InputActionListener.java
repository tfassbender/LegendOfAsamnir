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
		SCROLLED; //
	}
	
	public class Parameters {
		
		public int keycode;
		public int button;
		public float screenX;
		public float screenY;
		public int pointer;//the pointer for touch events
		public int scrollAmount;
		public char character;//the character for keyTyped events
		
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
		
		@Override
		public String toString() {
			return "Parameters [keycode=" + keycode + ", button=" + button + ", screenX=" + screenX + ", screenY=" + screenY + ", pointer=" + pointer
					+ ", scrollAmount=" + scrollAmount + ", character=" + character + "]";
		}
	}
	
	public boolean onAction(String action, Type type, Parameters parameters);
}
