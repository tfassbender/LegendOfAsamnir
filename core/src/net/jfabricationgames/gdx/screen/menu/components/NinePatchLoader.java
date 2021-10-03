package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.screen.menu.config.NinePatchConfig;
import net.jfabricationgames.gdx.texture.TextureLoader;

public abstract class NinePatchLoader {
	
	private static Json json = new Json();
	
	public static NinePatch load(String configFile) {
		NinePatchConfig config = loadConfig(configFile);
		TextureLoader textureLoader = new TextureLoader(config.texture);
		TextureRegion texture = textureLoader.loadDefaultTexture();
		return new NinePatch(texture, config.left, config.right, config.top, config.bottom);
	}
	
	private static NinePatchConfig loadConfig(String configFile) {
		return json.fromJson(NinePatchConfig.class, Gdx.files.internal(configFile));
	}
}
