package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

public class Pot extends DestroyableObject {
	
	public Pot(ObjectType type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 15f;
		destroySound = "glass_break";
	}
}
