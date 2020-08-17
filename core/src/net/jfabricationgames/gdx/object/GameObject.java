package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.screens.GameScreen;

public class GameObject {
	
	private ObjectType type;
	private Sprite sprite;
	private MapProperties properties;
	private Body body;
	
	public GameObject(ObjectType type, Sprite sprite, MapProperties properties) {
		this.type = type;
		this.sprite = sprite;
		this.properties = properties;
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		float width = sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * type.getPhysicsBodySizeFactor().x;
		float height = sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * type.getPhysicsBodySizeFactor().y;
		
		x += sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * type.getPhysicsBodyOffsetFactor().x;
		y += sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * type.getPhysicsBodyOffsetFactor().y;
		
		PhysicsBodyProperties properties = type.getPhysicsBodyProperties().setX(x).setY(y).setWidth(width).setHeight(height);
		body = PhysicsBodyCreator.createRectangularBody(world, properties);
		body.setUserData(this);
	}
	
	public ObjectType getType() {
		return type;
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
		return "MapObject [type=" + type + ", properties=" + properties + "]";
	}
}
