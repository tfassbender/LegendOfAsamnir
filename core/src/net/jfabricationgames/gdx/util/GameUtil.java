package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameUtil {
	
	private GameUtil() {}
	
	public static void runDelayed(Runnable runnable, float delayTime) {
		Thread delayThread = new Thread(() -> {
			try {
				Thread.sleep((int) (delayTime * 1000));
				runnable.run();
			}
			catch (InterruptedException e) {
				Gdx.app.error(GameUtil.class.getSimpleName(), "Delay thread - sleep interrupted");
			}
		});
		delayThread.setDaemon(true);
		delayThread.start();
	}
	
	public static Color getColorFromRGB(String rgb) {
		return getColorFromRGB(rgb, null);
	}
	public static Color getColorFromRGB(String rgb, Color defaultColor) {
		if (rgb == null || rgb.length() != 7) {
			return defaultColor;
		}
		
		String redHex = rgb.substring(1, 3);
		String greenHex = rgb.substring(3, 5);
		String blueHex = rgb.substring(5, 7);
		
		float red = Integer.parseInt(redHex, 16);
		float green = Integer.parseInt(greenHex, 16);
		float blue = Integer.parseInt(blueHex, 16);
		
		return new Color(red / 255f, green / 255f, blue / 255f, 1f);
	}
}
