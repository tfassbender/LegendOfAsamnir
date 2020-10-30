package net.jfabricationgames.gdx.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.BaseAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FightAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.enemy.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
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
		EnemyPhysicsUtil.addSensor(body, 6f);
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
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		return new PreDefinedMovementAI(ai, movingState, idleState, true, positions);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		return new FollowAI(ai, movingState, idleState);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		EnemyState attackState = stateMachine.getState("attack");
		
		return new FightAI(ai, attackState, new FixedAttackTimer(1f), 1.25f);
	}
}
