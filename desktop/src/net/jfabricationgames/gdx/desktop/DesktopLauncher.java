package net.jfabricationgames.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.desktop.log.LogConfiguration;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = createApplicationConfiguration();
		new LwjglApplication(DwarfScrollerGame.getInstance(), config);
		
		configureLog();
	}

	private static LwjglApplicationConfiguration createApplicationConfiguration() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "DwarfScrollerGDX";
		config.width = 1200;
		config.height = 800;
		config.vSyncEnabled = true;
		return config;
	}

	private static void configureLog() {
		new LogConfiguration().configureLog();
	}
}
