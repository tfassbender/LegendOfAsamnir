package net.jfabricationgames.gdx.character.enemy.ai;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.FixedAttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class MimicSurpriseAI extends AbstractAttackAI {
	
	private CharacterState waitingState;
	
	private float attackDistance;
	
	public MimicSurpriseAI(ArtificialIntelligence subAI, CharacterState waitingState, CharacterState surpriseState, float attackDistance) {
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
