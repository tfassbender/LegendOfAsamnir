package net.jfabricationgames.gdx.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.enemy.ai.BaseAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FightAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.RunAwayAI;
import net.jfabricationgames.gdx.enemy.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Bat extends Enemy {
	
	public Bat(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, -0.2f);
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
		Array<Vector2> positions = loadPositionsFromMapProperties();
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = movingState;
		EnemyState attackState = stateMachine.getState("attack");
		
		ai = new BaseAI();
		ai = new PreDefinedMovementAI(ai, movingState, idleState, true, positions);
		ai = new RunAwayAI(ai, movingState, idleState);
		ai = new FightAI(ai, attackState, new FixedAttackTimer(0f), 2f);
	}
}
