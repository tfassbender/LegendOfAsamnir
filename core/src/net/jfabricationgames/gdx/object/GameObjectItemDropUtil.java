package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ObjectMap;

public interface GameObjectItemDropUtil {
	
	public ObjectMap<String, Float> processMapProperties(MapProperties mapProperties, ObjectMap<String, Float> drops);
	
	public void dropItem(String specialDropType, MapProperties mapProperties, float x, float y, boolean renderDropsAboveObject);
	public void dropItems(ObjectMap<String, Float> dropTypes, float x, float y, boolean renderDropsAboveObject);
}
