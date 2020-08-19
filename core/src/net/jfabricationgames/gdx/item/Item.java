package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class Item {
	
	private static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("item");
	
	private String name;
	private Sprite sprite;
	private MapProperties properties;
	private Body body;
	private GameMap gameMap;
	
	public Item(String name, Sprite sprite, MapProperties properties, GameMap gameMap) {
		this.name = name;
		this.sprite = sprite;
		this.properties = properties;
		this.gameMap = gameMap;
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		PhysicsBodyProperties properties = new PhysicsBodyProperties().setType(BodyType.StaticBody).setX(x).setY(y).setSensor(false).setRadius(0.1f)
				.setCollisionType(PhysicsCollisionType.ITEM);
		body = PhysicsBodyCreator.createCircularBody(world, properties);
		body.setUserData(this);
	}
	
	public void draw(float delta, SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void pickUp() {
		playPickUpSound();
		remove();
	}
	
	public void remove() {
		gameMap.removeItem(this);
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
	}
	
	private void playPickUpSound() {
		new ItemPickUpSound(name, properties, soundSet).play();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
	
	public <T> T getProperty(String property, Class<T> clazz) {
		return properties.get(property, clazz);
	}
	
	@Override
	public String toString() {
		return "Item [name=" + name + ", properties=" + properties + "]";
	}
}
