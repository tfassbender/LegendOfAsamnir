package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.animation.CharacterAnimationManager;
import net.jfabricationgames.gdx.character.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemPropertyKeys;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class Dwarf implements PlayableCharacter, StatsCharacter, Disposable, ContactListener {
	
	public static final float MOVING_SPEED = 300f;
	public static final float JUMPING_SPEED = 425f;
	public static final float MOVING_SPEED_SPRINT = 425f;
	public static final float MOVING_SPEED_ATTACK = 150;
	public static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.8f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	
	private static final String assetConfigFileName = "config/animation/dwarf.json";
	private static final String soundSetKey = "dwarf";
	
	private CharacterAnimationManager assetManager;
	
	private Body body;
	
	private GameMap map;
	
	private CharacterAction action;
	
	private float health = 100f;
	private float maxHealth = 100f;
	private float mana = 100f;
	private float maxMana = 100f;
	private float endurance = 100f;
	private float maxEndurance = 100f;
	private float enduranceChargeMoving = 7.5f;
	private float enduranceChargeIdle = 15f;
	private float enduranceCostsSprint = 15f;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite idleDwarfSprite;
	
	private SoundSet soundSet;
	
	public Dwarf(GameMap map) {
		this.map = map;
		
		assetManager = CharacterAnimationManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		action = CharacterAction.NONE;
		
		idleDwarfSprite = getIdleSprite();
		animation = getAnimation();
		
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		World world = physicsWorld.getWorld();
		physicsWorld.registerContactListener(this);
		
		body = PhysicsBodyCreator.createOctagonBody(world, BodyType.DynamicBody, 0f, 0f, 0f, 0f, 0f,
				idleDwarfSprite.getWidth() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X,
				idleDwarfSprite.getHeight() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y, PhysicsCollisionType.PLAYER);
		PhysicsBodyCreator.addCircularFixture(body, true, 0f, 0f, 0f, PHYSICS_BODY_SENSOR_RADIUS, PhysicsCollisionType.PLAYER_SENSOR);
		body.setLinearDamping(10f);
		body.setUserData(this);
		
		soundSet = SoundManager.getInstance().loadSoundSet(soundSetKey);
		
		movementHandler = new CharacterInputMovementHandler(this);
	}
	
	private Sprite getIdleSprite() {
		return new Sprite(getAnimation(CharacterAction.IDLE).getAnimation().getKeyFrame(0));
	}
	
	public boolean changeAction(CharacterAction action) {
		if (endurance >= action.getEnduranceCosts()) {
			this.action = action;
			this.animation = getAnimation();
			this.animation.resetStateTime();
			
			endurance = Math.max(0, endurance - action.getEnduranceCosts());
			
			playSound(action);
			return true;
		}
		return false;
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (action != CharacterAction.NONE) {
			return getAnimation(action);
		}
		else {
			return new DummyAnimationDirector<TextureRegion>();
		}
	}
	private AnimationDirector<TextureRegion> getAnimation(CharacterAction action) {
		return assetManager.getAnimationDirector(getAnimationName(action));
	}
	private String getAnimationName(CharacterAction action) {
		return action.getAnimationName();
	}
	
	private void playSound(CharacterAction action) {
		if (action.getSound() != null) {
			soundSet.playSound(action.getSound());
		}
	}
	
	public void render(float delta, SpriteBatch batch) {
		updateAction(delta);
		updateStats(delta);
		
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		drawDwarf(batch);
	}
	
	private void updateAction(float delta) {
		animation.increaseStateTime(delta);
		if (animation.isAnimationFinished()) {
			changeAction(CharacterAction.NONE);
		}
	}
	
	private void updateStats(float delta) {
		endurance = Math.min(endurance + delta * getEnduranceCharge(), maxEndurance);
	}
	private float getEnduranceCharge() {
		if (action == CharacterAction.NONE || action == CharacterAction.IDLE) {
			return enduranceChargeIdle;
		}
		else {
			return enduranceChargeMoving;
		}
	}
	
	private void drawDwarf(SpriteBatch batch) {
		TextureRegion frame = action != CharacterAction.NONE ? animation.getKeyFrame() : idleDwarfSprite;
		
		if (movementHandler.isDrawDirectionRight() == frame.isFlipX()) {
			frame.flip(true, false);
		}
		
		draw(batch, frame);
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame) {
		int width = frame.getRegionWidth();
		int height = frame.getRegionHeight();
		float originX = 0.5f * width + PHYSICS_BODY_POSITION_OFFSET.x * width;
		float originY = 0.5f * height + PHYSICS_BODY_POSITION_OFFSET.y * height;
		float x = body.getPosition().x - originX;
		float y = body.getPosition().y - originY;
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				GameScreen.WORLD_TO_SCREEN, // scaleX
				GameScreen.WORLD_TO_SCREEN, // scaleY
				0.0f); // rotation
	}
	
	@Override
	public CharacterAction getCurrentAction() {
		return action;
	}
	
	@Override
	public float getMovingSpeed(boolean sprint) {
		if (action == CharacterAction.JUMP) {
			return JUMPING_SPEED;
		}
		if (action == CharacterAction.ATTACK) {
			return MOVING_SPEED_ATTACK;
		}
		if (sprint) {
			return MOVING_SPEED_SPRINT;
		}
		return MOVING_SPEED;
	}
	
	@Override
	public void reduceEnduranceForSprinting(float delta) {
		endurance -= enduranceCostsSprint * delta;
		if (endurance < 0) {
			endurance = 0;
		}
	}
	@Override
	public boolean isExhausted() {
		return endurance < 1e-5;
	}
	
	@Override
	public void move(float deltaX, float deltaY) {
		float force = 10f * body.getMass();
		body.applyForceToCenter(deltaX * force, deltaY * force, true);
	}
	
	@Override
	public float getTimeTillIdleAnimation() {
		return TIME_TILL_IDLE_ANIMATION;
	}
	
	@Override
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished();
	}
	
	@Override
	public float getHealth() {
		return health / maxHealth;
	}
	
	@Override
	public float getMana() {
		return mana / maxMana;
	}
	
	@Override
	public float getEndurance() {
		return endurance / maxEndurance;
	}
	
	@Override
	public void dispose() {
		soundSet.dispose();
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Object sensorCollidingUserData = null;
		Object sensorUserData = null;
		
		if (fixtureA.isSensor()) {
			sensorUserData = fixtureA.getBody().getUserData();
			sensorCollidingUserData = fixtureB.getBody().getUserData();
		}
		else if (fixtureB.isSensor()) {
			sensorUserData = fixtureB.getBody().getUserData();
			sensorCollidingUserData = fixtureA.getBody().getUserData();
		}
		
		if (sensorUserData instanceof Dwarf && sensorCollidingUserData instanceof Item) {
			collectItem((Item) sensorCollidingUserData);
		}
	}
	
	private void collectItem(Item item) {
		MapProperties properties = item.getProperties();
		if (properties.containsKey(ItemPropertyKeys.HEALTH.getPropertyName())) {
			int itemHealth = properties.get(ItemPropertyKeys.HEALTH.getPropertyName(), Integer.class);
			health = Math.min(health + itemHealth, maxHealth);
		}
		if (properties.containsKey(ItemPropertyKeys.MANA.getPropertyName())) {
			int itemMana = properties.get(ItemPropertyKeys.MANA.getPropertyName(), Integer.class);
			mana = Math.min(mana + itemMana, maxMana);
		}
		//TODO other item types
		
		removeItem(item);
	}
	
	private void removeItem(Item item) {
		map.removeItem(item);
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(item.getBody());
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void endContact(Contact contact) {}
}
