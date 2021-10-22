package net.jfabricationgames.gdx.character.animal;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsUtil;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class Animal extends AbstractCharacter {
	
	private static PhysicsBodyProperties physicsBodyProperties = createDefaultPhysicsBodyProperties();
	
	private static PhysicsBodyProperties createDefaultPhysicsBodyProperties() {
		return physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false)
				.setCollisionType(PhysicsCollisionType.MAP_OBJECT).setDensity(10f).setLinearDamping(10f);
	}
	
	protected AnimalTypeConfig typeConfig;
	
	private AnimalCharacterMap gameMap;
	
	public Animal(AnimalTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		readTypeConfig();
		initializeStates();
		initializeMovingState();
		initializeIdleState();
		
		createAI();
		ai.setCharacter(this);
		
		setImageOffset(typeConfig.imageOffsetX, typeConfig.imageOffsetY);
	}
	
	private void readTypeConfig() {
		movingSpeed = typeConfig.movingSpeed;
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, null);
	}
	
	protected void createAI() {
		createAiFromConfiguration(typeConfig.aiConfig);
	}
	
	public void setGameMap(AnimalCharacterMap gameMap) {
		this.gameMap = gameMap;
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
	public void act(float delta) {
		stateMachine.updateState(delta);
		
		if (!cutsceneHandler.isCutsceneActive()) {
			ai.calculateMove(delta);
			ai.executeMove(delta);
		}
	}
	
	@Override
	protected CharacterTypeConfig getTypeConfig() {
		return typeConfig;
	}
	
	@Override
	public void removeFromMap() {
		ai.characterRemovedFromMap();
		gameMap.removeAnimal(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
}
