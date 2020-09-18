package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.Gdx;

public abstract class GameUtils {
	
	public static void runDelayed(Runnable runnable, float delayTime) {
		Thread delayThread = new Thread(() -> {
			try {
				Thread.sleep((int) (delayTime * 1000));
				runnable.run();
			}
			catch (InterruptedException e) {
				Gdx.app.error(GameUtils.class.getSimpleName(), "Delay thread - sleep interrupted");
			}
		});
		delayThread.setDaemon(true);
		delayThread.start();
	}
}
