package net.jfabricationgames.gdx.text;

import java.util.HashMap;

public class FontConfig {
	
	private String fontShaderFrag;
	private String fontShaderVert;
	
	private HashMap<String, String> fonts;
	
	public FontConfig() {
		
	}
	
	public String getFont(String name) {
		return fonts.get(name);
	}
	
	public String getFontShaderFrag() {
		return fontShaderFrag;
	}
	
	public void setFontShaderFrag(String fontShaderFrag) {
		this.fontShaderFrag = fontShaderFrag;
	}
	
	public String getFontShaderVert() {
		return fontShaderVert;
	}
	
	public void setFontShaderVert(String fontShaderVert) {
		this.fontShaderVert = fontShaderVert;
	}
	
	public HashMap<String, String> getFonts() {
		return fonts;
	}
	
	public void setFonts(HashMap<String, String> fonts) {
		this.fonts = fonts;
	}
}
