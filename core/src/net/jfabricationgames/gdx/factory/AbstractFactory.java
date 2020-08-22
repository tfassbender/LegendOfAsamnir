package net.jfabricationgames.gdx.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public abstract class AbstractFactory {
	
	protected Json json;
	
	public AbstractFactory() {
		json = new Json();
	}
	
	public <T> T loadConfig(Class<T> clazz, String configFile) {
		return json.fromJson(clazz, Gdx.files.internal(configFile));
	}
}
