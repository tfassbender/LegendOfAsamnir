package net.jfabricationgames.gdx.desktop.util;

public class TexturePackSetting {
	
	private String textureDir;
	private String outputDir;
	private String atlasName;
	
	public TexturePackSetting(String textureDir, String outputDir, String atlasName) {
		this.textureDir = textureDir;
		this.outputDir = outputDir;
		this.atlasName = atlasName;
	}
	
	public TexturePackSetting() {
		
	}
	
	public String getTextureDir() {
		return textureDir;
	}
	public void setTextureDir(String textureDir) {
		this.textureDir = textureDir;
	}
	
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public String getAtlasName() {
		return atlasName;
	}
	public void setAtlasName(String atlasName) {
		this.atlasName = atlasName;
	}
}