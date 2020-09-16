package net.jfabricationgames.gdx.screens.menu.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class MenuStateMachine<T extends ControlledMenu<T>> {
	
	public enum InputDirection {
		UP, DOWN, LEFT, RIGHT;
	}
	
	private ControlledMenu<T> menu;
	private String statesConfig;
	
	private ObjectMap<String, MenuState> states;
	private String currentState;
	
	public MenuStateMachine(ControlledMenu<T> menu, String statesConfig) {
		this.menu = menu;
		this.statesConfig = statesConfig;
		
		loadStates();
	}
	
	@SuppressWarnings("unchecked")
	private void loadStates() {
		Json json = new Json();
		FileHandle statesConfigFileHandle = Gdx.files.internal(statesConfig);
		states = json.fromJson(ObjectMap.class, MenuState.class, statesConfigFileHandle);
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
		throw new IllegalStateException("No initial state was defined. Config file was: " + statesConfig);
	}
	
	private void changeState(String stateName) {
		currentState = stateName;
		menu.setFocusTo(stateName);
	}
	
	public void selectActionOnCurrentState() {
		MenuState state = states.get(currentState);
		executeStateSelectAction(state);
	}
	
	private void executeStateSelectAction(MenuState state) {
		menu.invokeMethod(state.select);
	}
}
