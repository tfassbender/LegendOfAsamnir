package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractRelativeMovementAI extends AbstractMovementAI {
	
	protected boolean absolutePositionSet = false;
	protected Vector2 relativeZero;
	
	/** The distance that is needed to the target point to assume it is reached */
	protected float distanceToReachTargetPoint = 0.1f;
	
	public AbstractRelativeMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI, movingState, idleState);
	}
	
	public void updateRelativeZero(Vector2 relativeZero) {
		this.relativeZero = relativeZero;
		absolutePositionSet = true;
	}
	
	public Array<Vector2> calculateAbsolutePositions(Vector2 relativeZero, Array<Vector2> relativePositions) {
		absolutePositionSet = true;
		
		if (relativePositions != null) {
			Array<Vector2> absolutePositions = new Array<>();
			for (Vector2 relativePosition : relativePositions) {
				absolutePositions.add(new Vector2(relativeZero).add(relativePosition));
			}
			
			return absolutePositions;
		}
		
		return null;
	}
	
	public boolean reachedTargetPoint(Vector2 targetPoint) {
		return new Vector2(targetPoint).sub(character.getPosition()).len() <= distanceToReachTargetPoint;
	}
}
