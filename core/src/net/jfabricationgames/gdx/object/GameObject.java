package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public abstract class GameObject implements Hittable {
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("object");
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	private ObjectTypeConfig typeConfig;
	private Sprite sprite;
	private MapProperties properties;
	private Body body;
	private GameMap gameMap;
	
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
	}
	
	protected void createPhysicsBody(World world, float x, float y) {
		if (physicsBodyProperties == null) {
			throw new IllegalStateException("The physicsProperties of this object have not yet been set. "
					+ "Set them (usually in the class constructor) before calling the createPhysicsBody method.");
		}
		if (physicsBodySizeFactor == null) {
			throw new IllegalStateException("The physicsBodySizeFactor of this object have not yet been set. "
					+ "Set them (usually in the class constructor) before calling the createPhysicsBody method.");
		}
		if (physicsBodyOffsetFactor == null) {
			throw new IllegalStateException("The physicsBodyOffsetFactor of this object have not yet been set. "
					+ "Set them (usually in the class constructor) before calling the createPhysicsBody method.");
		}
		
		float width = sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * physicsBodySizeFactor.x;
		float height = sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * physicsBodySizeFactor.y;
		
		x += sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * physicsBodyOffsetFactor.x;
		y += sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * physicsBodyOffsetFactor.y;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height);
		body = PhysicsBodyCreator.createRectangularBody(world, properties);
		body.setUserData(this);
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			float x = sprite.getX() + ((sprite.getWidth() - region.getRegionWidth()) * GameScreen.WORLD_TO_SCREEN / 2f);
			float y = sprite.getY() + ((sprite.getHeight() - region.getRegionHeight()) * GameScreen.WORLD_TO_SCREEN / 2f);
			batch.draw(region, x, y, sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f, region.getRegionWidth(), region.getRegionHeight(),
					GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
		}
		else {
			sprite.draw(batch);
		}
	}
	
	protected AnimationDirector<TextureRegion> createAnimation(String atlas, String region, float frameDuration) {
		return new AnimationDirector<TextureRegion>(
				new Animation<TextureRegion>(frameDuration, assetManager.get(atlas, TextureAtlas.class).findRegions(region)));
	}
	
	@Override
	public void takeDamage(float damage) {
		animation = getHitAnimation();
		playHitSound();
	}
	
	protected abstract AnimationDirector<TextureRegion> getHitAnimation();
	
	public void remove() {
		gameMap.removeObject(this);
		removePhysicsBody();
	}
	
	public void removePhysicsBody() {
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
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
	
	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
}
