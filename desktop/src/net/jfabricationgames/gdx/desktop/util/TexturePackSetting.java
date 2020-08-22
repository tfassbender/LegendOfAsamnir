package net.jfabricationgames.gdx.desktop.util;

import java.util.List;

import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class TexturePackSetting {
	
	private List<String> textureDirs;
	private String outputDir;
	private String atlasName;
	
	private boolean edgePadding = false;
	private boolean duplicatePadding = false;
	private TextureFilter filterMin = TextureFilter.Nearest;
	private TextureFilter filterMag = TextureFilter.Nearest;
	
	public TexturePackSetting() {
		
	}
	
	public List<String> getTextureDirs() {
		return textureDirs;
	}
	public void setTextureDirs(List<String> textureDirs) {
		this.textureDirs = textureDirs;
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
	
	public boolean isEdgePadding() {
		return edgePadding;
	}
	public void setEdgePadding(boolean edgePadding) {
		this.edgePadding = edgePadding;
	}
	
	public boolean isDuplicatePadding() {
		return duplicatePadding;
	}
	
	public void setDuplicatePadding(boolean duplicatePadding) {
		this.duplicatePadding = duplicatePadding;
	}
	
	public TextureFilter getFilterMin() {
		return filterMin;
	}
	
	public void setFilterMin(TextureFilter filterMin) {
		this.filterMin = filterMin;
	}
	
	public TextureFilter getFilterMag() {
		return filterMag;
	}
	
	public void setFilterMag(TextureFilter filterMag) {
		this.filterMag = filterMag;
	}
}
