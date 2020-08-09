package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

public class Item {
	
	private String name;
	private Sprite sprite;
	private MapProperties properties;
	
	public Item(String name, Sprite sprite, MapProperties properties) {
		this.name = name;
		this.sprite = sprite;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public MapProperties getProperties() {
		return properties;
	}
}
