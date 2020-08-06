package net.jfabricationgames.gdx.desktop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Json;

public class TexturePackingTool {
	
	public static final boolean REPACK_TEXTURES = false;
	public static final String TEXTURE_SETTINGS_FILE = "config/texture_packing/texture_settings.json";
	
	private TexturePacker.Settings textureSettings;
	private TextureSettingsList textures;
	
	public TexturePackingTool() {
		textureSettings = new TexturePacker.Settings();
		textureSettings.maxWidth = 4096;
		textureSettings.maxHeight = 4096;
		textureSettings.edgePadding = false;
		textureSettings.duplicatePadding = false;
		textureSettings.filterMin = TextureFilter.Nearest;
		textureSettings.filterMag = TextureFilter.Nearest;
		
		try {
			if (REPACK_TEXTURES) {
				loadTextureSettings();
			}
		}
		catch (FileNotFoundException fnfe) {
			throw new IllegalStateException(fnfe);
		}
	}
	
	private void loadTextureSettings() throws FileNotFoundException {
		Json json = new Json();
		textures = json.fromJson(TextureSettingsList.class, new FileInputStream(new File(TEXTURE_SETTINGS_FILE)));
	}
	
	public void packTextures() {
		if (REPACK_TEXTURES) {
			for (TexturePackSetting packSetting : textures.getSettings()) {
				textureSettings.edgePadding = packSetting.isEdgePadding();
				TexturePacker.process(textureSettings, packSetting.getTextureDir(), packSetting.getOutputDir(), packSetting.getAtlasName());
			}
		}
	}
}
