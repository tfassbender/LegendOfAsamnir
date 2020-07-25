package net.jfabricationgames.gdx.input;

import java.util.function.BiConsumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlReader.Element;

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
		listeners = new ObjectSet<>();
	}
	
	public void load(Element contextElement) {
		keyActions.clear();
		keyStates.clear();
		buttonStates.clear();
		buttonActions.clear();
		
		try {
			name = contextElement.getAttribute("name");
			
			Element statesElement = contextElement.getChildByName("states");
			loadElements(statesElement, keyStates::put, buttonStates::put);
			
			Element actionsElement = contextElement.getChildByName("actions");
			loadElements(actionsElement, (name, code) -> keyActions.put(code, name), (name, code) -> buttonActions.put(code, name));
		}
		catch (Exception e) {
			Gdx.app.error("InputContext", "Error loading context element", e);
		}
	}
	
	private void loadElements(Element rootElement, BiConsumer<String, Integer> keyInsertConsumer, BiConsumer<String, Integer> buttonInsertConsumer) {
		int numStates = rootElement != null ? rootElement.getChildCount() : 0;
		
		for (int i = 0; i < numStates; ++i) {
			Element element = rootElement.getChild(i);
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
		boolean processed = false;
		String action = keyActions.get(keycode);
		
		if (action != null) {
			for (InputActionListener listener : listeners) {
				processed = listener.onAction(action);
				if (processed) {
					break;
				}
			}
		}
		return processed;
	}
	
	protected boolean keyUp(int keycode) {
		return false;
	}
	
	protected boolean keyTyped(char character) {
		return false;
	}
	
	protected boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean processed = false;
		String action = buttonActions.get(button);
		
		if (action != null) {
			for (InputActionListener listener : listeners) {
				processed = listener.onButtonAction(action, screenX, screenY, pointer);
				if (processed) {
					break;
				}
			}
		}
		
		return processed;
	}
	
	protected boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	protected boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	protected boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	protected boolean scrolled(int amount) {
		return false;
	}
}