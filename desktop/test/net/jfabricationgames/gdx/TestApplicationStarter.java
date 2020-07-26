package net.jfabricationgames.gdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TestApplicationStarter {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "InputProfileTest";
		new LwjglApplication(TestApplicationListener.getInstance(), config);
	}
}
