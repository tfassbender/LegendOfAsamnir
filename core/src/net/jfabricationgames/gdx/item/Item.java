package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.container.data.KeyItem;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class Item {
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("item");
	
	protected static ItemTypeConfig defaultTypeConfig;
	
	protected AnimationDirector<TextureRegion> animation;
	protected Sprite sprite;
	protected MapProperties properties;
	protected Body body;
	protected GameMap gameMap;
	
	protected final String itemName;
	protected ItemTypeConfig typeConfig;
	protected String pickUpSoundName;
	
	public Item(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties,
			GameMap gameMap) {
		this.itemName = itemName;
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.animation = animation;
		this.properties = properties;
		this.gameMap = gameMap;
		
		if (animation == null && sprite == null) {
			Gdx.app.error(getClass().getSimpleName(), "Neither an animation nor a sprite was set for this item.");
		}
		
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
		if (animation != null) {
			animation.increaseStateTime(delta);
			animation.draw(batch);
		}
		else if (sprite != null) {
			sprite.draw(batch);
		}
	}
	
	public boolean canBePicked(PlayableCharacter player) {
		return true;
	}
	
	public void pickUp() {
		playPickUpSound();
		remove();
	}
	
	public void remove() {
		if (gameMap != null) {
			gameMap.removeItem(this);
			PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
			body = null;
		}
	}
	
	private void playPickUpSound() {
		if (pickUpSoundName != null) {
			soundSet.playSound(pickUpSoundName);
		}
	}
	
	public ObjectMap<String, String> getKeyProperties() {
		return KeyItem.getKeyProperties(properties);
	}
	
	public boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
	
	public <T> T getProperty(String property, Class<T> clazz) {
		return properties.get(property, clazz);
	}
	
	public String getItemName() {
		return itemName;
	}
	
	@Override
	public String toString() {
		return "Item [name=" + itemName + "; properties=" + properties + "]";
	}
}
