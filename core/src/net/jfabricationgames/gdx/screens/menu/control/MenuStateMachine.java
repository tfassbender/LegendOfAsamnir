package net.jfabricationgames.gdx.screens.menu.control;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class MenuStateMachine<T extends ControlledMenu<T>> {
	
	public enum InputDirection {
		UP, DOWN, LEFT, RIGHT;
	}
	
	private ControlledMenu<T> menu;
	private String[] stateConfigFiles;
	
	private ObjectMap<String, MenuState> states;
	private String currentState;
	
	public MenuStateMachine(ControlledMenu<T> menu, String... stateConfigFiles) {
		this.menu = menu;
		this.stateConfigFiles = stateConfigFiles;
		
		loadStates();
	}
	
	@SuppressWarnings("unchecked")
	private void loadStates() {
		Json json = new Json();
		states = new ObjectMap<>();
		for (String stateConfig : stateConfigFiles) {
			FileHandle statesConfigFileHandle = Gdx.files.internal(stateConfig);
			ObjectMap<String, MenuState> newStates = json.fromJson(ObjectMap.class, MenuState.class, statesConfigFileHandle);
			addNewStates(newStates);
			addAdditionalStateTransitions(newStates);
		}
	}
	
	private void addNewStates(ObjectMap<String, MenuState> newStates) {
		for (String key : newStates.keys()) {
			if (states.containsKey(key)) {
				throw new IllegalStateException("The state config files contain a duplicate key: " + key);
			}
		}
		
		for (String key : newStates.keys()) {
			if (newStates.get(key).select == null) {
				throw new IllegalStateException("A menu state does not configure a 'select' parameter. State name: " + key);
			}
		}
		
		states.putAll(newStates);
	}
	
	private void addAdditionalStateTransitions(ObjectMap<String, MenuState> newStates) {
		for (String toState : newStates.keys()) {
			MenuState state = newStates.get(toState);
			if (state.reachableFrom != null) {
				for (AdditionalStateTransition additionalTransition : state.reachableFrom) {
					MenuState fromState = states.get(additionalTransition.fromState);
					
					if (fromState == null) {
						throw new IllegalStateException("The configured 'fromState' in the reachableFrom list of the state '" + toState
								+ "' is not known or has not yet been loaded");
					}
					
					addAdditionalTransition(toState, additionalTransition, fromState);
				}
			}
		}
	}
	
	private void addAdditionalTransition(String toState, AdditionalStateTransition additionalTransition, MenuState fromState) {
		boolean transitionAlreadySet = false;
		switch (additionalTransition.direction) {
			case UP:
				fromState.up = toState;
				if (fromState.up == null) {
					transitionAlreadySet = true;
				}
				break;
			case DOWN:
				fromState.down = toState;
				if (fromState.down == null) {
					transitionAlreadySet = true;
				}
				break;
			case LEFT:
				fromState.left = toState;
				if (fromState.left == null) {
					transitionAlreadySet = true;
				}
				break;
			case RIGHT:
				fromState.right = toState;
				if (fromState.right != null) {
					transitionAlreadySet = true;
				}
				break;
			default:
				throw new IllegalStateException("Unexpected direction in reachableFrom list of state '" + toState + "'");
		}
		if (transitionAlreadySet) {
			throw new IllegalStateException("The transition '" + additionalTransition.direction + "' of the state '" + fromState
					+ "' is already set and can not be overwritten by the reachableFrom list of the state '" + toState + "'.");
		}
	}
	
	public void changeState(InputDirection inputDirection) {
		MenuState state = states.get(currentState);
		String nextState;
		switch (inputDirection) {
			case UP:
				nextState = state.up;
				break;
			case DOWN:
				nextState = state.down;
				break;
			case LEFT:
				nextState = state.left;
				break;
			case RIGHT:
				nextState = state.right;
				break;
			default:
				throw new IllegalStateException("Unexpected InputDirection: " + inputDirection);
		}
		
		if (nextState != null) {
			changeState(nextState);
		}
	}
	
	public void changeToInitialState() {
		for (String stateName : states.keys()) {
			MenuState state = states.get(stateName);
			if (state.initial) {
				changeState(stateName);
				return;
			}
		}
		throw new IllegalStateException("No initial state was defined. Config files were: " + Arrays.toString(stateConfigFiles));
	}
	
	public void changeState(String stateName) {
		String leavingState = currentState;
		currentState = stateName;
		menu.setFocusTo(stateName, leavingState);
	}
	
	public void executeSelectActionOnCurrentState() {
		MenuState state = states.get(currentState);
		executeStateSelectAction(state);
	}
	
	private void executeStateSelectAction(MenuState state) {
		menu.invokeMethod(state.select);
	}
}
