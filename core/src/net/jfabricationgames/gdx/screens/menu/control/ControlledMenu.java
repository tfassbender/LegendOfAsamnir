package net.jfabricationgames.gdx.screens.menu.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;

import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.screens.menu.control.MenuStateMachine.InputDirection;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public abstract class ControlledMenu<T extends ControlledMenu<T>> extends ScreenAdapter implements InputActionListener {
	
	public static final String ACTION_SELECT = "select";
	public static final String ACTION_SELECTION_RIGHT = "right";
	public static final String ACTION_SELECTION_LEFT = "left";
	public static final String ACTION_SELECTION_DOWN = "down";
	public static final String ACTION_SELECTION_UP = "up";
	
	protected MenuStateMachine<T> stateMachine;
	
	private SoundSet soundSet;
	
	public ControlledMenu(String statesConfig) {
		stateMachine = new MenuStateMachine<T>(this, statesConfig);
		soundSet = SoundManager.getInstance().loadSoundSet("menu");
	}
	
	public void invokeMethod(String methodName) {
		try {
			Method method = getClass().getMethod(methodName);
			method.invoke(this);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Gdx.app.error(getClass().getSimpleName(), "Couldn't invoke menu controll method: " + methodName, e);
		}
	}
	
	protected abstract void setFocusTo(String stateName, String leavingState);
	
	public abstract void showMenu();
	
	protected void playMenuSound(String name) {
		soundSet.playSound(name);
	}
	
	protected boolean isEventTypeHandled(Type type) {
		return type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED || type == Type.CONTROLLER_AXIS_THRESHOLD_PASSED
				|| type == Type.CONTROLLER_POV_CHANGED;
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_SELECTION_UP) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.UP);
		}
		if (action.equals(ACTION_SELECTION_DOWN) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.DOWN);
		}
		if (action.equals(ACTION_SELECTION_LEFT) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.LEFT);
		}
		if (action.equals(ACTION_SELECTION_RIGHT) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.RIGHT);
		}
		if (action.equals(ACTION_SELECT) && isEventTypeHandled(type)) {
			stateMachine.executeSelectActionOnCurrentState();
		}
		return false;
	}
	
	@Override
	public Priority getInputPriority() {
		return Priority.MENU;
	}
}
