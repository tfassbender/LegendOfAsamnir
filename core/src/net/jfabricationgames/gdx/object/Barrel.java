package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

public class Barrel extends DestroyableObject {
	
	public Barrel(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 30f;
	}
}
