package net.jfabricationgames.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;

public class InputManager {
	
	private static InputManager instance;
	
	public static synchronized InputManager getInstance() {
		if (instance == null) {
			instance = new InputManager();
		}
		return instance;
	}
	
	private InputProfile inputProfile;
	
	private InputManager() {}
	
	public void createInputProfile(FileHandle inputProfileFile, InputMultiplexer inputMultiplexer) {
		this.inputProfile = new InputProfile(inputProfileFile, inputMultiplexer);
	}
	
	public InputContext changeInputContext(String contextName) {
		Gdx.app.debug(getClass().getSimpleName(), "Changing InputContext to \"" + contextName + "\"");
		inputProfile.setContext(contextName);
		return inputProfile.getContext();
	}
	
	public InputProfile getInputProfile() {
		return inputProfile;
	}
	
	public InputContext getInputContext() {
		return inputProfile.getContext();
	}
}
