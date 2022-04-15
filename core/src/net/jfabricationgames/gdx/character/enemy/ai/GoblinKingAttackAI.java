package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.enemy.implementation.GoblinKing;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class GoblinKingAttackAI extends AbstractMultiAttackAI {
	
	private CharacterState attackSlamState;
	private CharacterState attackCoinBagState;
	
	public GoblinKingAttackAI(ArtificialIntelligence subAI, ArrayMap<String, CharacterState> attackStates,
			ArrayMap<CharacterState, Float> attackDistances, AttackTimer attackTimer) {
		super(subAI, attackStates, attackDistances, attackTimer);
		
		attackSlamState = attackStates.get(GoblinKing.STATE_NAME_JUMP); // use jump, because the following state is slam
		attackCoinBagState = attackStates.get(GoblinKing.STATE_NAME_COIN_BAG); // use coin bag, because the following state is throw
		
		/*
		 *  Use a target supplier, because the attack state is not set directly, but the attack_throw state follows the coin_bag state. 
		 *  So the target is set to the coin bag state, but not to the attack throw state.
		 *  Also the aiming is more accurate this way. 
		 */
		CharacterState attackThrowState = attackStates.get(GoblinKing.STATE_NAME_ATTACK_THROW);
		attackThrowState.setTargetDirectionSupplier(this::directionToTarget);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (stateMachine.isInState(GoblinKing.STATE_NAME_EAT)) {
			//TODO attack with force field (no state change)
		}
		else {
			if (isInRangeForAttack(attackSlamState, distanceToTarget)) {
				return attackSlamState;
			}
			else {
				//TODO command state
				
				if (isInRangeForAttack(attackCoinBagState, distanceToTarget)) {
					return attackCoinBagState;
				}
			}
		}
		
		return null;
	}
}
