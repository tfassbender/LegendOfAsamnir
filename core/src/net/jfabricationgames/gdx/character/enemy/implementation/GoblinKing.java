package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.GoblinKingAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class GoblinKing extends Enemy {
	
	//	public static final String STATE_NAME_IDLE = "idle";
	//	public static final String STATE_NAME_MOVE = "move";
	public static final String STATE_NAME_JUMP = "jump";
	//	public static final String STATE_NAME_ATTACK_SLAM = "attack_slam";
	public static final String STATE_NAME_COIN_BAG = "coin_bag";
	public static final String STATE_NAME_ATTACK_THROW = "throw";
	public static final String STATE_NAME_COMMAND = "command";
	public static final String STATE_NAME_EAT = "eat";
	//	public static final String STATE_NAME_PANIC = "panic";
	
	public GoblinKing(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSlamState = stateMachine.getState(GoblinKing.STATE_NAME_JUMP);
		CharacterState attackCoinBagState = stateMachine.getState(GoblinKing.STATE_NAME_COIN_BAG);
		CharacterState attackThrowState = stateMachine.getState(GoblinKing.STATE_NAME_ATTACK_THROW);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(GoblinKing.STATE_NAME_JUMP, attackSlamState);
		attackStates.put(GoblinKing.STATE_NAME_COIN_BAG, attackCoinBagState);
		attackStates.put(GoblinKing.STATE_NAME_ATTACK_THROW, attackThrowState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackSlamState, 4f);
		attackDistances.put(attackCoinBagState, 10f);
		
		float minTimeBetweenAttacks = 0.5f;
		float maxTimeBetweenAttacks = 2.5f;
		
		return new GoblinKingAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
