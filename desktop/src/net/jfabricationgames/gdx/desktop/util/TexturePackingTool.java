package net.jfabricationgames.gdx.desktop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Json;

public class TexturePackingTool {
	
	public static final String TEXTURE_SETTINGS_FILE = "config/texture_packing/texture_settings.json";
	public static final String TEXTURE_PACKING_TMP_DIR = "texture_packing/tmp";
	
	public static void main(String[] args) {
		new TexturePackingTool().packTextures();
	}
	
	private TexturePacker.Settings textureSettings;
	private TextureSettingsMap textures;
	
	public TexturePackingTool() {
		textureSettings = new TexturePacker.Settings();
		textureSettings.maxWidth = 4096;
		textureSettings.maxHeight = 4096;
		
		try {
			loadTextureSettings();
		}
		catch (FileNotFoundException fnfe) {
			throw new IllegalStateException(fnfe);
		}
	}
	
	private void loadTextureSettings() throws FileNotFoundException {
		Json json = new Json();
		textures = json.fromJson(TextureSettingsMap.class, new FileInputStream(new File(TEXTURE_SETTINGS_FILE)));
	}
	
	public void packTextures() {
		for (TexturePackSetting packSetting : textures.getLevels().values()) {
			textureSettings.edgePadding = packSetting.isEdgePadding();
			textureSettings.duplicatePadding = packSetting.isDuplicatePadding();
			textureSettings.filterMin = packSetting.getFilterMin();
			textureSettings.filterMag = packSetting.getFilterMag();
			
			try {
				createTmpDir();
				copyTexturesToTmpDir(packSetting.getTextureDirs());
				TexturePacker.process(textureSettings, TEXTURE_PACKING_TMP_DIR, packSetting.getOutputDir(), packSetting.getAtlasName());
				dropTmpDir();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private void createTmpDir() throws IOException {
		File tmpDir = new File(TEXTURE_PACKING_TMP_DIR);
		if (tmpDir.exists()) {
			if (!tmpDir.isDirectory()) {
				throw new IOException("The tmp dir already exists, but is no directory");
			}
			if (tmpDir.list().length > 0) {
				throw new IOException("The tmp dir already exists, but is not empty");
			}
			// else: the tmp directory exists and is empty
		}
		else {
			if (!tmpDir.mkdirs()) {
				throw new IOException("Creation of the tmp dir failed");
			}
		}
	}
	
	/**
	 * Copies all files (but no directories) to the tmp directory to be packed.
	 */
	private void copyTexturesToTmpDir(List<String> textureDirs) throws IOException {
		Path tmpDirPath = new File(TEXTURE_PACKING_TMP_DIR).toPath();
		for (String textureDirPath : textureDirs) {
			File textureDir = new File(textureDirPath);
			File[] textureFiles = textureDir.listFiles();
			for (File textureFile : textureFiles) {
				Files.copy(textureFile.toPath(), tmpDirPath.resolve(textureFile.getName()));
			}
		}
	}
	
	private void dropTmpDir() throws IOException {
		File tmpDir = new File(TEXTURE_PACKING_TMP_DIR);
		//delete all files
		for (File file : tmpDir.listFiles()) {
			Files.delete(file.toPath());
		}
		//delete the directory
		Files.delete(tmpDir.toPath());
		//delete the super dir (if it's empty)
		if (tmpDir.getParentFile().list().length == 0) {
			Files.delete(tmpDir.getParentFile().toPath());
		}
	}
}
