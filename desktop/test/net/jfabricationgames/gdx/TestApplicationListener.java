package net.jfabricationgames.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import net.jfabricationgames.gdx.input.InputProfileTest;

public class TestApplicationListener extends ApplicationAdapter {
	
	private static TestApplicationListener instance;
	
	public InputMultiplexer inputMultiplexer;
	
	public static synchronized TestApplicationListener getInstance() {
		if (instance == null) {
			instance = new TestApplicationListener();
		}
		return instance;
	}
	
	private TestApplicationListener() {
		
	}
	
	@Override
	public void create() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		new InputProfileTest();
	}
}