package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.maps.MapProperties;

public interface EnemySpawnFactory {
	
	public void createAndAddEnemy(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap);
}
