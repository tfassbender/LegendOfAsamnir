package net.jfabricationgames.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader.Element;

import net.jfabricationgames.gdx.input.InputContext.AxisThresholdPair;
import net.jfabricationgames.gdx.input.struct.AxisThreshold;
import net.jfabricationgames.gdx.input.struct.PlayerAxis;
import net.jfabricationgames.gdx.input.struct.PlayerValue;

public class InputContextLoader {
	
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
		EVENTS, //
		STATES, //
		ACTIONS, //
		CONTROLLER_AXES;
	}
	
	private InputContext context;
	
	private ContextType readingContextType;
	
	protected InputContextLoader(InputContext context) {
		this.context = context;
	}
	
	protected void load(Element contextRoot) {
		try {
			context.name = contextRoot.getAttribute("name");
			Gdx.app.log(getClass().getSimpleName(), "Loading context: " + context.name);
			
			readingContextType = ContextType.EVENTS;
			Element eventsElement = contextRoot.getChildByName("events");
			loadEvents(eventsElement);
			
			readingContextType = ContextType.STATES;
			Element statesElement = contextRoot.getChildByName("states");
			loadElements(statesElement);
			
			readingContextType = ContextType.ACTIONS;
			Element actionsElement = contextRoot.getChildByName("actions");
			loadElements(actionsElement);
			
			readingContextType = ContextType.CONTROLLER_AXES;
			Element controllerAxes = contextRoot.getChildByName("controllerAxes");
			loadControllerAxes(controllerAxes);
			
			readingContextType = null;
		}
		catch (Exception e) {
			Gdx.app.error("InputContext", "Error loading context element", e);
		}
	}
	
	/**
	 * Load the events from the profile or set the defaults if no events are defined.
	 * 
	 * Loads: <context><events>...</events><context>
	 */
	private void loadEvents(Element eventsElement) {
		if (eventsElement != null && eventsElement.getChildCount() > 0) {
			for (InputEvent inputEvent : InputEvent.values()) {
				Element eventElement = eventsElement.getChildByName(inputEvent.name());
				loadEventElement(inputEvent, eventElement);
			}
		}
		else {
			//there are no events defined -> use the defaults on all events
			for (InputEvent inputEvent : InputEvent.values()) {
				context.handledEvents.put(inputEvent, inputEvent.isDefault());
			}
		}
	}
	
	/**
	 * Loads: <context><events><__event_name_here__ listened="true/false" /></events><context>
	 */
	private void loadEventElement(InputEvent inputEvent, Element eventElement) {
		if (eventElement != null) {
			if (eventElement.hasAttribute("listening")) {
				boolean value = Boolean.parseBoolean(eventElement.getAttribute("listening"));
				checkInputEventDuplicate(inputEvent, value, eventElement);
				context.handledEvents.put(inputEvent, value);
			}
			else {
				//the event has no "listening" attribute, so it is assumed to be true
				checkInputEventDuplicate(inputEvent, true, eventElement);
				context.handledEvents.put(inputEvent, true);
			}
		}
	}
	
	private void checkInputEventDuplicate(InputEvent inputEvent, boolean newValue, Element eventElement) {
		if (context.handledEvents.containsKey(inputEvent)) {
			logDuplicateErrorMessage(inputEvent.name(), context.handledEvents.get(inputEvent), newValue, eventElement);
		}
	}
	
	private void logDuplicateErrorMessage(Object key, Object existingValue, Object newValue, Element eventElement) {
		Gdx.app.error(getClass().getName(), "Duplicate key. The existing entry will be overwritten. Key: " + key + "   Existing Value: "
				+ existingValue + "   New Value: " + newValue + "   Element: " + eventElement);
	}
	
	/**
	 * Load the state and action elements for keys, mouse buttons, and controllers.
	 * 
	 * Loads: <context><states>...</states><context> AND <context><actions>...</actions><context>
	 */
	private void loadElements(Element contextElement) {
		int numStates = contextElement != null ? contextElement.getChildCount() : 0;
		
		for (int i = 0; i < numStates; i++) {
			Element element = contextElement.getChild(i);
			String elementName = element.getAttribute("name");
			
			Array<Element> keyElements = element.getChildrenByName("key");
			Array<Element> buttonElements = element.getChildrenByName("button");
			Element controllerElement = element.getChildByName("controller");
			
			for (Element keyElement : keyElements) {
				loadKeyElement(elementName, keyElement);
			}
			for (Element buttonElement : buttonElements) {
				loadButtonElement(elementName, buttonElement);
			}
			loadControllerElement(elementName, controllerElement);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><key code="..."/></state></states><context>
	 */
	private void loadKeyElement(String elementName, Element keyElement) {
		if (keyElement != null) {
			String code = keyElement.getAttribute("code");
			int keycode = Keys.valueOf(code);
			
			if (keycode == -1 && !code.equals("ANY_KEY")) {
				Gdx.app.error(getClass().getSimpleName(), "The key code '" + code + "' is unknown");
				//prevent mapping an unknown key to ANY_KEY
				keycode = -2;
			}
			
			if (readingContextType == ContextType.STATES) {
				checkDuplicateKey(context.keyStates, elementName, keyElement, keycode);
				context.keyStates.put(elementName, keycode);
			}
			else if (readingContextType == ContextType.ACTIONS) {
				checkDuplicateKey(context.keyActions, keycode, keyElement, elementName);
				context.keyActions.put(keycode, elementName);
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + readingContextType);
			}
		}
	}
	
	private <T> void checkDuplicateKey(ArrayMap<T, ?> map, T key, Element keyElement, Object newValue) {
		if (map.containsKey(key) && map.get(key) != newValue) {
			logDuplicateErrorMessage(key, map.get(key), newValue, keyElement);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><button code="..."/></state></states><context>
	 */
	private void loadButtonElement(String elementName, Element buttonElement) {
		if (buttonElement != null) {
			String code = buttonElement.getAttribute("code");
			int buttoncode = buttonValueOf(code);
			
			if (buttoncode == -1) {
				Gdx.app.error(getClass().getSimpleName(), "The button code '" + code + "' is unknown");
			}
			
			if (readingContextType == ContextType.STATES) {
				checkDuplicateKey(context.buttonStates, elementName, buttonElement, buttoncode);
				context.buttonStates.put(elementName, buttoncode);
			}
			else if (readingContextType == ContextType.ACTIONS) {
				checkDuplicateKey(context.buttonStates, elementName, buttonElement, buttoncode);
				context.buttonActions.put(buttoncode, elementName);
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + readingContextType);
			}
		}
	}
	
	private int buttonValueOf(String code) {
		if (BUTTONS.containsKey(code.toLowerCase())) {
			return BUTTONS.get(code.toLowerCase());
		}
		return -1;
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="...">...</controller></state></states><context>
	 */
	private void loadControllerElement(String elementName, Element controllerElement) {
		if (controllerElement != null) {
			int player = InputContext.CONTROLLER_ANY_PLAYER;
			if (controllerElement.hasAttribute("player")) {
				player = Integer.parseInt(controllerElement.getAttribute("player"));
			}
			
			Element buttonElement = controllerElement.getChildByName("button");
			Element axisElement = controllerElement.getChildByName("axis");
			
			loadControllerButton(elementName, player, buttonElement);
			loadControllerAxisElement(elementName, player, axisElement);
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="..."><button code="..." /></controller></state></states><context>
	 */
	private void loadControllerButton(String elementName, int player, Element controllerButtonElement) {
		if (controllerButtonElement != null) {
			String code = controllerButtonElement.getAttribute("code");
			int buttoncode = Integer.parseInt(code);
			
			PlayerValue key = new PlayerValue(player, buttoncode);
			if (readingContextType == ContextType.STATES) {
				checkDuplicateKey(context.controllerButtonStates, elementName, controllerButtonElement, key);
				context.controllerButtonStates.put(elementName, key);
			}
			else if (readingContextType == ContextType.ACTIONS) {
				checkDuplicateKey(context.controllerButtonActions, key, controllerButtonElement, elementName);
				context.controllerButtonActions.put(key, elementName);
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + readingContextType);
			}
		}
	}
	
	/**
	 * Loads: <context><states><state name="..."><controller player="..."><axis code="..." threshold="..." /></controller></state></states><context>
	 */
	private void loadControllerAxisElement(String elementName, int player, Element controllerAxisElement) {
		if (controllerAxisElement != null) {
			int axisCode = Integer.parseInt(controllerAxisElement.getAttribute("code"));
			float threshold = Float.parseFloat(controllerAxisElement.getAttribute("threshold"));
			
			if (Math.abs(threshold) < InputProfile.CONTROLLER_AXIS_DEAD_ZONE) {
				throw new IllegalArgumentException("The absolute value of the 'threshold' attribute must be greater than 0.01");
			}
			
			if (readingContextType == ContextType.STATES) {
				AxisThreshold axisThreshold = new AxisThreshold(axisCode, player, threshold);
				checkDuplicateKey(context.controllerAxisStates, elementName, controllerAxisElement, axisThreshold);
				context.controllerAxisStates.put(elementName, axisThreshold);
			}
			else if (readingContextType == ContextType.ACTIONS) {
				PlayerValue key = new PlayerValue(player, axisCode);
				AxisThreshold axisThreshold = new AxisThreshold(axisCode, player, threshold, elementName);
				
				AxisThresholdPair thresholdPair = context.controllerAxisActions.get(key);
				if (thresholdPair == null) {
					thresholdPair = new AxisThresholdPair();
				}
				
				if (threshold < 0) {
					thresholdPair.lowerThreshold = axisThreshold;
				}
				else {
					thresholdPair.upperThreshold = axisThreshold;
				}
				
				checkDuplicateKey(context.controllerAxisActions, key, controllerAxisElement, thresholdPair);
				context.controllerAxisActions.put(key, thresholdPair);
			}
			else {
				throw new IllegalStateException("unexpected ContextType: " + readingContextType);
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
		checkDuplicateKey(context.namedAxes, elementName, axisElement, playerAxis);
		context.namedAxes.put(elementName, playerAxis);
	}
}
