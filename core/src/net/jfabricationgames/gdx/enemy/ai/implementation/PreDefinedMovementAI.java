package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.enemy.state.EnemyState;

public class PreDefinedMovementAI extends AbstractMovementAI implements ArtificialIntelligence {
	
	private Array<Vector2> relativePositions;
	private Array<Vector2> absolutePositions;
	/**
	 * Update the absolute positions from the relative positions and the enemy position in the first turn (because the enemy position is not set when
	 * the AI is created).
	 */
	private boolean updateAbsolutePositions;
	
	private boolean positionsDefined;
	
	private int targetPointIndex = 0;
	
	/** The distance that is needed to the target point to assume it is reached */
	private float distanceToReachTargetPoint = 0.1f;
	
	public PreDefinedMovementAI(ArtificialIntelligence subAI, EnemyState movingState, boolean relativePositions, Vector2... positions) {
		this(subAI, movingState, relativePositions, new Array<>(positions));
	}
	public PreDefinedMovementAI(ArtificialIntelligence subAI, EnemyState movingState, boolean relativePositions, Array<Vector2> positions) {
		super(subAI, movingState);
		positionsDefined = positions != null && !positions.isEmpty();
		updateAbsolutePositions = relativePositions;
		this.relativePositions = positions;
		if (positionsDefined) {
			absolutePositions = new Array<>(positions);
		}
	}
	
	public void updateAbsolutePositions(Vector2 relativeZero) {
		if (positionsDefined) {
			absolutePositions.clear();
			for (Vector2 relativePosition : relativePositions) {
				absolutePositions.add(new Vector2(relativeZero).add(relativePosition));
			}
		}
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (positionsDefined) {
			if (updateAbsolutePositions) {
				updateAbsolutePositions(enemy.getPosition());
				updateAbsolutePositions = false;
			}
			
			Vector2 targetPoint = absolutePositions.get(targetPointIndex);
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			move.movementTarget = targetPoint;
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove() {
		if (positionsDefined) {
			AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
			if (isExecutedByMe(move)) {
				if (inMovingState() || changeToMovingState()) {
					Vector2 targetPoint = move.movementTarget;
					if (reachedTargetPoint(targetPoint)) {
						// next target point
						targetPointIndex = (targetPointIndex + 1) % absolutePositions.size;
					}
					else {
						enemy.moveTo(targetPoint);
					}
					move.executed();
				}
			}
		}
		
		subAI.executeMove();
	}
	
	private boolean reachedTargetPoint(Vector2 targetPoint) {
		return new Vector2(targetPoint).sub(enemy.getPosition()).len() <= distanceToReachTargetPoint;
	}
}
