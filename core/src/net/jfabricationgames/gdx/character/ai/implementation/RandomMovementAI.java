package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class RandomMovementAI extends AbstractRelativeMovementAI {
	
	private Vector2 targetPosition;
	
	private float maxDistance;
	
	public RandomMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, float maxDistance) {
		super(subAI, movingState, idleState);
		this.maxDistance = maxDistance;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!absolutePositionSet) {
			updateRelativeZero(character.getPosition());
		}
		
		if (maxDistance > 0) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			move.movementTarget = targetPosition;
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove(float delta) {
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			Vector2 targetPoint = move.movementTarget;
			if (!isMovementWouldLeadToNearToPlayer(targetPoint, delta)) {
				if (inMovingState() || changeToMovingState()) {
					if (targetPoint == null || reachedTargetPoint(targetPoint)) {
						calculateNextTargetPoint();
					}
					else {
						character.moveTo(targetPoint, movementSpeedFactor);
					}
					move.executed();
				}
			}
		}
		
		subAI.executeMove(delta);
	}
	
	private void calculateNextTargetPoint() {
		targetPosition = new Vector2(1, 1);//don't use a null-vector; otherwise setLength will not work
		targetPosition.setLength((float) (Math.random() * maxDistance));
		targetPosition.setAngleDeg((float) (Math.random() * 360f));
		targetPosition.add(relativeZero);
	}
}
