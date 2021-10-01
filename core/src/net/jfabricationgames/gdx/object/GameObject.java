package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.cutscene.action.CutscenePositioningUnit;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapObject;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.util.MapUtil;

public class GameObject implements Hittable, GameMapObject, StatefulMapObject, CutsceneControlledUnit, CutscenePositioningUnit {
	
	public static final String MAP_PROPERTY_KEY_DEBUG_OBJECT = "debugObject";
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("object");
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected Sprite sprite;
	protected MapProperties mapProperties;
	protected Body body;
	protected TextureAtlas textureAtlas;
	protected GameObjectTypeConfig typeConfig;
	protected ObjectMap<String, Float> dropTypes;
	
	protected AnimationManager animationManager;
	protected AnimationDirector<TextureRegion> animation;
	protected String hitSound;
	
	protected PhysicsBodyProperties physicsBodyProperties;
	protected Vector2 physicsBodySizeFactor;
	protected Vector2 physicsBodyOffsetFactor;
	
	public GameObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.mapProperties = mapProperties;
		animationManager = AnimationManager.getInstance();
		
		processMapProperties();
		readTypeConfig();
	}
	
	protected void processMapProperties() {
		dropTypes = ItemDropUtil.processMapProperties(mapProperties, typeConfig.drops);
	}
	
	protected void readTypeConfig() {
		physicsBodyProperties = new PhysicsBodyProperties().setType(typeConfig.bodyType).setSensor(typeConfig.isSensor).setDensity(typeConfig.density)
				.setFriction(typeConfig.friction).setRestitution(typeConfig.restitution).setLinearDamping(typeConfig.linearDamping)
				.setCollisionType(typeConfig.collisionType);
		physicsBodySizeFactor = new Vector2(typeConfig.physicsBodySizeFactorX, typeConfig.physicsBodySizeFactorY);
		physicsBodyOffsetFactor = new Vector2(typeConfig.physicsBodyOffsetFactorX, typeConfig.physicsBodyOffsetFactorY);
		
		hitSound = typeConfig.hitSound;
	}
	
	/**
	 * Called by the GameMap or the TiledMapLoader directly after the GameObject was added.
	 */
	public void postAddToGameMap() {
		executeInitAction();
	}
	
	private void executeInitAction() {
		if (typeConfig.initAction != null) {
			typeConfig.initAction.execute(this);
		}
	}
	
	@Override
	public String getMapObjectId() {
		return StatefulMapObject.getMapObjectId(mapProperties);
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {}
	
	protected void createPhysicsBody(float x, float y) {
		float width = sprite.getWidth() * Constants.WORLD_TO_SCREEN * physicsBodySizeFactor.x;
		float height = sprite.getHeight() * Constants.WORLD_TO_SCREEN * physicsBodySizeFactor.y;
		
		x += sprite.getWidth() * Constants.WORLD_TO_SCREEN * physicsBodyOffsetFactor.x;
		y += sprite.getHeight() * Constants.WORLD_TO_SCREEN * physicsBodyOffsetFactor.y;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
		
		if (typeConfig.addSensor) {
			PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(typeConfig.sensorRadius)
					.setCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR);
			PhysicsBodyCreator.addCircularFixture(sensorProperties);
		}
	}
	
	protected Sprite createSprite(String textureName) {
		AnimationFrame animationFrame = AnimationFrame.getAnimationFrame(textureName);
		TextureRegion textureRegion = animationFrame.findRegion(textureAtlas);
		
		Sprite sprite = new Sprite(textureRegion);
		sprite.setX(this.sprite.getX());
		sprite.setY(this.sprite.getY());
		sprite.setScale(Constants.WORLD_TO_SCREEN);
		return sprite;
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			animation.draw(batch);
		}
		else {
			sprite.draw(batch);
		}
	}
	
	@Override
	public String getUnitId() {
		return mapProperties.get(CutsceneControlledUnit.MAP_PROPERTIES_KEY_UNIT_ID, String.class);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		animation = getHitAnimation();
		playHitSound();
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, boolean affectedByBlock) {
		//objects don't get pushed by hits
	}
	
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		if (showHitAnimation()) {
			AnimationDirector<TextureRegion> animation = animationManager.getTextureAnimationDirector(typeConfig.animationHit);
			animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
			return animation;
		}
		return null;
	}
	
	protected boolean showHitAnimation() {
		return typeConfig.animationHit != null;
	}
	
	protected AnimationDirector<TextureRegion> getActionAnimation() {
		return getAnimation(typeConfig.animationAction);
	}
	
	protected AnimationDirector<TextureRegion> getAnimation(String animationName) {
		if (animationName != null) {
			AnimationDirector<TextureRegion> animation = animationManager.getTextureAnimationDirector(animationName);
			animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
			return animation;
		}
		return null;
	}
	
	protected void dropItems() {
		float x = (body.getPosition().x + typeConfig.dropPositionOffsetX) * Constants.SCREEN_TO_WORLD;
		float y = (body.getPosition().y + typeConfig.dropPositionOffsetY) * Constants.SCREEN_TO_WORLD;
		
		if (mapProperties.containsKey(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE)) {
			String specialDropType = mapProperties.get(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE, String.class);
			String specialDropMapProperties = mapProperties.get(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_MAP_PROPERTIES, String.class);
			MapProperties mapProperties = MapUtil.createMapPropertiesFromString(specialDropMapProperties);
			ItemDropUtil.dropItem(specialDropType, mapProperties, x, y, typeConfig.renderDropsAboveObject);
		}
		else {
			ItemDropUtil.dropItems(dropTypes, x, y, typeConfig.renderDropsAboveObject);
		}
	}
	
	@Override
	public void removeFromMap() {
		remove();
	}
	
	public void remove() {
		GameMap.getInstance().removeObject(this, body);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
	
	protected void changeBodyToSensor() {
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setSensor(true);
		}
	}
	
	public void changeBodyToNonSensor() {
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setSensor(false);
		}
	}
	
	protected void removePhysicsBody() {
		if (body != null) {
			PhysicsWorld.getInstance().removeBodyWhenPossible(body);
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
		return "MapObject [type=" + typeConfig + ", properties=" + MapUtil.mapPropertiesToString(mapProperties, true) + "]";
	}
	
	protected void setTextureAtlas(TextureAtlas textureAtlas) {
		this.textureAtlas = textureAtlas;
	}
	
	@Override
	public Vector2 getPosition() {
		return body != null ? body.getPosition().cpy() : null;
	}
	
	protected boolean isPlayableCharacterContact(Contact contact) {
		return CollisionUtil.isPlayableCharacterContact(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact);
	}
}
