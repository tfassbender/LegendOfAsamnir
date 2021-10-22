package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.maps.MapProperties;

public interface ItemSpawnFactory {
	
	public void createAndAddItem(String type, float x, float y, MapProperties mapProperties, boolean renderAboveGameObjects);
}
