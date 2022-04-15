package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.enemy.implementation.GoblinKing;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class GoblinKingAttackAI extends AbstractMultiAttackAI {
	
	private CharacterState attackSlamState;
	private CharacterState attackCoinBagState;
	
	private AttackHandler attackHandler;
	
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
	public void calculateMove(float delta) {
		super.calculateMove(delta);
		
		if (stateMachine.isInState(GoblinKing.STATE_NAME_EAT)) {
			// attacks in the eating state are not handled in states, because the attack has to be executed multiple times
			if (attackHandler.allAttacksExecuted()) {
				attackHandler.startAttack("attack_force_field", Vector2.Zero);
			}
		}
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (!stateMachine.isInState(GoblinKing.STATE_NAME_EAT)) {
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
	
	@Override
	public void setAttackHandler(AttackHandler attackHandler) {
		this.attackHandler = attackHandler;
	}
}
