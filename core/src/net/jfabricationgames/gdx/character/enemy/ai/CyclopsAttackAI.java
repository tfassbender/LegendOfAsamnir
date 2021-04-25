package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.Random;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.enemy.implementation.Cyclops;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class CyclopsAttackAI extends AbstractMultiAttackAI {
	
	private static final float STOMP_IF_IN_RANGE = 0.5f;
	private static final float THROW_IF_IN_STOMP_RANGE = 0.3f;
	private static final float THROW_IF_IN_RANGE = 0.5f;
	private static final float BEAM_IF_IN_THROW_RANGE = 0.3f;
	private static final float BEAM_IF_IN_RANGE = 0.5f;
	private static final float BEAM_ANGLE_DELTA = 10f;
	
	private CharacterState attackBeamState;
	private CharacterState attackStompState;
	private CharacterState attackThrowState;
	
	private Random random;
	
	public CyclopsAttackAI(ArtificialIntelligence subAI, ArrayMap<String, CharacterState> attackStates,
			ArrayMap<CharacterState, Float> attackDistances, AttackTimer attackTimer) {
		super(subAI, attackStates, attackDistances, attackTimer);
		
		attackBeamState = attackStates.get(Cyclops.STATE_NAME_ATTACK_BEAM);
		attackStompState = attackStates.get(Cyclops.STATE_NAME_ATTACK_STOMP);
		attackThrowState = attackStates.get(Cyclops.STATE_NAME_ATTACK_THROW);
		
		random = new Random();
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		float angleToTarget = angleToTarget();
		float randomValue = random.nextFloat();
		
		if (isInRangeForAttack(attackStompState, distanceToTarget)) {
			if (randomValue < STOMP_IF_IN_RANGE) {
				return attackStompState;
			}
			else if (randomValue < STOMP_IF_IN_RANGE + THROW_IF_IN_STOMP_RANGE) {
				return attackThrowState;
			}
			//don't use the beam if the enemy is to near
		}
		else if (isInRangeForAttack(attackThrowState, distanceToTarget)) {
			if (randomValue < THROW_IF_IN_RANGE) {
				return attackThrowState;
			}
			else if (randomValue < THROW_IF_IN_RANGE + BEAM_IF_IN_THROW_RANGE) {
				if (isInBeamTargetAngle(angleToTarget)) {
					return attackBeamState;
				}
			}
		}
		else if (isInRangeForAttack(attackBeamState, distanceToTarget)) {
			if (randomValue < BEAM_IF_IN_RANGE) {
				if (isInBeamTargetAngle(angleToTarget)) {
					return attackBeamState;
				}
			}
		}
		
		return null;
	}
	
	private float angleToTarget() {
		if (targetingPlayer == null) {
			return -1;
		}
		
		return targetingPlayer.getPosition().sub(character.getPosition()).angleDeg();
	}
	
	private boolean isInBeamTargetAngle(float angle) {
		return (angle > 180 - BEAM_ANGLE_DELTA && angle < 180 + BEAM_ANGLE_DELTA) || (angle > 360 - BEAM_ANGLE_DELTA || angle < BEAM_ANGLE_DELTA);
	}
}
