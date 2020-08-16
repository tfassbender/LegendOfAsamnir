package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class Item {
	
	private String name;
	private Sprite sprite;
	private MapProperties properties;
	private Body body;
	
	public Item(String name, Sprite sprite, MapProperties properties) {
		this.name = name;
		this.sprite = sprite;
		this.properties = properties;
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		body = PhysicsBodyCreator.createCircularBody(world, BodyType.StaticBody, x, y, false, 0, 0, 0, 0.1f, PhysicsCollisionType.ITEM);
		body.setUserData(this);
	}
	
	public String getName() {
		return name;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public MapProperties getProperties() {
		return properties;
	}
	
	public Body getBody() {
		return body;
	}
	
	@Override
	public String toString() {
		return "Item [name=" + name + ", properties=" + properties + "]";
	}
}
