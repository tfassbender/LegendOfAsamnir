package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterPhysicsUtil;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class NonPlayableCharacter extends AbstractCharacter {
	
	NonPlayableCharacterTypeConfig typeConfig;
	
	public NonPlayableCharacter(NonPlayableCharacterTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		initializeStates();
		createAi();
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, null);
	}
	
	private void createAi() {
		ai = typeConfig.aiConfig.buildAI(stateMachine, properties);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false).setCollisionType(PhysicsCollisionType.OBSTACLE)
				.setDensity(10f).setLinearDamping(10f).setPhysicsBodyShape(PhysicsBodyShape.OCTAGON).setWidth(typeConfig.bodyWidth)
				.setHeight(typeConfig.bodyHeight);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		CharacterPhysicsUtil.addNpcSensor(body, 2f);
	}
	
	@Override
	public void act(float delta) {
		stateMachine.updateState(delta);
		
		if (!cutsceneHandler.isCutsceneActive()) {
			ai.calculateMove(delta);
			ai.executeMove();
		}
	}
	
	@Override
	protected void updateTextureDirection(TextureRegion region) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeFromMap() {
		gameMap.removeNpc(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
}
