package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.CyclopsAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class Cyclops extends Enemy implements EventListener {
	
	public static final String STATE_NAME_IDLE = "idle";
	public static final String STATE_NAME_MOVE = "move";
	public static final String STATE_NAME_ATTACK_BEAM = "attack_beam";
	public static final String STATE_NAME_ATTACK_STOMP = "attack_stomp";
	public static final String STATE_NAME_ATTACK_THROW = "attack_throw";
	public static final String STATE_NAME_DEFENSE = "defense";
	public static final String STATE_NAME_DAMAGE_LOW = "damage_low";
	public static final String STATE_NAME_DAMAGE_HIGH = "damage_high";
	
	private static final float DAMAGE_TAKEN_IN_STATE_ATTACK_BEAM = 5f;
	private static final float FOLLOW_AI_MIN_DISTANCE_TO_PLAYER = 6f;
	
	public Cyclops(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (stateMachine.isInState(STATE_NAME_ATTACK_BEAM)) {
			if (attackType.isSubTypeOf(AttackType.ARROW)) {
				super.takeDamage(DAMAGE_TAKEN_IN_STATE_ATTACK_BEAM, attackType);
			}
		}
		else if (stateMachine.isInState(STATE_NAME_DEFENSE)) {
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
		CharacterState movingState = stateMachine.getState(STATE_NAME_MOVE);
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToPlayer(FOLLOW_AI_MIN_DISTANCE_TO_PLAYER);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackBeamState = stateMachine.getState(STATE_NAME_ATTACK_BEAM);
		CharacterState attackStompState = stateMachine.getState(STATE_NAME_ATTACK_STOMP);
		CharacterState attackThrowState = stateMachine.getState(STATE_NAME_ATTACK_THROW);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(STATE_NAME_ATTACK_BEAM, attackBeamState);
		attackStates.put(STATE_NAME_ATTACK_STOMP, attackStompState);
		attackStates.put(STATE_NAME_ATTACK_THROW, attackThrowState);
		
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
		if (stateMachine.isInState(STATE_NAME_ATTACK_BEAM)) {
			return STATE_NAME_DAMAGE_LOW;
		}
		else {
			return STATE_NAME_DAMAGE_HIGH;
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameDataService.fireQuickSaveEvent();
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
