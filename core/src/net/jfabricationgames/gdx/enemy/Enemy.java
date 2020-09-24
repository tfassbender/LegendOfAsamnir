package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.state.EnemyStateMachine;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public abstract class Enemy implements Hittable, ContactListener {
	
	public static final String MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS = "predefinedMovementPositions";
	
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected EnemyTypeConfig typeConfig;
	protected EnemyStateMachine stateMachine;
	protected ArtificialIntelligence ai;
	protected AttackCreator attackCreator;
	
	protected MapProperties properties;
	protected GameMap gameMap;
	protected Body body;
	
	private PhysicsBodyProperties physicsBodyProperties;
	
	protected float health;
	protected float movingSpeed;
	
	private float imageOffsetX;
	private float imageOffsetY;
	
	protected Vector2 intendedMovement;
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		this.typeConfig = typeConfig;
		this.properties = properties;
		physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false)
				.setCollisionType(PhysicsCollisionType.ENEMY).setDensity(10f).setLinearDamping(10f);
		PhysicsWorld.getInstance().registerContactListener(this);
		intendedMovement = new Vector2();
		
		readTypeConfig();
		initializeAttackCreator();
		initializeStates();
		createAI();
		ai.setEnemy(this);
	}
	
	protected void readTypeConfig() {
		health = typeConfig.health;
		movingSpeed = typeConfig.movingSpeed;
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
	
	@SuppressWarnings("unchecked")
	protected Array<Vector2> loadPositionsFromMapProperties() {
		String predefinedMovingPositions = properties.get(MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS, String.class);
		if (predefinedMovingPositions != null) {
			Json json = new Json();
			return (Array<Vector2>) json.fromJson(Array.class, Vector2.class, predefinedMovingPositions);
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
		stateMachine.updateState();
		attackCreator.handleAttacks(delta);
		
		if (!isAlive()) {
			if (getAnimation() == null || getAnimation().isAnimationFinished()) {
				remove();
			}
		}
		else {
			ai.calculateMove(delta);
			ai.executeMove();
		}
	}
	
	private boolean isAlive() {
		return health > 0;
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (getAnimation() != null && !getAnimation().isAnimationFinished()) {
			getAnimation().increaseStateTime(delta);
			TextureRegion region = getAnimation().getKeyFrame();
			stateMachine.flipTextureToMovementDirection(region, intendedMovement);
			float x = body.getPosition().x - region.getRegionWidth() * 0.5f + imageOffsetX;
			float y = body.getPosition().y - region.getRegionHeight() * 0.5f + imageOffsetY;
			batch.draw(region, x, y, region.getRegionWidth() * 0.5f, region.getRegionHeight() * 0.5f, region.getRegionWidth(),
					region.getRegionHeight(), GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
		}
	}
	
	public void moveTo(float x, float y) {
		moveTo(new Vector2(x, y), false);
	}
	public void moveTo(Vector2 pos) {
		moveTo(pos, false);
	}
	public void moveTo(Vector2 pos, boolean slowDown) {
		Vector2 direction = pos.cpy().sub(getPosition());
		if (!slowDown || direction.len() > movingSpeed) {
			direction.nor().scl(movingSpeed);
		}
		
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
	
	public Vector2 getPosition() {
		return new Vector2(body.getPosition());
	}
	
	protected void setImageOffset(float x, float y) {
		this.imageOffsetX = x;
		this.imageOffsetY = y;
	}
	
	public EnemyStateMachine getStateMachine() {
		return stateMachine;
	}
	
	@Override
	public void takeDamage(float damage) {
		if (isAlive()) {
			health -= damage;
			
			if (!isAlive()) {
				die();
			}
			else {
				stateMachine.setState(getDamageStateName(damage));
			}
		}
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected) {
		if (hasBody()) {
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
