package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractRelativeMovementAI extends AbstractMovementAI {
	
	protected boolean absolutePositionSet = false;
	protected Vector2 relativeZero;
	
	/** The distance that is needed to the target point to assume it is reached */
	protected float distanceToReachTargetPoint = 0.1f;
	
	/** 
	 * The distance to keep from the player to avoid moving to the player and away from the player to often, 
	 * which results in many direction changes, that make the animation look wired
	 */
	protected float distanceToKeepFromPlayer = 0f;
	
	private final float idleTimeBetweenMovements;
	private float idleTime;
	
	protected PlayableCharacter playerCharacter;
	
	public AbstractRelativeMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState,
			float idleTimeBetweenMovements) {
		super(subAI, movingState, idleState);
		
		this.idleTimeBetweenMovements = idleTimeBetweenMovements;
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
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingCharacter != null) {
			playerCharacter = collidingCharacter;
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingCharacter != null) {
			playerCharacter = null;
		}
		
		subAI.endContact(contact);
	}
	
	/**
	 * Check whether the next movement to the targetPoint would move the character to near to the player.
	 * Used to avoid moving close to the player and then away from the player, which leads to many direction changes and wired animations.
	 */
	protected boolean isMovementWouldLeadToNearToPlayer(Vector2 targetPoint, float delta) {
		if (playerCharacter == null || distanceToKeepFromPlayer <= 0f) {
			return false;
		}
		
		Vector2 movement = targetPoint.cpy().sub(character.getPosition()).nor().scl(character.getMovingSpeed() * delta);
		Vector2 positionAfterMovement = character.getPosition().add(movement);
		Vector2 distanceVector = positionAfterMovement.cpy().sub(playerCharacter.getPosition());
		float distanceAfterMovement = distanceVector.len();
		
		return distanceAfterMovement < distanceToKeepFromPlayer;
	}
	
	public boolean reachedTargetPoint(Vector2 targetPoint) {
		return new Vector2(targetPoint).sub(character.getPosition()).len() <= distanceToReachTargetPoint;
	}
	
	public void setDistanceToKeepFromPlayer(float distanceToKeepFromPlayer) {
		this.distanceToKeepFromPlayer = distanceToKeepFromPlayer;
	}
	
	protected boolean waitBetweenMovements(float delta) {
		idleTime += delta;
		return idleTime < idleTimeBetweenMovements;
	}
	
	protected void resetIdleTimeBetweenMovements() {
		idleTime = 0;
	}
}
