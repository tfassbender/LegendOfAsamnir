package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.character.ai.implementation.RunAwayAI;
import net.jfabricationgames.gdx.character.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Imp extends Enemy {
	
	private static final float MIN_DISTANCE_TO_PLAYER = 5f;

	public Imp(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, 0.3f);
	}

	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.3f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 7f);
	}

	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createPreDefinedMovementAI(ai);
		ai = createFollowAI(ai);
		ai = createRunAwayAI(ai);
		ai = createArcherFighterAI(ai);
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
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToPlayer(MIN_DISTANCE_TO_PLAYER);
		
		return followAI;
	}
	
	private ArtificialIntelligence createRunAwayAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		RunAwayAI runAwayAI = new RunAwayAI(ai, movingState, idleState);
		runAwayAI.setDistanceToKeepFromPlayer(MIN_DISTANCE_TO_PLAYER - 0.5f);
		runAwayAI.setDistanceToStopRunning(0f);
		
		return runAwayAI;
	}
	
	private ArtificialIntelligence createArcherFighterAI(ArtificialIntelligence ai) {
		CharacterState attackState = stateMachine.getState("attack");
		
		FightAI fightAi = new FightAI(ai, attackState, new FixedAttackTimer(1.5f), MIN_DISTANCE_TO_PLAYER);
		fightAi.setMinDistanceToTargetPlayer(MIN_DISTANCE_TO_PLAYER);
		
		return fightAi;
	}
}
