package net.jfabricationgames.gdx.character;

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
import net.jfabricationgames.gdx.attack.AttackCreator;
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
import net.jfabricationgames.gdx.texture.TextureLoader;

public class Dwarf implements PlayableCharacter, StatsCharacter, Disposable, ContactListener, Hittable {
	
	public static final float MOVING_SPEED = 300f;
	public static final float JUMPING_SPEED = 425f;
	public static final float MOVING_SPEED_SPRINT = 425f;
	public static final float MOVING_SPEED_ATTACK = 150f;
	public static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	private static final float TIME_TILL_SPIN_ATTACK = 1.5f;
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.8f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	
	private static final String assetConfigFileName = "config/animation/dwarf.json";
	private static final String attackConfigFileName = "config/dwarf/attacks.json";
	private static final String textureConfig = "config/dwarf/textures.json";
	private static final String soundSetKey = "dwarf";
	
	private static final String spinAttackChargedSound = "spin_attack_charged";
	
	private AnimationManager assetManager;
	
	private Body body;
	private AttackCreator attackCreator;
	
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
	
	private float armor = 100f;
	private float maxArmor = 100f;
	private float increaseArmor = 0f;
	private final float armorIncreasePerSecond = 25f;
	
	private CharacterInputMovementHandler movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private TextureLoader textureLoader;
	private TextureRegion idleDwarfSprite;
	private TextureRegion blockSprite;
	private TextureRegion aimMarkerSprite;
	
	private SoundSet soundSet;
	
	public Dwarf() {
		assetManager = AnimationManager.getInstance();
		assetManager.loadAnimations(assetConfigFileName);
		soundSet = SoundManager.getInstance().loadSoundSet(soundSetKey);
		
		textureLoader = new TextureLoader(textureConfig);
		idleDwarfSprite = getIdleSprite();
		blockSprite = getShieldSprite();
		aimMarkerSprite = getAimMarkerSprite();
		
		action = CharacterAction.NONE;
		body = createPhysicsBody();
		registerAsContactListener();
		
		animation = getAnimation();
		
		attackCreator = new AttackCreator(attackConfigFileName, body, PhysicsCollisionType.PLAYER_ATTACK);
		movementHandler = new CharacterInputMovementHandler(this);
	}
	
	private Body createPhysicsBody() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		World world = physicsWorld.getWorld();
		
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody)
				.setWidth(idleDwarfSprite.getRegionWidth() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X)
				.setHeight(idleDwarfSprite.getRegionHeight() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y)
				.setCollisionType(PhysicsCollisionType.PLAYER).setLinearDamping(10f);
		Body body = PhysicsBodyCreator.createOctagonBody(world, bodyProperties);
		body.setSleepingAllowed(false);
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(PHYSICS_BODY_SENSOR_RADIUS)
				.setCollisionType(PhysicsCollisionType.PLAYER_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
		body.setUserData(this);
		
		return body;
	}
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
	}
	
	private TextureRegion getIdleSprite() {
		return textureLoader.loadTexture("idle");
	}
	private TextureRegion getShieldSprite() {
		return textureLoader.loadTexture("block");
	}
	private TextureRegion getAimMarkerSprite() {
		return textureLoader.loadTexture("aim_marker");
	}
	
	@Override
	public boolean changeAction(CharacterAction action) {
		if (endurance < action.getEnduranceCosts()) {
			return false;
		}
		if ((action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT) && armor <= 0) {
			return false;
		}
		this.action = action;
		this.animation = getAnimation();
		this.animation.resetStateTime();
		
		endurance = Math.max(0, endurance - action.getEnduranceCosts());
		
		playSound(action);
		
		if (action.isAttack()) {
			attackCreator.startAttack(action.getAttack(), movementHandler.getMovingDirection().getNormalizedDirectionVector());
		}
		
		return true;
	}
	
	@Override
	public boolean executeSpecialAction() {
		//only arrows are used currently
		if (attackCreator.allAttacksExecuted()) {
			attackCreator.startAttack("arrow", movementHandler.getMovingDirection().getNormalizedDirectionVector());//TODO refactor "arrow"
			return true;
		}
		
		return false;
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (action != CharacterAction.NONE && action != CharacterAction.BLOCK) {
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
			playSound(action.getSound());
		}
	}
	private void playSound(String sound) {
		soundSet.playSound(sound);
	}
	
	public void render(float delta, SpriteBatch batch) {
		updateAction(delta);
		updateStats(delta);
		attackCreator.handleAttacks(delta);
		
		movementHandler.handleInputs(delta);
		movementHandler.move(delta);
		
		drawDwarf(batch);
		drawAimMarker(batch);
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
		if (increaseArmor > 0f) {
			float increaseStep = Math.min(delta * armorIncreasePerSecond, increaseArmor);
			increaseArmor -= increaseStep;
			armor = Math.min(armor + increaseStep, maxArmor);
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
		TextureRegion frame;
		if (action == CharacterAction.NONE) {
			frame = idleDwarfSprite;
		}
		else if (action == CharacterAction.BLOCK) {
			frame = blockSprite;
		}
		else {
			frame = animation.getKeyFrame();
		}
		
		if (!drawingDirectionEqualsTextureDirection(frame)) {
			frame.flip(true, false);
		}
		
		draw(batch, frame);
	}
	
	private boolean drawingDirectionEqualsTextureDirection(TextureRegion frame) {
		return movementHandler.isDrawDirectionRight() != frame.isFlipX();
	}
	
	private void drawAimMarker(SpriteBatch batch) {
		float aimMarkerDistanceFactor = 0.5f;
		float aimMarkerOffsetY = -0.1f;
		Vector2 aimMarkerOffset = movementHandler.getMovingDirection().getNormalizedDirectionVector().scl(aimMarkerDistanceFactor).add(0,
				aimMarkerOffsetY);
		Vector2 aimMarkerSize = new Vector2(5f, 5f);
		draw(batch, aimMarkerSprite, aimMarkerOffset, aimMarkerSize);
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame) {
		Vector2 size = new Vector2(frame.getRegionWidth(), frame.getRegionHeight());
		//use null as offset parameter to not create a new empty vector every time
		draw(batch, frame, null, size);
	}
	private void draw(SpriteBatch batch, TextureRegion frame, Vector2 offset, Vector2 size) {
		float originX = 0.5f * size.x + PHYSICS_BODY_POSITION_OFFSET.x * size.x;
		float originY = 0.5f * size.y + PHYSICS_BODY_POSITION_OFFSET.y * size.y;
		float x = body.getPosition().x - originX;
		float y = body.getPosition().y - originY;
		if (offset != null) {
			x += offset.x;
			y += offset.y;
		}
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				size.x, size.y, // width, height
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
	public float getHoldTimeTillSpinAttack() {
		return TIME_TILL_SPIN_ATTACK;
	}
	
	@Override
	public void playSpinAttackChargedSound() {
		playSound(spinAttackChargedSound);
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
	public float getArmor() {
		return armor / maxArmor;
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
		
		attackCreator.handleAttackDamage(contact);
	}
	
	private void collectItem(Item item) {
		if (item.containsProperty(ItemPropertyKeys.HEALTH.getPropertyName())) {
			float itemHealth = item.getProperty(ItemPropertyKeys.HEALTH.getPropertyName(), Float.class);
			increaseHealth = itemHealth;
		}
		if (item.containsProperty(ItemPropertyKeys.MANA.getPropertyName())) {
			float itemMana = item.getProperty(ItemPropertyKeys.MANA.getPropertyName(), Float.class);
			increaseMana = itemMana;
		}
		if (item.containsProperty(ItemPropertyKeys.ARMOR.getPropertyName())) {
			float itemArmor = item.getProperty(ItemPropertyKeys.ARMOR.getPropertyName(), Float.class);
			increaseArmor = itemArmor;
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
		if (isBlocking()) {
			takeArmorDamage(damage * 0.33f);
			damage *= 0.1f;
		}
		health -= damage;
		if (health <= 0) {
			health = 0;
			//TODO die
			//TODO play death sound
		}
		else {
			if (isBlocking()) {
				changeAction(CharacterAction.SHIELD_HIT);
			}
			else {
				changeAction(CharacterAction.HIT);
			}
		}
	}
	
	public void takeArmorDamage(float damage) {
		armor = Math.max(armor - damage, 0);
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force) {
		Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
		force *= 10f * body.getMass();
		if (isBlocking()) {
			force *= 0.33;
		}
		
		body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
	}
	
	private boolean isBlocking() {
		return action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT;
	}
	
	@Override
	public void dispose() {
		soundSet.dispose();
		PhysicsWorld.getInstance().removeContactListener(this);
	}
}
