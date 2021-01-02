package net.jfabricationgames.gdx.character.implementation;

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
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.container.CharacterFastTravelContainer;
import net.jfabricationgames.gdx.character.container.CharacterItemContainer;
import net.jfabricationgames.gdx.character.container.CharacterPropertiesContainer;
import net.jfabricationgames.gdx.character.container.data.CharacterFastTravelProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapGroundType;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.physics.BeforeWorldStep;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.texture.TextureLoader;
import net.jfabricationgames.gdx.util.GameUtils;

public class Dwarf implements PlayableCharacter, Disposable, ContactListener, Hittable, EventListener {
	
	private static final float MOVING_SPEED = 300f;
	private static final float MOVING_SPEED_JUMP = 425f;
	private static final float MOVING_SPEED_SPRINT = 425f;
	private static final float MOVING_SPEED_ATTACK = 150f;
	private static final float MOVING_SPEED_CUTSCENE = 3.5f;
	
	private static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	private static final float TIME_TILL_SPIN_ATTACK = 1.5f;
	private static final float TIME_TILL_GAME_OVER_MENU = 3f;
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.6f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	
	private static final float DRAWING_DIRECTION_OFFSET = 0.1f;
	
	private static final String ASSET_CONFIG_FILE_NAME = "config/animation/dwarf.json";
	private static final String ATTACK_CONFIG_FILE_NAME = "config/dwarf/attacks.json";
	private static final String TEXTURE_CONFIG_FILE_NAME = "config/dwarf/textures.json";
	
	private static final String SOUND_SET_KEY = "dwarf";
	private static final String SPIN_ATTACK_CHARGED_SOUND = "spin_attack_charged";
	
	private boolean gameOver;
	
	private AnimationManager animationManager;
	
	private Body body;
	private AttackCreator attackCreator;
	
	private CharacterAction action;
	private SpecialAction activeSpecialAction;
	
	private CharacterInputProcessor movementHandler;
	
	private AnimationDirector<TextureRegion> animation;
	private TextureLoader textureLoader;
	private TextureRegion idleDwarfSprite;
	private TextureRegion blockSprite;
	private TextureRegion aimMarkerSprite;
	
	private SoundSet soundSet;
	
	private CharacterPropertiesContainer properties;
	private CharacterItemContainer itemContainer;
	private CharacterFastTravelContainer fastTravelContainer;
	
	private GameMapGroundType groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	
	public Dwarf() {
		properties = CharacterPropertiesContainer.getInstance();
		itemContainer = CharacterItemContainer.getInstance();
		fastTravelContainer = CharacterFastTravelContainer.getInstance();
		
		animationManager = AnimationManager.getInstance();
		animationManager.loadAnimations(ASSET_CONFIG_FILE_NAME);
		soundSet = SoundManager.getInstance().loadSoundSet(SOUND_SET_KEY);
		
		textureLoader = new TextureLoader(TEXTURE_CONFIG_FILE_NAME);
		idleDwarfSprite = getIdleSprite();
		blockSprite = getShieldSprite();
		aimMarkerSprite = getAimMarkerSprite();
		
		action = CharacterAction.NONE;
		body = createPhysicsBody();
		registerAsContactListener();
		
		activeSpecialAction = SpecialAction.JUMP;
		
		animation = getAnimation();
		
		attackCreator = new AttackCreator(ATTACK_CONFIG_FILE_NAME, body, PhysicsCollisionType.PLAYER_ATTACK);
		movementHandler = new CharacterInputProcessor(this);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void reAddToWorld() {
		body = createPhysicsBody();
		attackCreator = new AttackCreator(ATTACK_CONFIG_FILE_NAME, body, PhysicsCollisionType.PLAYER_ATTACK);
	}
	
	private Body createPhysicsBody() {
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody)
				.setWidth(idleDwarfSprite.getRegionWidth() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X)
				.setHeight(idleDwarfSprite.getRegionHeight() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y)
				.setCollisionType(PhysicsCollisionType.PLAYER).setLinearDamping(10f);
		Body body = PhysicsBodyCreator.createOctagonBody(bodyProperties);
		body.setSleepingAllowed(false);
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(PHYSICS_BODY_SENSOR_RADIUS)
				.setCollisionType(PhysicsCollisionType.PLAYER_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
		body.setUserData(this);
		
		return body;
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
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
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
		return animationManager.getAnimationDirector(getAnimationName(action));
	}
	private String getAnimationName(CharacterAction action) {
		return action.getAnimationName();
	}
	
	@Override
	public boolean changeAction(CharacterAction action) {
		if (isAlive() || action == CharacterAction.DIE) {
			if (!properties.hasEnoughEndurance(action)) {
				return false;
			}
			if ((action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT) && !properties.hasBlock()) {
				return false;
			}
			this.action = action;
			this.animation = getAnimation();
			this.animation.resetStateTime();
			
			properties.reduceEnduranceForAction(action);
			
			playSound(action);
			
			if (action.isAttack()) {
				attackCreator.startAttack(action.getAttack(), movementHandler.getMovingDirection().getNormalizedDirectionVector());
			}
			
			return true;
		}
		return false;
	}
	
	private void playSound(CharacterAction action) {
		if (action.getSound() != null) {
			playSound(action.getSound());
		}
	}
	private void playSound(String sound) {
		soundSet.playSound(sound);
	}
	
	@Override
	public boolean executeSpecialAction() {
		if (activeSpecialAction != null) {
			switch (activeSpecialAction) {
				case BOW:
				case BOMB:
					ItemAmmoType ammoType = ItemAmmoType.fromSpecialAction(activeSpecialAction);
					if (attackCreator.allAttacksExecuted()) {
						if (itemContainer.hasAmmo(ammoType)) {
							itemContainer.decreaseAmmo(ammoType);
							attackCreator.startAttack(ammoType.name().toLowerCase(),
									movementHandler.getMovingDirection().getNormalizedDirectionVector());
							
							if (!itemContainer.hasAmmo(ammoType)) {
								fireOutOfAmmoEvent(ammoType);
							}
						}
						else {
							//TODO delay between sounds
							//soundSet.playSound(SOUND_AMMO_EMPTY);
							return false;
						}
						
						return true;
					}
					break;
				case JUMP:
					return changeAction(CharacterAction.JUMP);
				default:
					throw new IllegalStateException("Unexpected SpecialAction: " + activeSpecialAction);
			}
		}
		
		return false;
	}
	
	private void fireOutOfAmmoEvent(ItemAmmoType ammoType) {
		EventConfig eventConfig = new EventConfig().setEventType(EventType.OUT_OF_AMMO).setStringValue(ammoType.name());
		EventHandler.getInstance().fireEvent(eventConfig);
	}
	
	@Override
	public int getAmmo(ItemAmmoType ammoType) {
		return itemContainer.getAmmo(ammoType);
	}
	
	@Override
	public void render(float delta, SpriteBatch batch) {
		updateAction(delta);
		properties.updateStats(delta, action);
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
	
	private void draw(SpriteBatch batch, TextureRegion frame) {
		//use null as offset parameter to not create a new empty vector every time
		draw(batch, frame, null, frame.getRegionWidth(), frame.getRegionHeight());
	}
	private void draw(SpriteBatch batch, TextureRegion frame, Vector2 offset, float width, float height) {
		float originX = 0.5f * width + PHYSICS_BODY_POSITION_OFFSET.x * width;
		float originY = 0.5f * height + PHYSICS_BODY_POSITION_OFFSET.y * height;
		float x = body.getPosition().x - originX;
		float y = body.getPosition().y - originY;
		if (offset != null) {
			x += offset.x;
			y += offset.y;
		}
		x += getDrawingDirectionOffset();
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				GameScreen.WORLD_TO_SCREEN, // scaleX
				GameScreen.WORLD_TO_SCREEN, // scaleY
				0.0f); // rotation
	}
	
	private float getDrawingDirectionOffset() {
		if (movementHandler.isDrawDirectionRight()) {
			return DRAWING_DIRECTION_OFFSET;
		}
		else {
			return -DRAWING_DIRECTION_OFFSET;
		}
	}
	
	private void drawAimMarker(SpriteBatch batch) {
		float aimMarkerDistanceFactor = 0.5f;
		float aimMarkerOffsetY = -0.1f;
		Vector2 aimMarkerOffset = movementHandler.getMovingDirection().getNormalizedDirectionVector().scl(aimMarkerDistanceFactor).add(0,
				aimMarkerOffsetY);
		float aimMarkerSize = 5f;
		draw(batch, aimMarkerSprite, aimMarkerOffset, aimMarkerSize, aimMarkerSize);
	}
	
	@Override
	public CharacterAction getCurrentAction() {
		return action;
	}
	
	@Override
	public float getMovingSpeed(boolean sprint) {
		float speed;
		speed = MOVING_SPEED;
		if (sprint) {
			speed = MOVING_SPEED_SPRINT;
		}
		if (action == CharacterAction.ATTACK) {
			speed = MOVING_SPEED_ATTACK;
		}
		if (action == CharacterAction.JUMP) {
			speed = MOVING_SPEED_JUMP;
		}
		
		return speed;
	}
	
	@Override
	public void reduceEnduranceForSprinting(float delta) {
		properties.reduceEnduranceForSprinting(delta);
	}
	@Override
	public boolean isExhausted() {
		return properties.isExhausted();
	}
	
	@Override
	public void move(float deltaX, float deltaY) {
		float force = 10f * groundProperties.movementSpeedFactor * body.getMass();
		body.applyForceToCenter(deltaX * force, deltaY * force, true);
	}
	
	@Override
	public void moveTo(Vector2 position, float speedFactor) {
		Vector2 direction = position.cpy().sub(getPosition());
		direction.nor().scl(MOVING_SPEED_CUTSCENE * speedFactor);
		
		move(direction.x, direction.y);
	}
	
	@Override
	public void changeToMovingState() {
		if (action != CharacterAction.RUN) {
			changeAction(CharacterAction.RUN);
		}
	}
	
	@Override
	public Vector2 getPosition() {
		return body.getPosition().cpy();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
		properties.setRespawnPoint(new Vector2(x, y));
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
		playSound(SPIN_ATTACK_CHARGED_SOUND);
	}
	
	@Override
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished();
	}
	
	@Override
	public float getHealth() {
		return properties.getHealthPercentual();
	}
	
	@Override
	public boolean isAlive() {
		return properties.isAlive();
	}
	
	@Override
	public float getMana() {
		return properties.getManaPercentual();
	}
	
	@Override
	public float getEndurance() {
		return properties.getEndurancePercentual();
	}
	
	@Override
	public float getArmor() {
		return properties.getArmorPercentual();
	}
	
	@Override
	public int getCoins() {
		return properties.getCoins();
	}
	
	@Override
	public int getCoinsForHud() {
		return properties.getCoinsForHud();
	}
	
	@Override
	public int getNormalKeys() {
		return itemContainer.getNumNormalKeys();
	}
	
	@Override
	public SpecialAction getActiveSpecialAction() {
		return activeSpecialAction;
	}
	
	@Override
	public void setActiveSpecialAction(SpecialAction specialAction) {
		this.activeSpecialAction = specialAction;
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB)) {
			//collect stuff that touches the player sensor (usually items)
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB);
			
			if (sensorCollidingUserData instanceof Item) {
				itemContainer.collectItem((Item) sensorCollidingUserData, this);
			}
		}
		
		attackCreator.handleAttackDamage(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		GameMapGroundType updatedGroundProperties = GameMapGroundType.handleGameMapGroundContact(contact, PhysicsCollisionType.PLAYER,
				groundProperties);
		if (updatedGroundProperties != null) {
			groundProperties = updatedGroundProperties;
		}
	}
	
	@BeforeWorldStep
	public void resetGroundProperties() {
		groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (isAlive()) {
			if (isBlocking()) {
				takeArmorDamage(damage * 0.33f);
				damage *= 0.1f;
			}
			properties.takeDamage(damage);
			if (!properties.isAlive()) {
				die();
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
	}
	
	private void takeArmorDamage(float damage) {
		properties.takeArmorDamage(damage);
	}
	
	private void die() {
		playSound(CharacterAction.HIT);
		changeAction(CharacterAction.DIE);
		GameUtils.runDelayed(() -> gameOver(), TIME_TILL_GAME_OVER_MENU);
	}
	
	private void gameOver() {
		gameOver = true;
	}
	
	@Override
	public boolean isGameOver() {
		return gameOver;
	}
	
	@Override
	public void removeFromMap() {
		PhysicsWorld.getInstance().removeBodyWhenPossible(body);
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected) {
		if (isAlive()) {
			Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
			force *= 10f * body.getMass();
			if (isBlocking() && blockAffected) {
				force *= 0.33;
			}
			
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
	
	private boolean isBlocking() {
		return action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.EVENT_OBJECT_TOUCHED && event.stringValue.equals(EventObject.EVENT_KEY_RESPAWN_CHECKPOINT)) {
			if (event.parameterObject != null && event.parameterObject instanceof EventObject) {
				EventObject respawnObject = (EventObject) event.parameterObject;
				properties.setRespawnPoint(respawnObject.getEventObjectCenterPosition().cpy());
			}
		}
		if (event.eventType == EventType.TAKE_PLAYERS_COINS) {
			properties.reduceCoins(event.intValue);
		}
		if (event.eventType == EventType.PLAYER_BUY_ITEM) {
			itemContainer.collectItem((Item) event.parameterObject, this);
		}
		if (event.eventType == EventType.FAST_TRAVEL_TO_MAP_POSITION) {
			CharacterFastTravelProperties fastTravelTargetPoint = fastTravelContainer.getFastTravelPropertiesById(event.stringValue);
			if (fastTravelTargetPoint.enabled) {
				setPosition(fastTravelTargetPoint.positionOnMapX, fastTravelTargetPoint.positionOnMapY);
			}
		}
		if (event.eventType == EventType.SET_ITEM) {
			String itemId = event.stringValue;
			itemContainer.addSpecialItem(itemId);
		}
	}
	
	@Override
	public void respawn() {
		Vector2 respawnPoint = properties.getRespawnPoint();
		setPosition(respawnPoint.x, respawnPoint.y);
		properties.changeStatsAfterRespawn();
		gameOver = false;
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.PLAYER_RESPAWNED));
	}
	
	@Override
	public void dispose() {
		soundSet.dispose();
		PhysicsWorld.getInstance().removeContactListener(this);
		EventHandler.getInstance().removeEventListener(this);
	}
}
