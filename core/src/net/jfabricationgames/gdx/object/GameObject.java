package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class GameObject implements Hittable {
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("object");
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected Sprite sprite;
	protected MapProperties properties;
	protected Body body;
	protected GameMap gameMap;
	protected TextureAtlas textureAtlas;
	
	protected Vector2 position;
	
	protected ObjectTypeConfig typeConfig;
	
	protected AnimationManager animationManager;
	protected AnimationDirector<TextureRegion> animation;
	protected String hitSound;
	
	protected PhysicsBodyProperties physicsBodyProperties;
	protected Vector2 physicsBodySizeFactor;
	protected Vector2 physicsBodyOffsetFactor;
	
	public GameObject(ObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.properties = properties;
		animationManager = AnimationManager.getInstance();
		
		readTypeConfig();
	}
	
	protected void readTypeConfig() {
		physicsBodyProperties = new PhysicsBodyProperties().setType(typeConfig.bodyType).setSensor(typeConfig.isSensor).setDensity(typeConfig.density)
				.setFriction(typeConfig.friction).setRestitution(typeConfig.restitution).setCollisionType(typeConfig.collsitionType);
		physicsBodySizeFactor = new Vector2(typeConfig.physicsBodySizeFactorX, typeConfig.physicsBodySizeFactorY);
		physicsBodyOffsetFactor = new Vector2(typeConfig.physicsBodyOffsetFactorX, typeConfig.physicsBodyOffsetFactorY);
		
		hitSound = typeConfig.hitSound;
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		float width = sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * physicsBodySizeFactor.x;
		float height = sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * physicsBodySizeFactor.y;
		
		x += sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * physicsBodyOffsetFactor.x;
		y += sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * physicsBodyOffsetFactor.y;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height);
		body = PhysicsBodyCreator.createRectangularBody(world, properties);
		body.setUserData(this);
		
		if (typeConfig.addSensor) {
			PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(typeConfig.sensorRadius)
					.setCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR);
			PhysicsBodyCreator.addCircularFixture(sensorProperties);
		}
	}
	
	protected Sprite createSprite(String textureName) {
		AnimationFrame animationFrame = AnimationFrame.getAnimationFrame(typeConfig.textureAfterAction);
		TextureRegion textureRegion = animationFrame.findRegion(textureAtlas);
		
		Sprite sprite = new Sprite(textureRegion);
		sprite.setX(position.x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(position.y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		return sprite;
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			float x = sprite.getX() + ((sprite.getWidth() - region.getRegionWidth()) * GameScreen.WORLD_TO_SCREEN * 0.5f);
			float y = sprite.getY() + ((sprite.getHeight() - region.getRegionHeight()) * GameScreen.WORLD_TO_SCREEN * 0.5f);
			batch.draw(region, x, y, sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f, region.getRegionWidth(), region.getRegionHeight(),
					GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
		}
		else {
			sprite.draw(batch);
		}
	}
	
	@Override
	public void takeDamage(float damage) {
		animation = getHitAnimation();
		playHitSound();
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force) {
		//objects don't get pushed by hits
	}
	
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		if (typeConfig.animationHit != null) {
			return animationManager.getAnimationDirector(typeConfig.animationHit);
		}
		return null;
	}
	
	protected AnimationDirector<TextureRegion> getActionAnimation() {
		if (typeConfig.animationAction != null) {
			return animationManager.getAnimationDirector(typeConfig.animationAction);
		}
		return null;
	}
	
	public void remove() {
		gameMap.removeObject(this);
		removePhysicsBody();
	}
	
	protected void removePhysicsBody() {
		if (body != null) {
			PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
			body = null;
		}
	}
	
	protected void playHitSound() {
		if (hitSound != null) {
			soundSet.playSound(hitSound);
		}
	}
	
	@Override
	public String toString() {
		return "MapObject [type=" + typeConfig + ", properties=" + properties + "]";
	}
	
	protected void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
	
	protected void setTextureAtlas(TextureAtlas textureAtlas) {
		this.textureAtlas = textureAtlas;
	}
	
	protected void setPosition(Vector2 position) {
		this.position = position;
	}
	
}
