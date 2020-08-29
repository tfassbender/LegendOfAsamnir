package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemPropertyKeys;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class Dwarf implements PlayableCharacter, StatsCharacter, Disposable, ContactListener, Hittable {
	
	public static final float MOVING_SPEED = 300f;
	public static final float JUMPING_SPEED = 425f;
	public static final float MOVING_SPEED_SPRINT = 425f;
	public static final float MOVING_SPEED_ATTACK = 150;
	public static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.8f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	
	private static final float PHYSICS_BODY_HIT_FIXTURE_RADIUS_FACTOR = 0.4f; //radius is calculated as width * factor
	private static final float PHYSICS_BODY_HIT_FIXTURE_POSITION_OFFSET_FACTOR = 0.4f; //offset (in the given direction) is [width/height] * factor
	
	private static final String assetConfigFileName = "config/animation/dwarf.json";
	private static final String soundSetKey = "dwarf";
	
	private AnimationManager assetManager;
	
	private Body body;
	
	private Fixture hitFixture;
	
	private CharacterAction action;
	
	private float health = 100f;
	private float maxHealth = 100f;
	private float increaseHealth = 0f;
	private final float healthIncreasePerSecond = 25f;
	
	private float mana = 100f;
	private float maxMana = 100f;
	private float increaseMana = 0f;
	private float manaIncreasePerSecond = 25f;
	
	private float endurance = 100f;
	private float maxEndurance = 100f;
	private float increaseEndurance = 0f;
	private float enduranceIncreasePerSecond = 25f;
	
	private float enduranceChargeMoving = 7.5f;
	private float enduranceChargeIdle = 15f;
	private float enduranceCostsSprint = 15f;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private Sprite idleDwarfSprite;
	
	private SoundSet soundSet;
	
	public Dwarf() {
		assetManager = AnimationManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		
		action = CharacterAction.NONE;
		
		idleDwarfSprite = getIdleSprite();
		animation = getAnimation();
		
		createBody();
		
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
			
			if (CharacterAction.isAttack(action)) {
				addHitFixture();
			}
			else {
				removeHitFixture();
			}
			
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
	
	private void addHitFixture() {
		PhysicsBodyProperties properties = new PhysicsBodyProperties().setBody(body).setSensor(true)
				.setCollisionType(PhysicsCollisionType.PLAYER_ATTACK).setRadius(getSpriteWidth() * PHYSICS_BODY_HIT_FIXTURE_RADIUS_FACTOR)
				.setFixturePosition(getHitFixturePosition());
		hitFixture = PhysicsBodyCreator.addCircularFixture(properties);
	}
	private Vector2 getHitFixturePosition() {
		return movementHandler.getMovingDirection().getNormalizedDirectionVector()
				.scl(PHYSICS_BODY_HIT_FIXTURE_POSITION_OFFSET_FACTOR * getSpriteWidth());
	}
	
	private float getSpriteWidth() {
		return idleDwarfSprite.getWidth() * GameScreen.WORLD_TO_SCREEN;
	}
	
	private void removeHitFixture() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, body);
		}
	}
	
	private void createBody() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		World world = physicsWorld.getWorld();
		physicsWorld.registerContactListener(this);
		
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody)
				.setWidth(idleDwarfSprite.getWidth() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X)
				.setHeight(idleDwarfSprite.getHeight() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y)
				.setCollisionType(PhysicsCollisionType.PLAYER).setLinearDamping(10f);
		body = PhysicsBodyCreator.createOctagonBody(world, bodyProperties);
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(PHYSICS_BODY_SENSOR_RADIUS)
				.setCollisionType(PhysicsCollisionType.PLAYER_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
		body.setUserData(this);
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
		//recharge endurance by time
		endurance = Math.min(endurance + delta * getEnduranceCharge(), maxEndurance);
		
		//increase health, mana and endurance
		if (increaseHealth > 0f) {
			float increaseStep = Math.min(delta * healthIncreasePerSecond, increaseHealth);
			increaseHealth -= increaseStep;
			health = Math.min(health + increaseStep, maxHealth);
		}
		if (increaseMana > 0f) {
			float increaseStep = Math.min(delta * manaIncreasePerSecond, increaseMana);
			increaseMana -= increaseStep;
			mana = Math.min(mana + increaseStep, maxMana);
		}
		if (increaseEndurance > 0f) {
			float increaseStep = Math.min(delta * enduranceIncreasePerSecond, increaseEndurance);
			increaseEndurance -= increaseStep;
			endurance = Math.min(endurance + increaseStep, maxEndurance);
		}
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
	
	public Vector2 getPosition() {
		return body.getPosition().cpy();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB)) {
			//collect stuff that touches the player sensor (usually items)
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB);
			
			if (sensorCollidingUserData instanceof Item) {
				collectItem((Item) sensorCollidingUserData);
			}
		}
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.PLAYER_ATTACK, fixtureA, fixtureB)) {
			//hit something with an axe
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_ATTACK, fixtureA, fixtureB);
			
			if (attackedUserData instanceof Hittable) {
				Hittable hittable = ((Hittable) attackedUserData);
				hittable.takeDamage(action.getDamage());
				//enemies define the force themselves; the force parameter is a factor for this self defined force
				hittable.pushByHit(getPosition(), 1f);
			}
		}
	}
	
	private void collectItem(Item item) {
		if (item.containsProperty(ItemPropertyKeys.HEALTH.getPropertyName())) {
			int itemHealth = item.getProperty(ItemPropertyKeys.HEALTH.getPropertyName(), Integer.class);
			increaseHealth = itemHealth;
		}
		if (item.containsProperty(ItemPropertyKeys.MANA.getPropertyName())) {
			int itemMana = item.getProperty(ItemPropertyKeys.MANA.getPropertyName(), Integer.class);
			mana = Math.min(mana + itemMana, maxMana);
		}
		//TODO other item types
		
		item.pickUp();
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void takeDamage(float damage) {
		health -= damage;
		if (health <= 0) {
			//TODO die
		}
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force) {
		Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
		force *= 10f * body.getMass();
		body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
	}
	
	@Override
	public void dispose() {
		soundSet.dispose();
		PhysicsWorld.getInstance().removeContactListener(this);
	}
}
