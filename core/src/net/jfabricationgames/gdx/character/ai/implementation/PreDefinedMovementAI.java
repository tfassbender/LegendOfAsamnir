package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class PreDefinedMovementAI extends AbstractRelativeMovementAI {
	
	private Array<Vector2> relativePositions;
	private Array<Vector2> absolutePositions;
	
	/**
	 * Update the absolute positions from the relative positions and the enemy position in the first turn (because the enemy position is not set when
	 * the AI is created).
	 */
	private boolean positionsDefined;
	
	private int targetPointIndex = 0;
	
	public PreDefinedMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, boolean relativePositions,
			Vector2... positions) {
		this(subAI, movingState, idleState, relativePositions, new Array<>(positions));
	}
	public PreDefinedMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, boolean relativePositions,
			Array<Vector2> positions) {
		super(subAI, movingState, idleState);
		positionsDefined = positions != null && !positions.isEmpty();
		absolutePositionSet = !relativePositions;
		this.relativePositions = positions;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!absolutePositionSet) {
			absolutePositions = calculateAbsolutePositions(character.getPosition(), relativePositions);
		}
		
		if (positionsDefined) {
			Vector2 targetPoint = absolutePositions.get(targetPointIndex);
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			move.movementTarget = targetPoint;
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove(float delta) {
		if (positionsDefined) {
			AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
			if (isExecutedByMe(move)) {
				Vector2 targetPoint = move.movementTarget;
				if (!isMovementWouldLeadToNearToPlayer(targetPoint, delta)) {
					if (inMovingState() || changeToMovingState()) {
						if (reachedTargetPoint(targetPoint)) {
							// next target point
							targetPointIndex = (targetPointIndex + 1) % absolutePositions.size;
						}
						else {
							character.moveTo(targetPoint, movementSpeedFactor);
						}
						move.executed();
					}
				}
			}
		}
		
		subAI.executeMove(delta);
	}
}
