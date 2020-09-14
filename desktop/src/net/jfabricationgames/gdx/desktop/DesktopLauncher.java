package net.jfabricationgames.gdx.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.desktop.log.LogAdapter;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "DwarfScrollerGDX";
		config.width = 1200;
		config.height = 800;
		config.vSyncEnabled = true;
		new LwjglApplication(DwarfScrollerGame.getInstance(), config);
		
		setLogAdapter();
	}
	
	private static void setLogAdapter() {
		LogAdapter logAdapter = new LogAdapter();
		logAdapter.log("APPLICATION_START", "################################################################################");
		logAdapter.log("APPLICATION_START", "#                                                                              #");
		logAdapter.log("APPLICATION_START", "#                      Starting DwarfScrollerGDX                               #");
		logAdapter.log("APPLICATION_START", "#                                                                              #");
		logAdapter.log("APPLICATION_START", "################################################################################");
		
		//set the application logger after the application is started (otherwise Gdx.app is null)
		Gdx.app.setApplicationLogger(logAdapter);
	}
}
