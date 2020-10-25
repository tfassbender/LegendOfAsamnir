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
import net.jfabricationgames.gdx.enemy.ai.implementation.RunAwayAI;
import net.jfabricationgames.gdx.enemy.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Spider extends Enemy {
	
	private static final float DISTANCE_MELEE_ATTACK = 1.5f;
	private static final float MIN_DISTANCE_TO_PLAYER = 4f;
	
	public Spider(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, 0.5f);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createPreDefinedMovementAI(ai);
		ai = createFollowAI(ai);
		ai = createRunAwayAI(ai);
		ai = createArcherFighterAI(ai);
		ai = createMeleeFightAI(ai);
	}
	
	private ArtificialIntelligence createPreDefinedMovementAI(ArtificialIntelligence ai) {
		Array<Vector2> positions = loadPositionsFromMapProperties();
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		return new PreDefinedMovementAI(ai, movingState, idleState, true, positions);
	}

	private FollowAI createFollowAI(ArtificialIntelligence ai) {
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToPlayer(MIN_DISTANCE_TO_PLAYER);
		
		return followAI;
	}
	
	private ArtificialIntelligence createRunAwayAI(ArtificialIntelligence ai) {
		EnemyState movingState = stateMachine.getState("move");
		EnemyState idleState = stateMachine.getState("idle");
		
		RunAwayAI runAwayAI = new RunAwayAI(ai, movingState, idleState);
		runAwayAI.setDistanceToKeepFromPlayer(MIN_DISTANCE_TO_PLAYER - 0.5f);
		runAwayAI.setDistanceToStopRunning(0f);
		
		return runAwayAI;
	}
	
	private ArtificialIntelligence createMeleeFightAI(ArtificialIntelligence ai) {
		EnemyState attackState = stateMachine.getState("attack_jump");
		
		FightAI fightAi = new FightAI(ai, attackState, new FixedAttackTimer(1f), DISTANCE_MELEE_ATTACK);
		
		return fightAi;
	}
	
	private ArtificialIntelligence createArcherFighterAI(ArtificialIntelligence ai) {
		EnemyState attackState = stateMachine.getState("attack");
		
		FightAI fightAi = new FightAI(ai, attackState, new FixedAttackTimer(2.5f), MIN_DISTANCE_TO_PLAYER);
		fightAi.setMinDistanceToTargetPlayer(MIN_DISTANCE_TO_PLAYER);
		
		return fightAi;
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.3f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 6f);
	}
}
