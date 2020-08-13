package net.jfabricationgames.gdx.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Asset implements Json.Serializable {
	
	public Class<?> type;
	public String path;
	
	public Class<? extends AssetLoaderParameters<?>> parametersType;
	@SuppressWarnings("rawtypes")
	public AssetLoaderParameters parameters;
	
	@Override
	public void write(Json json) {
		json.writeValue("assetType", type.getName());
		json.writeValue("path", path);
		json.writeValue("parameters", parameters);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		try {
			type = Class.forName(jsonData.get("type").asString());
			JsonValue parametersTypeJson = jsonData.get("parametersType");
			if (parametersTypeJson != null) {
				parametersType = (Class<? extends AssetLoaderParameters<?>>) Class.forName(parametersTypeJson.asString());
			}
		}
		catch (Exception e) {
			type = null;
			parametersType = null;
			
			throw new IllegalStateException("The type parameters could not be loaded or parsed", e);
		}
		
		path = jsonData.get("path").asString();
		
		JsonValue parametersValue = jsonData.get("parameters");
		if (parametersValue != null) {
			if (parametersType == null) {
				throw new IllegalStateException("The parameterType is not defined. Add a \"parameterType\" attribute to the "
						+ "config if you want to use parameters (NOTE: local classes are described as \"path.to.class$localClass\").");
			}
			parameters = json.readValue(parametersType, parametersValue);
		}
	}
}