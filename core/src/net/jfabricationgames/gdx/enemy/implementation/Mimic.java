package net.jfabricationgames.gdx.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.enemy.Enemy;
import net.jfabricationgames.gdx.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.BaseAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FightAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.enemy.ai.implementation.MimicSurpriseAI;
import net.jfabricationgames.gdx.enemy.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Mimic extends Enemy {
	
	public Mimic(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, 0f);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setWidth(0.5f).setHeight(0.5f).setPhysicsBodyShape(PhysicsBodyShape.RECTANGLE);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 4f);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFollowAI(ai);
		ai = createFightAI(ai);
		ai = createMimicSupriseAI(ai);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		return new FollowAI(ai, movingState, idleState);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		EnemyState attackState = stateMachine.getState("attack");
		
		return new FightAI(ai, attackState, new FixedAttackTimer(0.5f), 1.25f);
	}
	
	private ArtificialIntelligence createMimicSupriseAI(ArtificialIntelligence ai) {
		EnemyState waitingState = stateMachine.getState("waiting");
		EnemyState surpriseState = stateMachine.getState("surprise");
		
		return new MimicSurpriseAI(ai, waitingState, surpriseState, 1f);
	}
}
