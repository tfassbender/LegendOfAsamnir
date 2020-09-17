package net.jfabricationgames.gdx.input;

import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader.Element;

import net.jfabricationgames.gdx.input.InputActionListener.Parameters;
import net.jfabricationgames.gdx.input.InputActionListener.Type;
import net.jfabricationgames.gdx.input.struct.AxisThreshold;
import net.jfabricationgames.gdx.input.struct.PlayerAxis;
import net.jfabricationgames.gdx.input.struct.PlayerValue;

public class InputContext {
	
	public static final int CONTROLLER_ANY_PLAYER = -1;
	
	protected String name;
	
	protected ArrayMap<InputEvent, Boolean> handledEvents;
	
	protected ArrayMap<String, Integer> keyStates;
	protected ArrayMap<Integer, String> keyActions;
	protected ArrayMap<String, Integer> buttonStates;
	protected ArrayMap<Integer, String> buttonActions;
	protected ArrayMap<String, PlayerValue> controllerButtonStates;
	protected ArrayMap<PlayerValue, String> controllerButtonActions;
	protected ArrayMap<String, Array<PlayerValue>> controllerPovStates;
	protected ArrayMap<PlayerValue, Array<String>> controllerPovActions;
	protected ArrayMap<String, AxisThreshold> controllerAxisStates;
	protected ArrayMap<PlayerValue, AxisThresholdPair> controllerAxisActions;
	protected ArrayMap<String, PlayerAxis> namedAxes;
	
	private PriorityQueue<InputActionListener> listeners;
	
	/**
	 * To store {@link AxisThreshold} objects for controller axis actions. One for a positive axis value and one for a negative axis value.
	 */
	protected static class AxisThresholdPair {
		
		public AxisThreshold lowerThreshold;
		public AxisThreshold upperThreshold;
	}
	
	public InputContext() {
		initializeMaps();
		listeners = new PriorityQueue<>((l1, l2) -> Integer.compare(l2.getInputPriority().priority, l1.getInputPriority().priority));
	}
	
	private void initializeMaps() {
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
	}
	
	protected void load(Element contextRoot) {
		clearInputMaps();
		new InputContextLoader(this).load(contextRoot);
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
			AxisThresholdPair axisThreshold = controllerAxisActions.get(new PlayerValue(player, axisCode));
			if (axisThreshold != null) {
				float axisValue = getAxisValue(player, axisCode);
				processControllerAxisThresholdPassed(player, axisThreshold.lowerThreshold, axisValue);
				processControllerAxisThresholdPassed(player, axisThreshold.upperThreshold, axisValue);
			}
		}
		return false;
	}
	
	private void processControllerAxisThresholdPassed(int player, AxisThreshold axisThreshold, float axisValue) {
		if (axisThreshold != null) {
			boolean thresholdPassed = axisThreshold.isThresholdPassed(axisValue);
			if (thresholdPassed && !axisThreshold.thresholdPassed) {
				invokeListeners(axisThreshold.stateName, Type.CONTROLLER_AXIS_THRESHOLD_PASSED,
						new Parameters().setPlayer(player).setAxisValue(axisValue).setAxisThreshold(axisThreshold.threshold));
			}
			axisThreshold.thresholdPassed = thresholdPassed;			
		}
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