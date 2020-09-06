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
	
	protected static ItemTypeConfig defaultTypeConfig;
	
	private Sprite sprite;
	private MapProperties properties;
	private Body body;
	private GameMap gameMap;
	
	protected ItemTypeConfig typeConfig;
	protected String pickUpSoundName;
	
	public Item(ItemTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameMap gameMap) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.properties = properties;
		this.gameMap = gameMap;
		
		readTypeConfig();
	}
	
	protected void readTypeConfig() {
		pickUpSoundName = typeConfig.pickUpSound;
		if (pickUpSoundName == null && defaultTypeConfig != null) {
			pickUpSoundName = defaultTypeConfig.pickUpSound;
		}
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		PhysicsBodyProperties properties = new PhysicsBodyProperties().setType(BodyType.StaticBody).setX(x).setY(y).setSensor(false)
				.setRadius(typeConfig.physicsObjectRadius).setCollisionType(PhysicsCollisionType.ITEM);
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
		if (pickUpSoundName != null) {
			soundSet.playSound(pickUpSoundName);
		}
	}
	
	public boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
	
	public <T> T getProperty(String property, Class<T> clazz) {
		return properties.get(property, clazz);
	}
	
	@Override
	public String toString() {
		return "Item [properties=" + properties + "]";
	}
}
