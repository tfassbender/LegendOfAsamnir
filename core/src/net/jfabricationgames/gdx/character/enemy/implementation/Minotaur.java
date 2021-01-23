package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.ActionAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.MinotaurAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Minotaur extends Enemy implements EventListener {
	
	public Minotaur(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		setImageOffset(0f, -0.2f);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.OCTAGON).setWidth(1.2f).setHeight(1.8f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 10f);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.MELEE)) {
			super.takeDamage(damage, attackType);
		}
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFollowAI(ai);
		ai = createMinotaurAttackAI(ai);
		ai = createActionAI(ai);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToPlayer(1.7f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createMinotaurAttackAI(ArtificialIntelligence ai) {
		String attackNameHit = "attack_hit";
		String attackNameKnock = "attack_knock";
		String attackNameSpin = "attack_spin";
		String attackNameStab = "attack_stab";
		
		CharacterState attackHitState = stateMachine.getState(attackNameHit);
		CharacterState attackKnockState = stateMachine.getState(attackNameKnock);
		CharacterState attackSpinState = stateMachine.getState(attackNameSpin);
		CharacterState attackStabState = stateMachine.getState(attackNameStab);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(attackNameHit, attackHitState);
		attackStates.put(attackNameKnock, attackKnockState);
		attackStates.put(attackNameSpin, attackSpinState);
		attackStates.put(attackNameStab, attackStabState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackHitState, 2.5f);
		attackDistances.put(attackKnockState, 4f);
		attackDistances.put(attackSpinState, 2.5f);
		attackDistances.put(attackStabState, 3f);
		
		float minTimeBetweenAttacks = 1f;
		float maxTimeBetweenAttacks = 2.5f;
		
		return new MinotaurAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	
	private ArtificialIntelligence createActionAI(ArtificialIntelligence ai) {
		CharacterState tauntState = stateMachine.getState("taunt");
		float timeBetweenActions = 5f;
		float minDistToEnemy = 5f;
		float maxDistToEnemy = 7f;
		
		return new ActionAI(ai, tauntState, minDistToEnemy, maxDistToEnemy, timeBetweenActions);
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (damage >= 15) {
			return "damage_high";
		}
		else {
			return "damage_low";
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAYER_RESPAWNED) {
			resetHealthToMaximum();
		}
	}
	
	private void resetHealthToMaximum() {
		health = typeConfig.health;
	}
	
	@Override
	public void remove() {
		EventHandler.getInstance().removeEventListener(this);
		super.remove();
	}
}
