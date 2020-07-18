package net.jfabricationgames.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.desktop.util.TexturePackingTool;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		packTextures();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "DwarfScrollerGDX";
		config.width = 1200;
		config.height = 800;
		config.vSyncEnabled = true;
		new LwjglApplication(DwarfScrollerGame.getInstance(), config);
	}
	
	private static void packTextures() {
		new TexturePackingTool().packTextures();
	}
}
