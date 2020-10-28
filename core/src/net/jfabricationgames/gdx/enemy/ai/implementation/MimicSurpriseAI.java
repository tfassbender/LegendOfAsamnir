package net.jfabricationgames.gdx.enemy.ai.implementation;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.enemy.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class MimicSurpriseAI extends AbstractAttackAI implements ArtificialIntelligence {
	
	private EnemyState waitingState;
	
	private float attackDistance;
	
	public MimicSurpriseAI(ArtificialIntelligence subAI, EnemyState waitingState, EnemyState surpriseState, float attackDistance) {
		super(subAI, surpriseState, new FixedAttackTimer(0f));
		this.waitingState = waitingState;
		this.attackDistance = attackDistance;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (inWaitingState()) {
			if (targetInRange(attackDistance)) {
				AIAttackingMove move = new AIAttackingMove(this);
				move.targetPosition = targetingPlayer.getPosition();
				setMove(MoveType.ATTACK, move);
			}
		}
	}
	
	protected boolean inWaitingState() {
		return stateMachine.getCurrentState() == waitingState;
	}
	
	@Override
	public void executeMove() {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			if (changeToAttackState()) {
				move.executed();
			}
		}
		
		subAI.executeMove();
	}
}
