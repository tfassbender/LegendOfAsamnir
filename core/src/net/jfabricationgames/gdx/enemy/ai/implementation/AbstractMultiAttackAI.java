package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public abstract class AbstractMultiAttackAI extends AbstractAttackAI {
	
	protected ArrayMap<String, EnemyState> attackStates;
	protected ArrayMap<EnemyState, Float> attackDistances;
	
	public AbstractMultiAttackAI(ArtificialIntelligence subAI, ArrayMap<String, EnemyState> attackStates, ArrayMap<EnemyState, Float> attackDistances,
			float timeBetweenAttacks) {
		super(subAI, null, timeBetweenAttacks);
		this.attackStates = attackStates;
		this.attackDistances = attackDistances;
	}
	
	protected boolean changeToAttackState(EnemyState state) {
		this.attackState = state;
		return super.changeToAttackState();
	}
	
	@Override
	protected boolean changeToAttackState() {
		throw new UnsupportedOperationException(
				"changeToAttackState can't be called in this implementation. Use changeToAttackState(EnemyState) instead.");
	}
	
	@Override
	protected boolean inAttackState() {
		return attackStates.containsValue(stateMachine.getCurrentState(), false);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		super.calculateMove(delta);
		
		if (timeToAttack()) {
			EnemyState attack = chooseAttack();
			if (attack != null) {
				if (!targetInRange(attackDistances.get(attack))) {
					Gdx.app.error(getClass().getSimpleName(),
							"calculateMove(): The chosen attack can't be executed, because the target is not in range.");
					return;
				}
				
				changeToAttackState(attack);
			}
		}
	}
	
	protected abstract EnemyState chooseAttack();
	
	@Override
	public void executeMove() {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			if (changeToAttackState(move.attack)) {
				move.executed();
			}
			if (inAttackState()) {
				attackState.flipAnimationToDirection(directionToTarget());
				if (distanceToTarget() > minDistanceToTargetPlayer) {
					enemy.moveTo(move.targetPosition);
				}
			}
		}
		
		subAI.executeMove();
	}
	
	protected boolean isInRangeForAttack(EnemyState attack, float distanceToTarget) {
		float attackDistance = attackDistances.get(attack, null);
		return distanceToTarget <= attackDistance;
	}
}
