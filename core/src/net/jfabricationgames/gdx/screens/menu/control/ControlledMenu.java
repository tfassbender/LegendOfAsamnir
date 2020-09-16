package net.jfabricationgames.gdx.screens.menu.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;

public abstract class ControlledMenu<T extends ControlledMenu<T>> extends ScreenAdapter {
	
	protected MenuStateMachine<T> stateMachine;
	
	public ControlledMenu(String statesConfig) {
		stateMachine = new MenuStateMachine<T>(this, statesConfig);
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
	
	protected abstract void setFocusTo(String stateName);
}
