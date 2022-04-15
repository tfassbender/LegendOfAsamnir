package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.attack.hit.Hittable;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledState;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledStatefullUnit;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsUtil;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.util.MapUtil;

public class Enemy extends AbstractCharacter implements Hittable, StatefulMapObject, CutsceneControlledStatefullUnit {
	
	private static final String MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT = "enemyDefeatedEventText";
	
	private static PhysicsBodyProperties physicsBodyProperties = createDefaultPhysicsBodyProperties();
	
	private static PhysicsBodyProperties createDefaultPhysicsBodyProperties() {
		return new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false).setCollisionType(PhysicsCollisionType.ENEMY).setDensity(10f)
				.setLinearDamping(10f);
	}
	
	protected EnemyHealthBarRenderer healthBarRenderer;
	
	protected EnemyTypeConfig typeConfig;
	protected AttackHandler attackHandler;
	
	protected float health;
	
	protected ObjectMap<String, Float> dropTypes;
	@MapObjectState
	protected boolean droppedItems;
	@MapObjectState
	protected boolean defeated;
	
	private EnemyCharacterMap gameMap;
	private Runnable onRemoveFromMap;
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		healthBarRenderer = new EnemyHealthBarRenderer();
		
		readTypeConfig();
		readMapProperties(properties);
		initializeAttackHandler();
		initializeStates();
		initializeMovingState();
		initializeIdleState();
		
		createAI();
		ai.setCharacter(this);
		ai.setAttackHandler(attackHandler);
		
		setImageOffset(typeConfig.imageOffsetX, typeConfig.imageOffsetY);
	}
	
	protected void readTypeConfig() {
		health = typeConfig.health;
		movingSpeed = typeConfig.movingSpeed;
	}
	
	private void readMapProperties(MapProperties mapProperties) {
		dropTypes = ItemDropUtil.processMapProperties(mapProperties, typeConfig.drops);
	}
	
	private void initializeAttackHandler() {
		//the body is not yet created -> set a null body here and update it when it is created (see createPhysicsBody(...))
		attackHandler = new AttackHandler(typeConfig.attackConfig, null, PhysicsCollisionType.ENEMY_ATTACK);
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, attackHandler);
	}
	
	/**
	 * Create the {@link ArtificialIntelligence} that controls this enemy. The default implementation creates an AI from the configuration file, that
	 * is referenced in the field typeConfig.aiConfig.
	 */
	protected void createAI() {
		createAiFromConfiguration(typeConfig.aiConfig);
	}
	
	public void setGameMap(EnemyCharacterMap gameMap) {
		this.gameMap = gameMap;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		//add the body to the attack handler, because it needed to be initialised before the body was created
		attackHandler.setBody(body);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return physicsBodyProperties.setRadius(typeConfig.bodyRadius).setWidth(typeConfig.bodyWidth).setHeight(typeConfig.bodyHeight)
				.setPhysicsBodyShape(typeConfig.bodyShape);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		if (typeConfig.addSensor) {
			PhysicsUtil.addEnemySensor(body, typeConfig.sensorRadius);
		}
	}
	
	@Override
	protected CharacterTypeConfig getTypeConfig() {
		return typeConfig;
	}
	
	@Override
	public String getMapObjectId() {
		return StatefulMapObject.getMapObjectId(properties);
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {
		if (Boolean.parseBoolean(state.get("droppedItems")) && dropsSpecialItems()) {
			//don't drop special items twice, because special items will be saved and re-added to the map if they are not picked up
			droppedItems = true;
		}
		if (Boolean.parseBoolean(state.get("defeated"))) {
			defeated = true;
			removeFromMap();
		}
	}
	
	@Override
	public CutsceneControlledState getState(String controlledUnitState) {
		return getStateMachine().getState(controlledUnitState);
	}
	
	@Override
	public void setState(CutsceneControlledState state) {
		if (state instanceof CharacterState) {
			getStateMachine().setState((CharacterState) state);
		}
		else {
			throw new IllegalArgumentException("Only states of the type CharacterState are allowed here.");
		}
	}
	
	@Override
	public void act(float delta) {
		stateMachine.updateState(delta);
		attackHandler.handleAttacks(delta);
		
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
			ai.executeMove(delta);
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		attackHandler.renderAttacks(delta, batch);
	}
	
	private boolean isAlive() {
		return health > 0;
	}
	
	public void drawHealthBar(ShapeRenderer shapeRenderer) {
		if (drawHealthBar()) {
			AnimationSpriteConfig spriteConfig = getAnimation().getSpriteConfig();
			float x = body.getPosition().x - spriteConfig.width * 0.5f * Constants.WORLD_TO_SCREEN + typeConfig.healthBarOffsetX;
			float y = body.getPosition().y + spriteConfig.height * 0.5f * Constants.WORLD_TO_SCREEN + typeConfig.healthBarOffsetY;
			float width = getAnimation().getSpriteConfig().width * typeConfig.healthBarWidthFactor;
			healthBarRenderer.drawHealthBar(shapeRenderer, getPercentualHealth(), x, y, width);
		}
	}
	
	private boolean drawHealthBar() {
		return typeConfig.usesHealthBar && getPercentualHealth() < 1 && isAlive();
	}
	
	protected float getPercentualHealth() {
		return health / typeConfig.health;
	}
	
	public void moveToDirection(float x, float y) {
		moveToDirection(new Vector2(x, y));
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
		
		defeated = true;
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
		
		if (properties.containsKey(MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT)) {
			String enemyDefeatedEventText = properties.get(MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT, "", String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DEFEATED).setStringValue(enemyDefeatedEventText));
		}
	}
	
	/**
	 * Returns the name of the state that shows the enemy dying. Override this method if the state is not named "die".
	 */
	protected String getDieStateName() {
		return "die";
	}
	
	protected void dropItems() {
		float x = (body.getPosition().x + typeConfig.dropPositionOffsetX) * Constants.SCREEN_TO_WORLD;
		float y = (body.getPosition().y + typeConfig.dropPositionOffsetY) * Constants.SCREEN_TO_WORLD;
		
		if (dropsSpecialItems()) {
			String specialDropType = properties.get(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE, String.class);
			String specialDropMapProperties = properties.get(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_MAP_PROPERTIES, String.class);
			MapProperties mapProperties = MapUtil.createMapPropertiesFromString(specialDropMapProperties);
			ItemDropUtil.dropItem(specialDropType, mapProperties, x, y, typeConfig.renderDropsAboveObject);
		}
		else {
			ItemDropUtil.dropItems(dropTypes, x, y, typeConfig.renderDropsAboveObject);
		}
		
		droppedItems = true;
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private boolean dropsSpecialItems() {
		return properties.containsKey(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE);
	}
	
	@Override
	public void removeFromMap() {
		ai.characterRemovedFromMap();
		gameMap.removeEnemy(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
		
		if (onRemoveFromMap != null) {
			onRemoveFromMap.run();
		}
	}
	
	public void setOnRemoveFromMap(Runnable onRemoveFromMap) {
		this.onRemoveFromMap = onRemoveFromMap;
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		attackHandler.handleAttackDamage(contact);
	}
}
