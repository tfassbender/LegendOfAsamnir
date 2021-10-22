package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.CyclopsAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Cyclops extends Enemy implements EventListener {
	
	private static final float DAMAGE_TAKEN_IN_STATE_ATTACK_BEAM = 5f;
	private static final float FOLLOW_AI_MIN_DISTANCE_TO_PLAYER = 6f;
	
	public Cyclops(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (stateMachine.isInState(CyclopsAttackAI.STATE_NAME_ATTACK_BEAM)) {
			if (attackType.isSubTypeOf(AttackType.ARROW)) {
				super.takeDamage(DAMAGE_TAKEN_IN_STATE_ATTACK_BEAM, attackType);
			}
		}
		else if (stateMachine.isInState(CyclopsAttackAI.STATE_NAME_DEFENSE)) {
			if (attackType.isSubTypeOf(AttackType.MELEE)) {
				super.takeDamage(damage, attackType);
			}
		}
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFollowAI(ai);
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState(CyclopsAttackAI.STATE_NAME_MOVE);
		CharacterState idleState = stateMachine.getState(CyclopsAttackAI.STATE_NAME_IDLE);
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToTarget(FOLLOW_AI_MIN_DISTANCE_TO_PLAYER);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackBeamState = stateMachine.getState(CyclopsAttackAI.STATE_NAME_ATTACK_BEAM);
		CharacterState attackStompState = stateMachine.getState(CyclopsAttackAI.STATE_NAME_ATTACK_STOMP);
		CharacterState attackThrowState = stateMachine.getState(CyclopsAttackAI.STATE_NAME_ATTACK_THROW);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(CyclopsAttackAI.STATE_NAME_ATTACK_BEAM, attackBeamState);
		attackStates.put(CyclopsAttackAI.STATE_NAME_ATTACK_STOMP, attackStompState);
		attackStates.put(CyclopsAttackAI.STATE_NAME_ATTACK_THROW, attackThrowState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackBeamState, 10f);
		attackDistances.put(attackStompState, 4f);
		attackDistances.put(attackThrowState, 10f);
		
		float minTimeBetweenAttacks = 0.5f;
		float maxTimeBetweenAttacks = 1.5f;
		
		return new CyclopsAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (stateMachine.isInState(CyclopsAttackAI.STATE_NAME_ATTACK_BEAM)) {
			return CyclopsAttackAI.STATE_NAME_DAMAGE_LOW;
		}
		else {
			return CyclopsAttackAI.STATE_NAME_DAMAGE_HIGH;
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
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
	public void removeFromMap() {
		EventHandler.getInstance().removeEventListener(this);
		super.removeFromMap();
	}
}
