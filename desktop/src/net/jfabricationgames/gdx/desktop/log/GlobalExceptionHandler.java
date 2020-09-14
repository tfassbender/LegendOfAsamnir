package net.jfabricationgames.gdx.desktop.log;

import com.badlogic.gdx.Gdx;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Gdx.app.error("UNCAUGHT_EXCEPTION", "Uncaught exception in thread [" + thread.getName() + "]", throwable);
	}
}
