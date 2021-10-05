package net.jfabricationgames.gdx.data.handler.type;

import com.badlogic.gdx.utils.ObjectMap;

public interface DataItem {
	
	public Object getItemName();
	
	public boolean canBePicked();
	public void pickUp();
	
	public boolean containsProperty(String propertyName);
	public <T> T getProperty(String property, Class<T> clazz);
	public ObjectMap<String, String> getKeyProperties();
}