package net.jfabricationgames.gdx.enemy.ai.implementation;

import java.util.Random;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.util.AttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class MinotaurAttackAI extends AbstractMultiAttackAI {
	
	private static final float HIT_IF_IN_RANGE = 0.5f;
	private static final float SPIN_IF_IN_RANGE = 0.25f;
	private static final float KNOCK_IF_IN_HIT_RANGE = 0.1f;
	private static final float STAB_IF_IN_RANGE = 0.7f;
	private static final float KNOCK_IF_IN_RANGE = 0.01f;
	
	private EnemyState attackHit;
	private EnemyState attackKnock;
	private EnemyState attackSpin;
	private EnemyState attackStab;
	
	private Random random;
	
	public MinotaurAttackAI(ArtificialIntelligence subAI, ArrayMap<String, EnemyState> attackStates, ArrayMap<EnemyState, Float> attackDistances,
			AttackTimer attackTimer) {
		super(subAI, attackStates, attackDistances, attackTimer);
		
		attackHit = attackStates.get("attack_hit");
		attackKnock = attackStates.get("attack_knock");
		attackSpin = attackStates.get("attack_spin");
		attackStab = attackStates.get("attack_stab");
		
		random = new Random();
	}
	
	@Override
	protected EnemyState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		float randomValue = random.nextFloat();
		if (isInRangeForAttack(attackHit, distanceToTarget) && isInRangeForAttack(attackSpin, distanceToTarget)) {
			if (randomValue < HIT_IF_IN_RANGE) {
				return attackHit;
			}
			else if (randomValue < HIT_IF_IN_RANGE + SPIN_IF_IN_RANGE) {
				return attackSpin;
			}
			else if (randomValue < HIT_IF_IN_RANGE + SPIN_IF_IN_RANGE + KNOCK_IF_IN_HIT_RANGE) {
				return attackKnock;
			}
		}
		else {
			if (isInRangeForAttack(attackStab, distanceToTarget) && randomValue < STAB_IF_IN_RANGE) {
				return attackStab;
			}
			else if (isInRangeForAttack(attackKnock, distanceToTarget)) {
				randomValue = random.nextFloat();
				if (randomValue < KNOCK_IF_IN_RANGE) {
					return attackKnock;
				}
			}
		}
		
		return null;
	}
}
