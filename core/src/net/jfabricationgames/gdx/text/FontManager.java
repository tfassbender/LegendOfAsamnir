package net.jfabricationgames.gdx.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

public class FontManager implements Disposable {
	
	private static FontManager instance;
	
	private ArrayMap<String, BitmapFont> fonts;
	private ShaderProgram fontShader;
	
	private FontConfig fontConfig;
	
	public static synchronized FontManager getInstance() {
		if (instance == null) {
			instance = new FontManager();
		}
		return instance;
	}
	
	private FontManager() {
		fonts = new ArrayMap<String, BitmapFont>();
	}
	
	public void load(String configPath) {
		Json json = new Json();
		fontConfig = json.fromJson(FontConfig.class, Gdx.files.internal(configPath));
		createFontShader();
	}
	
	private void createFontShader() {
		fontShader = new ShaderProgram(Gdx.files.internal(fontConfig.getFontShaderVert()), Gdx.files.internal(fontConfig.getFontShaderFrag()));
		
		if (!fontShader.isCompiled()) {
			Gdx.app.error(getClass().getSimpleName(), "Shader compilation failed:\n" + fontShader.getLog());
			throw new IllegalStateException("The loading of the font shader failed (see logs for details)");
		}
	}
	
	public BitmapFont getFont(String name) {
		BitmapFont font = fonts.get(name);
		if (font == null) {
			String fontPath = fontConfig.getFont(name);
			if (fontPath != null) {
				font = new BitmapFont(Gdx.files.internal(fontPath));
				font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				fonts.put(name, font);
			}
			else {
				throw new IllegalArgumentException(
						"A font named '" + name + "' is not listed in the configuration. Please check the font configuration file.");
			}
		}
		return font;
	}
	
	public ShaderProgram getFontShader() {
		return fontShader;
	}
	
	@Override
	public void dispose() {
		fonts.values().forEach(BitmapFont::dispose);
		fonts.clear();
	}
}
