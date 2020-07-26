package net.jfabricationgames.gdx.input;

import java.util.Objects;
import java.util.function.BiConsumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlReader.Element;

import net.jfabricationgames.gdx.input.InputActionListener.Parameters;
import net.jfabricationgames.gdx.input.InputActionListener.Type;

public class InputContext {
	
	public static final int CONTROLLER_ANY_PLAYER = -1;
	
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
	
	private enum ContextType {
		STATES, //
		ACTIONS, //
		CONTROLLER_AXES;
	}
	
	private class PlayerAxis {
		
		public int axisCode;
		public int player;
		
		public PlayerAxis(int axisCode, int player) {
			this.axisCode = axisCode;
			this.player = player;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(axisCode, player);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PlayerAxis other = (PlayerAxis) obj;
			if (player != other.player && player != CONTROLLER_ANY_PLAYER && other.player != CONTROLLER_ANY_PLAYER)
				// if one of the players is CONTROLLER_ANY_PLAYER consider them as equal
				return false;
			return axisCode == other.axisCode;
		}
	}
	
	/**
	 * A threshold value for an axis of a specific player's controller.
	 */
	private class AxisThreshold extends PlayerAxis {
		
		public float threshold;
		public boolean thresholdPassed = false;
		public String stateName;
		
		public AxisThreshold(int axisCode, int player, float threshold) {
			super(axisCode, player);
			this.threshold = threshold;
		}
		public AxisThreshold(int axisCode, int player, float threshold, String stateName) {
			super(axisCode, player);
			this.threshold = threshold;
			this.stateName = stateName;
		}
		
		public boolean isThresholdPassed(float value) {
			return threshold < 0 && value < threshold || threshold > 0 && value > threshold;
		}
	}
	
	/**
	 * A value from the controller of a specific player.
	 */
	private class PlayerValue {
		
		public int player;
		public String value;
		public int intValue;
		
		public PlayerValue(int player, String value) {
			this.player = player;
			this.value = value;
		}
		public PlayerValue(int player, int intValue) {
			this.player = player;
			this.intValue = intValue;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PlayerValue other = (PlayerValue) obj;
			if (player != other.player && player != CONTROLLER_ANY_PLAYER && other.player != CONTROLLER_ANY_PLAYER)
				// if one of the players is CONTROLLER_ANY_PLAYER consider them as equal
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			}
			else if (!value.equals(other.value))
				return false;
			if (intValue != other.intValue)
				return false;
			return true;
		}
	}
	
	private String name;
	
	private ArrayMap<InputEvent, Boolean> handledEvents;
	
	private ArrayMap<String, Integer> keyStates;
	private ArrayMap<Integer, String> keyActions;
	private ArrayMap<String, Integer> buttonStates;
	private ArrayMap<Integer, String> buttonActions;
	private ArrayMap<String, PlayerValue> controllerButtonStates;
	private ArrayMap<PlayerValue, String> controllerButtonActions;
	private ArrayMap<String, Array<PlayerValue>> controllerPovStates;
	private ArrayMap<PlayerValue, Array<String>> controllerPovActions;
	private ArrayMap<String, AxisThreshold> controllerAxisStates;
	private ArrayMap<PlayerValue, AxisThreshold> controllerAxisActions;
	private ArrayMap<String, PlayerAxis> namedAxes;
	
	private ObjectSet<InputActionListener> listeners;
	
	public InputContext() {
		keyStates = new ArrayMap<>();
		keyActions = new ArrayMap<>();
		buttonStates = new ArrayMap<>();
		buttonActions = new ArrayMap<>();
		handledEvents = new ArrayMap<>();
		controllerButtonStates = new ArrayMap<>();
		controllerButtonActions = new ArrayMap<>();
		controllerPovStates = new ArrayMap<>();
		controllerPovActions = new ArrayMap<>();
		controllerAxisStates = new ArrayMap<>();
		controllerAxisActions = new ArrayMap<>();
		namedAxes = new ArrayMap<>();
		listeners = new ObjectSet<>();
	}
	
	public void load(Element contextRoot) {
		clearInputMaps();
		
		try {
			name = contextRoot.getAttribute("name");
			
			Element eventsElement = contextRoot.getChildByName("events");
			loadEvents(eventsElement);
			
			Element statesElement = contextRoot.getChildByName("states");
			loadElements(statesElement, ContextType.STATES, keyStates::put, buttonStates::put);
			
			Element actionsElement = contextRoot.getChildByName("actions");
			loadElements(actionsElement, ContextType.ACTIONS, (name, code) -> keyActions.put(code, name),
					(name, code) -> buttonActions.put(code, name));
			
			Element controllerAxes = contextRoot.getChildByName("controllerAxes");
			loadControllerAxes(controllerAxes);
		}
		catch (Exception e) {
			Gdx.app.error("InputContext", "Error loading context element", e);
		}
	}
	
	private void clearInputMaps() {
		keyActions.clear();
		keyStates.clear();
		buttonStates.clear();
		buttonActions.clear();
		handledEvents.clear();
		controllerButtonStates.clear();
		controllerButtonActions.clear();
		controllerPovStates.clear();
		controllerPovActions.clear();
		controllerAxisStates.clear();
		controllerAxisActions.clear();
		namedAxes.clear();
	}
	
	/**
	 * Load the events from the profile or set the defaults if no events are defined.
	 * 
	 * Loads: <context><events>...</events><context>
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
	 * Load the state and action elements for keys, mouse buttons, and controllers.
	 * 
	 * Loads: <context><states>...</states><context> AND <context><actions>...</actions><context>
	 */
	private void loadElements(Element contextElement, ContextType type, BiConsumer<String, Integer> keyInsertConsumer,
			BiConsumer<String, Integer> buttonInsertConsumer) {
		int numStates = contextElement != null ? contextElement.getChildCount() : 0;
		
		for (int i = 0; i < numStates; i++) {
			Element element = contextElement.getChild(i);
			String elementName = element.getAttribute("name");
			
			Element keyElement = element.getChildByName("key");
			Element buttonElement = element.getChildByName("button");
			Element controllerElement = element.getChildByName("controller");
			
			loadKeyElement(elementName, keyElement, keyInsertConsumer);
			loadButtonElement(elementName, buttonElement, buttonInsertConsumer);
			loadControllerElement(elementName, type, controllerElement);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><key code="..."/></state></states><context>
	 */
	private void loadKeyElement(String elementName, Element keyElement, BiConsumer<String, Integer> keyInsertConsumer) {
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
	}
	
	/**
	 * Loads: <context><states><state name="..."><button code="..."/></state></states><context>
	 */
	private void loadButtonElement(String elementName, Element buttonElement, BiConsumer<String, Integer> buttonInsertConsumer) {
		if (buttonElement != null) {
			String code = buttonElement.getAttribute("code");
			int buttoncode = buttonValueOf(code);
			if (buttoncode == -1) {
				Gdx.app.error(getClass().getSimpleName(), "The button code '" + code + "' is unknown");
			}
			buttonInsertConsumer.accept(elementName, buttoncode);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="...">...</controller></state></states><context>
	 */
	private void loadControllerElement(String elementName, ContextType type, Element controllerElement) {
		if (controllerElement != null) {
			int player = CONTROLLER_ANY_PLAYER;
			if (controllerElement.hasAttribute("player")) {
				player = Integer.parseInt(controllerElement.getAttribute("player"));
			}
			
			Element buttonElement = controllerElement.getChildByName("button");
			Element povElement = controllerElement.getChildByName("pov");
			Element axisElement = controllerElement.getChildByName("axis");
			
			loadControllerButton(elementName, player, type, buttonElement);
			loadControllerPov(elementName, player, type, povElement);
			loadControllerAxisElement(elementName, player, type, axisElement);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="..."><button code="..." /></controller></state></states><context>
	 */
	private void loadControllerButton(String elementName, int player, ContextType type, Element controllerButtonElement) {
		if (controllerButtonElement != null) {
			String code = controllerButtonElement.getAttribute("code");
			int buttoncode = Integer.parseInt(code);
			
			if (type == ContextType.STATES) {
				controllerButtonStates.put(elementName, new PlayerValue(player, buttoncode));
			}
			else if (type == ContextType.ACTIONS) {
				controllerButtonActions.put(new PlayerValue(player, buttoncode), elementName);
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + type);
			}
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="..."><pov directions="..." /></controller></state></states><context>
	 */
	private void loadControllerPov(String elementName, int player, ContextType type, Element controllerPovElement) {
		if (controllerPovElement != null) {
			String[] directions = controllerPovElement.getAttribute("directions").split(" ");
			//convert directions to PlayerValue objects (containing the direction and the player information)
			PlayerValue[] playerDirections = new PlayerValue[directions.length];
			for (int i = 0; i < directions.length; i++) {
				playerDirections[i] = new PlayerValue(player, directions[i]);
			}
			
			if (type == ContextType.STATES) {
				controllerPovStates.put(elementName, new Array<PlayerValue>(playerDirections));
			}
			else if (type == ContextType.ACTIONS) {
				for (PlayerValue playerDirection : playerDirections) {
					Array<String> actions = controllerPovActions.get(playerDirection);
					if (actions == null) {
						actions = new Array<String>();
						controllerPovActions.put(playerDirection, actions);
					}
					actions.add(elementName);
				}
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + type);
			}
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="..."><axis code="..." threshold="..." /></controller></state></states><context>
	 */
	private void loadControllerAxisElement(String elementName, int player, ContextType type, Element controllerAxisElement) {
		if (controllerAxisElement != null) {
			int axisCode = Integer.parseInt(controllerAxisElement.getAttribute("code"));
			float threshold = Float.parseFloat(controllerAxisElement.getAttribute("threshold"));
			if (Math.abs(threshold) < InputProfile.CONTROLLER_AXIS_DEAD_ZONE) {
				throw new IllegalArgumentException("The absolute value of the 'threshold' attribute must be greater than 0.01");
			}
			AxisThreshold axisThreshold = new AxisThreshold(axisCode, player, threshold);
			
			if (type == ContextType.STATES) {
				controllerAxisStates.put(elementName, axisThreshold);
			}
			else {
				controllerAxisActions.put(new PlayerValue(player, axisCode), new AxisThreshold(axisCode, player, threshold, elementName));
			}
		}
	}
	
	/**
	 * Loads: <context><controllerAxes name="...">...</controllerAxes><context>
	 */
	private void loadControllerAxes(Element controllerAxesElement) {
		int numStates = controllerAxesElement != null ? controllerAxesElement.getChildCount() : 0;
		
		for (int i = 0; i < numStates; i++) {
			Element element = controllerAxesElement.getChild(i);
			String elementName = element.getAttribute("name");
			
			Element axisElement = element.getChildByName("axis");
			
			loadAxisElement(elementName, axisElement);
		}
	}
	
	/**
	 * Loads: <context><controllerAxes name="..."><axis code="..." player="..." /></controllerAxes><context>
	 */
	private void loadAxisElement(String elementName, Element axisElement) {
		int axisCode = Integer.parseInt(axisElement.getAttribute("code"));
		int playerCode = Integer.parseInt(axisElement.getAttribute("player"));
		
		PlayerAxis playerAxis = new PlayerAxis(axisCode, playerCode);
		namedAxes.put(elementName, playerAxis);
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
	 * Checks whether a mouse button that is has the given state name is pressed.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if the state button is pressed. False otherwise.
	 */
	public boolean isMouseButtonPressed(String state) {
		Integer buttoncode = buttonStates.get(state);
		
		if (buttoncode != null) {
			return Gdx.input.isButtonPressed(buttoncode);
		}
		return false;
	}
	
	/**
	 * Checks whether a controller state is active, by checking all tags that are included in the controller tag (which can be a button, a pov and an
	 * axis). If at least one of the tag-states is active the method will return true.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if at least one of the controller's tag-states is active. False if all are inactive.
	 */
	public boolean isControllerStateActive(String state) {
		PlayerValue playerButton = controllerButtonStates.get(state);
		Array<PlayerValue> playerPovs = controllerPovStates.get(state);
		AxisThreshold playerAxis = controllerAxisStates.get(state);
		
		boolean stateActive = false;
		if (playerButton != null) {
			stateActive |= isControllerButtonPressed(playerButton);
		}
		if (playerPovs != null && !playerPovs.isEmpty()) {
			for (PlayerValue pov : playerPovs) {
				stateActive |= isControllerPovPressed(pov);
			}
		}
		if (playerAxis != null) {
			stateActive |= isControllerAxisStateActive(playerAxis);
		}
		
		return stateActive;
	}
	
	private boolean isControllerButtonPressed(PlayerValue playerButton) {
		int player = playerButton.player;
		int button = playerButton.intValue;
		
		if (player == CONTROLLER_ANY_PLAYER) {
			boolean buttonPressed = false;
			for (Controller controller : Controllers.getControllers()) {
				buttonPressed |= controller.getButton(button);
			}
			return buttonPressed;
		}
		else {
			if (Controllers.getControllers().size >= player) {
				return Controllers.getControllers().get(player - 1).getButton(button);
			}
		}
		
		return false;
	}
	
	private boolean isControllerPovPressed(PlayerValue playerPov) {
		int player = playerPov.player;
		String pov = playerPov.value;
		
		if (player == CONTROLLER_ANY_PLAYER) {
			boolean povPressed = false;
			for (Controller controller : Controllers.getControllers()) {
				povPressed |= controller.getPov(0) == PovDirection.valueOf(pov);
			}
			return povPressed;
		}
		else {
			if (Controllers.getControllers().size >= player) {
				return Controllers.getControllers().get(player - 1).getPov(0) == PovDirection.valueOf(pov);
			}
		}
		
		return false;
	}
	
	private boolean isControllerAxisStateActive(AxisThreshold playerAxis) {
		int player = playerAxis.player;
		int axis = playerAxis.axisCode;
		float threshold = playerAxis.threshold;
		
		if (player == CONTROLLER_ANY_PLAYER) {
			boolean axisStateActive = false;
			for (Controller controller : Controllers.getControllers()) {
				float axisValue = controller.getAxis(axis);
				if (threshold < 0) {
					axisStateActive |= axisValue <= threshold;
				}
				else {
					axisStateActive |= axisValue >= threshold;
				}
			}
			return axisStateActive;
		}
		else {
			if (Controllers.getControllers().size >= player) {
				float axisValue = Controllers.getControllers().get(player - 1).getAxis(axis);
				if (threshold < 0 && axisValue <= threshold || threshold > 0 && axisValue >= threshold) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks whether a key, a mouse button or a controller button or axis, that has the given state name is active.
	 * 
	 * @param state
	 *        The state name
	 * 		
	 * @return True if the state key or button is pressed, or if a controller axis is above a defined threshold. False otherwise.
	 */
	public boolean isStateActive(String state) {
		return isKeyDown(state) || isMouseButtonPressed(state) || isControllerStateActive(state);
	}
	
	/**
	 * Get the value of an axis by the name of the axis (that was provided in the XML-profile)
	 * 
	 * @param name
	 *        The axis name
	 * 		
	 * @return The value of the named axis.
	 */
	public float getControllerAxisValue(String name) {
		PlayerAxis playerAxis = namedAxes.get(name);
		return getAxisValue(playerAxis.player, playerAxis.axisCode);
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
	
	protected boolean controllerButtonDown(Controller controller, int buttonCode) {
		if (isEventHandled(InputEvent.CONTROLLER_BUTTON_DOWN)) {
			int player = getPlayerOfController(controller);
			String action = controllerButtonActions.get(new PlayerValue(player, buttonCode));
			return invokeListeners(action, Type.CONTROLLER_BUTTON_PRESSED, new Parameters().setButton(buttonCode).setPlayer(player));
		}
		return false;
	}
	
	protected boolean controllerButtonUp(Controller controller, int buttonCode) {
		if (isEventHandled(InputEvent.CONTROLLER_BUTTON_UP)) {
			int player = getPlayerOfController(controller);
			String action = controllerButtonActions.get(new PlayerValue(player, buttonCode));
			return invokeListeners(action, Type.CONTROLLER_BUTTON_RELEASED, new Parameters().setButton(buttonCode).setPlayer(player));
		}
		return false;
	}
	
	protected boolean controllerAxisMoved(Controller controller, int axisCode, float value) {
		if (isEventHandled(InputEvent.CONTROLLER_AXIS_THRESHOLD_PASSED)) {
			int player = getPlayerOfController(controller);
			AxisThreshold axisThreshold = controllerAxisActions.get(new PlayerValue(player, axisCode));
			if (axisThreshold != null) {
				float axisValue = getAxisValue(player, axisCode);
				boolean thresholdPassed = axisThreshold.isThresholdPassed(axisValue);
				if (thresholdPassed && !axisThreshold.thresholdPassed) {
					invokeListeners(axisThreshold.stateName, Type.CONTROLLER_AXIS_THRESHOLD_PASSED,
							new Parameters().setPlayer(player).setAxisValue(axisValue).setAxisThreshold(axisThreshold.threshold));
				}
				axisThreshold.thresholdPassed = thresholdPassed;				
			}
		}
		return false;
	}
	
	private float getAxisValue(int player, int axisCode) {
		if (Controllers.getControllers().size >= player) {
			return Controllers.getControllers().get(player - 1).getAxis(axisCode);
		}
		return 0;
	}
	
	protected boolean controllerPovMoved(Controller controller, int povCode, PovDirection value) {
		if (isEventHandled(InputEvent.CONTROLLER_POV_CHANGED)) {
			int player = getPlayerOfController(controller);
			Array<String> actions = controllerPovActions.get(new PlayerValue(player, value.name()));
			if (actions != null) {
				for (String action : actions) {
					invokeListeners(action, Type.CONTROLLER_POV_CHANGED, new Parameters().setPlayer(player).setPovDirection(value));
				}				
			}
		}
		return false;
	}
	
	private boolean isEventHandled(InputEvent inputEvent) {
		return handledEvents.get(inputEvent, false);
	}
	
	private int getPlayerOfController(Controller controller) {
		int player = Controllers.getControllers().indexOf(controller, false);
		//player count starts at 1 in the XML-profiles
		if (player > -1) {
			player++;
		}
		return player;
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