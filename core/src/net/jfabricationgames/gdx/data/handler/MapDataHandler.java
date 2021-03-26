package net.jfabricationgames.gdx.data.handler;

import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.container.MapDataContainer;

public class MapDataHandler implements DataHandler {
	
	private static MapDataHandler instance;
	
	public static synchronized MapDataHandler getInstance() {
		if (instance == null) {
			instance = new MapDataHandler();
		}
		return instance;
	}
	
	private MapDataContainer properties;
	
	private MapDataHandler() {}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.mapDataContainer;
	}
	
	public String getMapIdentifier() {
		return properties.mapIdentifier;
	}
	public void setMapIdentifier(String mapIdentifier) {
		properties.mapIdentifier = mapIdentifier;
	}
}
