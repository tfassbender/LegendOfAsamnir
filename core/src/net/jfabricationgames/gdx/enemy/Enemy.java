package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
import net.jfabricationgames.gdx.enemy.state.EnemyStateMachine;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.TiledMapLoader;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public abstract class Enemy implements Hittable, ContactListener, CutsceneControlledUnit, CutsceneMoveableUnit {
	
	public static final String MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS = "predefinedMovementPositions";
	
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected EnemyHealthBarRenderer healthBarRenderer;
	
	protected EnemyTypeConfig typeConfig;
	protected EnemyStateMachine stateMachine;
	protected EnemyState movingState;
	protected ArtificialIntelligence ai;
	protected AttackCreator attackCreator;
	protected CutsceneHandler cutsceneHandler;
	
	protected MapProperties properties;
	protected GameMap gameMap;
	protected Body body;
	protected Vector2 intendedMovement;
	
	protected float health;
	protected float movingSpeed;
	
	protected ObjectMap<String, Float> dropTypes;
	protected boolean droppedItems;
	
	private PhysicsBodyProperties physicsBodyProperties;
	
	private float imageOffsetX;
	private float imageOffsetY;
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		this.typeConfig = typeConfig;
		this.properties = properties;
		physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false)
				.setCollisionType(PhysicsCollisionType.ENEMY).setDensity(10f).setLinearDamping(10f);
		PhysicsWorld.getInstance().registerContactListener(this);
		intendedMovement = new Vector2();
		healthBarRenderer = new EnemyHealthBarRenderer();
		cutsceneHandler = CutsceneHandler.getInstance();
		
		readTypeConfig();
		readMapProperties(properties);
		initializeAttackCreator();
		initializeStates();
		createAI();
		setMovingState();
		ai.setEnemy(this);
	}
	
	protected void readTypeConfig() {
		health = typeConfig.health;
		movingSpeed = typeConfig.movingSpeed;
	}
	
	private void readMapProperties(MapProperties mapProperties) {
		dropTypes = ItemDropUtil.processMapProperties(mapProperties, typeConfig.drops);
	}
	
	private void initializeAttackCreator() {
		//the body is not yet created -> set a null body here and update it when it is created (see createPhysicsBody(...))
		attackCreator = new AttackCreator(typeConfig.attackConfig, null, PhysicsCollisionType.ENEMY_ATTACK);
	}
	
	private void initializeStates() {
		stateMachine = new EnemyStateMachine(typeConfig.stateConfig, typeConfig.initialState, attackCreator);
	}
	
	/**
	 * Create the {@link ArtificialIntelligence} that controls this enemy.
	 */
	protected abstract void createAI();
	
	private void setMovingState() {
		movingState = stateMachine.getState(getMovingStateName());
		if (movingState == null) {
			throw new IllegalStateException("Moving state not found. Maybe the 'getMovingStateName()' method needs to be overwritten?");
		}
	}
	
	protected String getMovingStateName() {
		return "move";
	}
	
	@SuppressWarnings("unchecked")
	protected Array<Vector2> loadPositionsFromMapProperties() {
		String predefinedMovingPositions = properties.get(MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS, String.class);
		if (predefinedMovingPositions != null) {
			try {
				Json json = new Json();
				return (Array<Vector2>) json.fromJson(Array.class, Vector2.class, predefinedMovingPositions);
			}
			catch (SerializationException e) {
				throw new IllegalStateException("A predefined movement string could not be parsed: \"" + predefinedMovingPositions
						+ "\". Complete map properties: " + TiledMapLoader.mapPropertiesToString(properties, true), e);
			}
		}
		return null;
	}
	
	/**
	 * Called from the factory to create a box2d physics body for this enemy.
	 */
	public void createPhysicsBody(World world, float x, float y) {
		PhysicsBodyProperties properties = definePhysicsBodyProperties();
		properties.setX(x).setY(y);
		body = PhysicsBodyCreator.createBody(world, properties);
		addAdditionalPhysicsParts();
		body.setUserData(this);
		//add the body to the attackCreator, because it needed to be initialized before the body was created
		attackCreator.setBody(body);
	}
	
	protected abstract PhysicsBodyProperties definePhysicsBodyProperties();
	
	protected abstract void addAdditionalPhysicsParts();
	
	protected PhysicsBodyProperties getDefaultPhysicsBodyProperties() {
		return physicsBodyProperties.clone();
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		return stateMachine.getCurrentState().getAnimation();
	}
	
	public void act(float delta) {
		stateMachine.updateState(delta);
		attackCreator.handleAttacks(delta);
		
		if (!isAlive()) {
			if (!droppedItems) {
				dropItems();
			}
			if (stateMachine.isInEndState()) {
				remove();
			}
		}
		else if (!cutsceneHandler.isCutsceneActive()) {
			ai.calculateMove(delta);
			ai.executeMove();
		}
	}
	
	private boolean isAlive() {
		return health > 0;
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (getAnimation() != null) {
			getAnimation().increaseStateTime(delta);
			TextureRegion region = getAnimation().getKeyFrame();
			getAnimation().getSpriteConfig().setX((body.getPosition().x - region.getRegionWidth() * 0.5f + imageOffsetX))
					.setY((body.getPosition().y - region.getRegionHeight() * 0.5f + imageOffsetY));
			stateMachine.flipTextureToMovementDirection(region, intendedMovement);
			getAnimation().draw(batch);
		}
	}
	
	public void drawHealthBar(ShapeRenderer shapeRenderer) {
		if (drawHealthBar()) {
			AnimationSpriteConfig spriteConfig = getAnimation().getSpriteConfig();
			float x = body.getPosition().x - spriteConfig.width * 0.5f * GameScreen.WORLD_TO_SCREEN + typeConfig.healthBarOffsetX;
			float y = body.getPosition().y + spriteConfig.height * 0.5f * GameScreen.WORLD_TO_SCREEN + typeConfig.healthBarOffsetY;
			float width = getAnimation().getSpriteConfig().width * typeConfig.healthBarWidthFactor;
			healthBarRenderer.drawHealthBar(shapeRenderer, getPercentualHealth(), x, y, width);
		}
	}
	
	private boolean drawHealthBar() {
		return typeConfig.usesHealthBar && getPercentualHealth() < 1 && isAlive();
	}
	
	private float getPercentualHealth() {
		return health / typeConfig.health;
	}
	
	@Override
	public String getUnitId() {
		return properties.get(CutsceneControlledUnit.MAP_PROPERTIES_KEY_UNIT_ID, String.class);
	}
	
	@Override
	public void changeToMovingState() {
		if (!stateMachine.getCurrentState().equals(movingState)) {
			stateMachine.setState(movingState);
		}
	}
	
	@Override
	public Vector2 getPosition() {
		return new Vector2(body.getPosition());
	}
	
	public void moveTo(Vector2 pos) {
		moveTo(pos, 1f);
	}
	@Override
	public void moveTo(Vector2 pos, float speedFactor) {
		Vector2 direction = pos.cpy().sub(getPosition());
		direction.nor().scl(movingSpeed * speedFactor);
		
		move(direction);
	}
	
	public void moveToDirection(float x, float y) {
		moveToDirection(new Vector2(x, y));
	}
	public void moveToDirection(Vector2 pos) {
		Vector2 direction = pos.cpy().nor().scl(movingSpeed);
		move(direction);
	}
	
	private void move(Vector2 delta) {
		intendedMovement = delta;
		float force = 10f * body.getMass();
		body.applyForceToCenter(delta.x * force, delta.y * force, true);
	}
	
	protected void setImageOffset(float x, float y) {
		this.imageOffsetX = x;
		this.imageOffsetY = y;
	}
	
	public EnemyStateMachine getStateMachine() {
		return stateMachine;
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (isAlive()) {
			health -= damage;
			
			if (!isAlive()) {
				die();
				changeBodyToSensor();
			}
			else {
				stateMachine.setState(getDamageStateName(damage));
			}
		}
	}
	
	protected void changeBodyToSensor() {
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setSensor(true);
		}
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected) {
		if (hasBody() && isAlive()) {
			Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
			//enemies define the force to get pushed themselves (the player's attack is multiplied to this self defined force as a factor)
			force *= typeConfig.pushForceDamage * 10f * body.getMass();
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
	
	private boolean hasBody() {
		return body != null;
	}
	
	/**
	 * Returns the name of the state that shows the enemy taking damage. Override this method if the state is not named "damage".
	 */
	protected String getDamageStateName(float damage) {
		return "damage";
	}
	
	protected void die() {
		stateMachine.setState(getDieStateName());
	}
	
	/**
	 * Returns the name of the state that shows the enemy dying. Override this method if the state is not named "die".
	 */
	protected String getDieStateName() {
		return "die";
	}
	
	protected void dropItems() {
		float x = (body.getPosition().x + typeConfig.dropPositionOffsetX) * GameScreen.SCREEN_TO_WORLD;
		float y = (body.getPosition().y + typeConfig.dropPositionOffsetY) * GameScreen.SCREEN_TO_WORLD;
		
		if (properties.containsKey(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE)) {
			String specialDropType = properties.get(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE, String.class);
			String specialDropMapProperties = properties.get(ItemDropUtil.MAP_PROPERTY_KEY_SPECIAL_DROP_MAP_PROPERTIES, String.class);
			MapProperties mapProperties = TiledMapLoader.createMapPropertiesFromString(specialDropMapProperties);
			ItemDropUtil.dropItem(specialDropType, mapProperties, gameMap, x, y, typeConfig.renderDropsAboveObject);
		}
		else {
			ItemDropUtil.dropItems(dropTypes, gameMap, x, y, typeConfig.renderDropsAboveObject);
		}
		droppedItems = true;
	}
	
	public void remove() {
		gameMap.removeEnemy(this);
		removePhysicsBody();
	}
	
	private void removePhysicsBody() {
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;
	}
	
	@Override
	public void beginContact(Contact contact) {
		ai.beginContact(contact);
		attackCreator.handleAttackDamage(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		ai.endContact(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		ai.preSolve(contact, oldManifold);
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		ai.postSolve(contact, impulse);
	}
}
