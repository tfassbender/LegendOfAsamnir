package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.CharacterPhysicsUtil;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Elemental extends Enemy {
	
	public Elemental(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, 0.3f);
	}

	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setRadius(0.3f).setPhysicsBodyShape(PhysicsBodyShape.CIRCLE);
	}

	@Override
	protected void addAdditionalPhysicsParts() {
		CharacterPhysicsUtil.addEnemySensor(body, 6f);
	}

	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createPreDefinedMovementAI(ai);
		ai = createFollowAI(ai);
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createPreDefinedMovementAI(ArtificialIntelligence ai) {
		Array<Vector2> positions = loadPositionsFromMapProperties();
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		return new PreDefinedMovementAI(ai, movingState, idleState, true, positions);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		return new FollowAI(ai, movingState, idleState);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackState = stateMachine.getState("attack");
		
		return new FightAI(ai, attackState, new FixedAttackTimer(1f), 1.25f);
	}
}
