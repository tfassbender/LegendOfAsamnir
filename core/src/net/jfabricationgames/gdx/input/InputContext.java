package net.jfabricationgames.gdx.input;

import java.util.function.BiConsumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlReader.Element;

import net.jfabricationgames.gdx.input.InputActionListener.Parameters;
import net.jfabricationgames.gdx.input.InputActionListener.Type;

public class InputContext {
	
	private static final ArrayMap<String, Integer> BUTTONS = createButtonsMap();
	
	private static ArrayMap<String, Integer> createButtonsMap() {
		ArrayMap<String, Integer> buttons = new ArrayMap<>();
		buttons.put("left", Buttons.LEFT);
		buttons.put("middle", Buttons.MIDDLE);
		buttons.put("right", Buttons.RIGHT);
		buttons.put("forward", Buttons.FORWARD);
		buttons.put("back", Buttons.BACK);
		return buttons;
	};
	
	private String name;
	
	private ArrayMap<InputEvent, Boolean> handledEvents;
	
	private ArrayMap<String, Integer> keyStates;
	private ArrayMap<Integer, String> keyActions;
	private ArrayMap<String, Integer> buttonStates;
	private ArrayMap<Integer, String> buttonActions;
	
	private ObjectSet<InputActionListener> listeners;
	
	public InputContext() {
		keyStates = new ArrayMap<>();
		keyActions = new ArrayMap<>();
		buttonStates = new ArrayMap<>();
		buttonActions = new ArrayMap<>();
		handledEvents = new ArrayMap<>();
		listeners = new ObjectSet<>();
	}
	
	public void load(Element contextRoot) {
		keyActions.clear();
		keyStates.clear();
		buttonStates.clear();
		buttonActions.clear();
		handledEvents.clear();
		
		try {
			name = contextRoot.getAttribute("name");
			
			Element eventsElement = contextRoot.getChildByName("events");
			loadEvents(eventsElement);
			
			Element statesElement = contextRoot.getChildByName("states");
			loadElements(statesElement, keyStates::put, buttonStates::put);
			
			Element actionsElement = contextRoot.getChildByName("actions");
			loadElements(actionsElement, (name, code) -> keyActions.put(code, name), (name, code) -> buttonActions.put(code, name));
		}
		catch (Exception e) {
			Gdx.app.error("InputContext", "Error loading context element", e);
		}
	}
	
	/**
	 * Load the events from the profile or set the defaults if no events are defined.
	 */
	private void loadEvents(Element eventsElement) {
		if (eventsElement != null && eventsElement.getChildCount() > 0) {
			for (InputEvent inputEvent : InputEvent.values()) {
				Element inputElement = eventsElement.getChildByName(inputEvent.name());
				if (inputElement != null) {
					if (inputElement.hasAttribute("listening")) {
						handledEvents.put(inputEvent, Boolean.parseBoolean(inputElement.getAttribute("listening")));
					}
					else {
						//the event has no "listening" attribute, so it is assumed to be true
						handledEvents.put(inputEvent, true);
					}
				}
			}
		}
		else {
			//use the defaults on all events
			for (InputEvent inputEvent : InputEvent.values()) {
				handledEvents.put(inputEvent, inputEvent.isDefault());
			}
		}
	}
	
	/**
	 * Load the state and action elements.
	 */
	private void loadElements(Element contextElement, BiConsumer<String, Integer> keyInsertConsumer,
			BiConsumer<String, Integer> buttonInsertConsumer) {
		int numStates = contextElement != null ? contextElement.getChildCount() : 0;
		
		for (int i = 0; i < numStates; ++i) {
			Element element = contextElement.getChild(i);
			String elementName = element.getAttribute("name");
			
			Element keyElement = element.getChildByName("key");
			Element buttonElement = element.getChildByName("button");
			
			if (keyElement != null) {
				String code = keyElement.getAttribute("code");
				int keycode = Keys.valueOf(code);
				if (keycode == -1 && !code.equals("ANY_KEY")) {
					Gdx.app.error(getClass().getSimpleName(), "The key code '" + code + "' is unknown");
					//prevent mapping an unknown key to ANY_KEY
					keycode = -2;
				}
				keyInsertConsumer.accept(elementName, keycode);
			}
			
			if (buttonElement != null) {
				String code = buttonElement.getAttribute("code");
				int buttoncode = buttonValueOf(code);
				if (buttoncode == -1) {
					Gdx.app.error(getClass().getSimpleName(), "The button code '" + code + "' is unknown");
				}
				buttonInsertConsumer.accept(elementName, buttoncode);
			}
		}
	}
	
	private int buttonValueOf(String code) {
		if (BUTTONS.containsKey(code.toLowerCase())) {
			return BUTTONS.get(code.toLowerCase());
		}
		return -1;
	}
	
	public String getName() {
		return name;
	}
	
	public void addListener(InputActionListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(InputActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Checks whether a key that is has the given state name is pressed.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if the state key is pressed. False otherwise.
	 */
	public boolean isKeyDown(String state) {
		Integer keycode = keyStates.get(state);
		
		if (keycode != null) {
			return Gdx.input.isKeyPressed(keycode);
		}
		return false;
	}
	
	/**
	 * Checks whether a button that is has the given state name is pressed.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if the state button is pressed. False otherwise.
	 */
	public boolean isButtonPressed(String state) {
		Integer buttoncode = buttonStates.get(state);
		
		if (buttoncode != null) {
			return Gdx.input.isButtonPressed(buttoncode);
		}
		return false;
	}
	
	/**
	 * Checks whether a key or a button that is has the given state name is pressed.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if the state key or button is pressed. False otherwise.
	 */
	public boolean isStateActive(String state) {
		return isKeyDown(state) || isButtonPressed(state);
	}
	
	protected boolean keyDown(int keycode) {
		if (isEventHandled(InputEvent.KEY_DOWN)) {
			String action = keyActions.get(keycode);
			return invokeListeners(action, Type.KEY_DOWN, new Parameters().setKeycode(keycode));
		}
		return false;
	}
	
	protected boolean keyUp(int keycode) {
		if (isEventHandled(InputEvent.KEY_UP)) {
			String action = keyActions.get(keycode);
			return invokeListeners(action, Type.KEY_UP, new Parameters().setKeycode(keycode));
		}
		return false;
	}
	
	protected boolean keyTyped(char character) {
		if (isEventHandled(InputEvent.KEY_TYPED)) {
			return invokeListeners(Type.KEY_TYPED, new Parameters().setCharacter(character));
		}
		return false;
	}
	
	protected boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (isEventHandled(InputEvent.TOUCH_DOWN)) {
			String action = buttonActions.get(button);
			return invokeListeners(action, Type.BUTTON_PRESSED,
					new Parameters().setScreenX(screenX).setScreenY(screenY).setPointer(pointer).setButton(button));
		}
		return false;
	}
	
	protected boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (isEventHandled(InputEvent.TOUCH_UP)) {
			String action = buttonActions.get(button);
			return invokeListeners(action, Type.BUTTON_RELEASED,
					new Parameters().setScreenX(screenX).setScreenY(screenY).setPointer(pointer).setButton(button));
		}
		return false;
	}
	
	protected boolean touchDragged(int screenX, int screenY, int pointer) {
		if (isEventHandled(InputEvent.TOUCH_DRAGGED)) {
			return invokeListeners(Type.MOUSE_DRAGGED, new Parameters().setScreenX(screenX).setScreenY(screenY).setPointer(pointer));
		}
		return false;
	}
	
	protected boolean mouseMoved(int screenX, int screenY) {
		if (isEventHandled(InputEvent.MOUSE_MOVED)) {
			return invokeListeners(Type.MOUSE_MOVED, new Parameters().setScreenX(screenX).setScreenY(screenY));
		}
		return false;
	}
	
	protected boolean scrolled(int amount) {
		if (isEventHandled(InputEvent.SCROLLED)) {
			return invokeListeners(Type.SCROLLED, new Parameters().setScrollAmount(amount));
		}
		return false;
	}
	
	private boolean isEventHandled(InputEvent inputEvent) {
		return handledEvents.get(inputEvent, false);
	}
	
	private boolean invokeListeners(String action, Type type, Parameters parameters) {
		if (action != null) {
			for (InputActionListener listener : listeners) {
				if (listener.onAction(action, type, parameters)) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean invokeListeners(Type type, Parameters parameters) {
		for (InputActionListener listener : listeners) {
			if (listener.onAction("", type, parameters)) {
				return true;
			}
		}
		return false;
	}
}