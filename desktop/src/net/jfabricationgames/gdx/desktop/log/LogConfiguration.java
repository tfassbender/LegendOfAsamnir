package net.jfabricationgames.gdx.desktop.log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class LogConfiguration {
	
	public void configureLog() {
		setLogAdapter();
		applyGlobalExceptionHandler();
	}
	
	private static void setLogAdapter() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		LogAdapter logAdapter = new LogAdapter();
		logAdapter.log("APPLICATION_START", "################################################################################");
		logAdapter.log("APPLICATION_START", "#                                                                              #");
		logAdapter.log("APPLICATION_START", "#                      Starting - Legend of Asamnir                            #");
		logAdapter.log("APPLICATION_START", "#                                                                              #");
		logAdapter.log("APPLICATION_START", "################################################################################");
		
		//set the application logger after the application is started (otherwise Gdx.app is null)
		Gdx.app.setApplicationLogger(logAdapter);
	}
	
	private void applyGlobalExceptionHandler() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}
}
