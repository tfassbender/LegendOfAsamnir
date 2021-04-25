package net.jfabricationgames.gdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.jfabricationgames.gdx.input.InputListeningSample;

public class InputListeningSampleApplicationStarter {
	
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "InputListeningSample";
		new LwjglApplication(new InputListeningSample(), config);
	}
}
