package net.jfabricationgames.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import net.jfabricationgames.gdx.DwarfScrollerGame;

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
		TexturePacker.Settings textureSettings = new TexturePacker.Settings();
		textureSettings.maxWidth = 4096;
		textureSettings.maxHeight = 4096;
		textureSettings.edgePadding = false;
		textureSettings.duplicatePadding = false;
		textureSettings.filterMin = TextureFilter.Linear;
		textureSettings.filterMag = TextureFilter.Linear;
		
		TexturePacker.process(textureSettings, "dwarf/dwarf_left_attack", "dwarf/packed", "dwarf_left_attack");
		TexturePacker.process(textureSettings, "dwarf/dwarf_left_run", "dwarf/packed", "dwarf_left_run");
	}
}
