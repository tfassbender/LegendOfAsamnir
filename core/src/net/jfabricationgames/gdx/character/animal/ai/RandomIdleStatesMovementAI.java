package net.jfabricationgames.gdx.character.animal.ai;

import java.util.Random;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.config.ArtificialIntelligenceConfig.StateConfig;
import net.jfabricationgames.gdx.character.ai.implementation.RandomMovementAI;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class RandomIdleStatesMovementAI extends RandomMovementAI {
	
	private RandomIdleStatesHandler handler;
	private ObjectMap<CharacterState, StateConfig> idleStateProbabilities;
	
	private float movementProbability;
	private Random random;
	
	public RandomIdleStatesMovementAI(ArtificialIntelligence subAI, ObjectMap<CharacterState, StateConfig> idleStateProbabilities,
			float movementProbability, CharacterState movingState, CharacterState idleState, float maxDistance) {
		super(subAI, movingState, idleState, maxDistance);
		this.idleStateProbabilities = idleStateProbabilities;
		this.movementProbability = movementProbability;
		
		random = new Random();
	}
	
	@Override
	public void setCharacter(AbstractCharacter character) {
		super.setCharacter(character);
		handler = new RandomIdleStatesHandler(stateMachine, idleStateProbabilities);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!absolutePositionSet) {
			updateRelativeZero(character.getPosition());
		}
		
		if (isTargetPositionSet()) {
			AIPositionChangingMove move = createPositionChangeMove();
			setMove(MoveType.MOVE, move);
		}
		else if (!handler.isInRandomIdleState() || (handler.isInRandomIdleState() && !handler.isOverridingFollowingStateSet())) {
			AIMove move = new AIActionMove(this);
			//use the MoveType MOVE to overwrite moving actions of sub AIs and be overwritten by super AIs moving actions
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove(float delta) {
		AIPositionChangingMove aiPositionChangeMove = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(aiPositionChangeMove)) {
			if (!isMovementWouldLeadToNearToPlayer(aiPositionChangeMove.movementTarget, delta)) {
				if (inMovingState() || changeToMovingState()) {
					if (reachedTargetPoint(aiPositionChangeMove.movementTarget)) {
						resetTargetPosition();
					}
					else {
						character.moveTo(aiPositionChangeMove.movementTarget, movementSpeedFactor);
					}
					aiPositionChangeMove.executed();
				}
			}
		}
		
		AIActionMove aiMove = getMove(MoveType.MOVE, AIActionMove.class);
		if (isExecutedByMe(aiMove)) {
			if (handler.isInRandomIdleState()) {
				if (!handler.isOverridingFollowingStateSet()) {
					if (chooseMovement()) {
						executeRandomMovement();
					}
					else {
						handler.setOverridingFollowingState();
					}
				}
			}
			else {
				if (chooseMovement()) {
					executeRandomMovement();
				}
				else {
					handler.changeToRandomIdleState();
				}
			}
		}
		
		subAI.executeMove(delta);
	}
	
	private boolean chooseMovement() {
		return random.nextFloat() < movementProbability;
	}
	
	private void executeRandomMovement() {
		calculateNextTargetPoint();
	}
}
