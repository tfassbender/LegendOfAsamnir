package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.AttackCreator;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.map.TiledMapLoader;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public abstract class Enemy extends AbstractCharacter implements Hittable {
	
	protected EnemyHealthBarRenderer healthBarRenderer;
	
	protected EnemyTypeConfig typeConfig;
	protected AttackCreator attackCreator;
	
	protected Vector2 intendedMovement;
	
	protected float health;
	
	protected ObjectMap<String, Float> dropTypes;
	protected boolean droppedItems;
	
	private PhysicsBodyProperties physicsBodyProperties;
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
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
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, attackCreator);
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
	
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		//add the body to the attackCreator, because it needed to be initialized before the body was created
		attackCreator.setBody(body);
	}
	
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
				removeFromMap();
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
	
	public void moveToDirection(float x, float y) {
		moveToDirection(new Vector2(x, y));
	}
	public void moveToDirection(Vector2 pos) {
		Vector2 direction = pos.cpy().nor().scl(movingSpeed);
		move(direction);
	}
	
	public void move(Vector2 delta) {
		intendedMovement = delta;
		super.move(delta);
	}
	
	public CharacterStateMachine getStateMachine() {
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
	
	@Override
	public void removeFromMap() {
		gameMap.removeEnemy(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		attackCreator.handleAttackDamage(contact);
	}
}
