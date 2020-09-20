package net.jfabricationgames.gdx.enemy.ai.implementation;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class FightAI extends AbstractAttackAI implements ArtificialIntelligence {
	
	private float attackDistance;
	
	public FightAI(ArtificialIntelligence subAI, EnemyState attackState, float timeBetweenAttacks, float attackDistance) {
		super(subAI, attackState, timeBetweenAttacks);
		this.attackDistance = attackDistance;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		super.calculateMove(delta);
		if (targetInRange(attackDistance)) {
			AIAttackingMove move = new AIAttackingMove(this);
			move.targetPosition = targetingPlayer.getPosition();
			setMove(MoveType.ATTACK, move);
		}
	}
	
	@Override
	public void executeMove() {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			if (changeToAttackState()) {
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
}
