package net.jfabricationgames.gdx.desktop.util;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureUtil {
	
	public static final boolean REPACK_TEXTURES = false;
	
	public static final TexturePackSetting[] TEXTURES = new TexturePackSetting[] {//
			new TexturePackSetting("dwarf/dwarf_left_attack", "packed/dwarf", "dwarf_left_attack"), //
			new TexturePackSetting("dwarf/dwarf_left_attack_jump", "packed/dwarf", "dwarf_left_attack_jump"), //
			new TexturePackSetting("dwarf/dwarf_left_die", "packed/dwarf", "dwarf_left_die"), //
			new TexturePackSetting("dwarf/dwarf_left_hit", "packed/dwarf", "dwarf_left_hit"), //
			new TexturePackSetting("dwarf/dwarf_left_idle", "packed/dwarf", "dwarf_left_idle"), //
			new TexturePackSetting("dwarf/dwarf_left_jump", "packed/dwarf", "dwarf_left_jump"), //
			new TexturePackSetting("dwarf/dwarf_left_run", "packed/dwarf", "dwarf_left_run"), //
			new TexturePackSetting("dwarf/dwarf_left_spin", "packed/dwarf", "dwarf_left_spin"), //
			new TexturePackSetting("dwarf/dwarf_right_attack", "packed/dwarf", "dwarf_right_attack"), //
			new TexturePackSetting("dwarf/dwarf_right_attack_jump", "packed/dwarf", "dwarf_right_attack_jump"), //
			new TexturePackSetting("dwarf/dwarf_right_die", "packed/dwarf", "dwarf_right_die"), //
			new TexturePackSetting("dwarf/dwarf_right_hit", "packed/dwarf", "dwarf_right_hit"), //
			new TexturePackSetting("dwarf/dwarf_right_idle", "packed/dwarf", "dwarf_right_idle"), //
			new TexturePackSetting("dwarf/dwarf_right_jump", "packed/dwarf", "dwarf_right_jump"), //
			new TexturePackSetting("dwarf/dwarf_right_run", "packed/dwarf", "dwarf_right_run"), //
			new TexturePackSetting("dwarf/dwarf_right_spin", "packed/dwarf", "dwarf_right_spin") //
	};
	
	public static class TexturePackSetting {
		
		public final String textureDir;
		public final String outputDir;
		public final String atlasName;
		
		public TexturePackSetting(String textureDir, String outputDir, String atlasName) {
			this.textureDir = textureDir;
			this.outputDir = outputDir;
			this.atlasName = atlasName;
		}
	}
	
	private TexturePacker.Settings textureSettings;
	
	public TextureUtil() {
		textureSettings = new TexturePacker.Settings();
		textureSettings.maxWidth = 4096;
		textureSettings.maxHeight = 4096;
		textureSettings.edgePadding = false;
		textureSettings.duplicatePadding = false;
		textureSettings.filterMin = TextureFilter.Linear;
		textureSettings.filterMag = TextureFilter.Linear;
	}
	
	public void packTextures() {
		if (REPACK_TEXTURES) {
			for (TexturePackSetting packSetting : TEXTURES) {
				TexturePacker.process(textureSettings, packSetting.textureDir, packSetting.outputDir, packSetting.atlasName);
			}
		}
	}
}
