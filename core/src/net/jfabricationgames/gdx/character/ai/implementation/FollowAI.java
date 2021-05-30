package net.jfabricationgames.gdx.character.ai.implementation;

import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;

/**
 * An AI implementation that follows the player when he's in a range in which he's noticed by the enemy.
 */
public class FollowAI extends AbstractRelativeMovementAI {
	
	protected PlayableCharacter targetToFollow;
	
	/** The distance till which the controlled actor follows the target (to not push him if to near) */
	protected float minDistanceToTarget = 1f;
	protected float maxDistanceFromStart;
	
	public FollowAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI, movingState, idleState);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!absolutePositionSet) {
			updateRelativeZero(character.getPosition());
		}
		
		if (targetToFollow != null) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			if (distanceToTarget() > minDistanceToTarget && (maxDistanceFromStart <= 0 || distanceFromStart() < maxDistanceFromStart)) {
				move.movementTarget = targetToFollow.getPosition();
			}
			setMove(MoveType.MOVE, move);
		}
	}
	
	private float distanceFromStart() {
		return relativeZero.dst(character.getPosition());
	}
	
	protected float distanceToTarget() {
		return character.getPosition().sub(targetToFollow.getPosition()).len();
	}
	
	@Override
	public void executeMove(float delta) {
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			if (move.movementTarget != null) {
				if (inMovingState() || changeToMovingState()) {
					character.moveTo(move.movementTarget);
					move.executed();
				}
			}
			else {
				if (inIdleState() || changeToIdleState()) {
					move.executed();
				}
			}
		}
		
		subAI.executeMove(delta);
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingCharacter != null) {
			followTarget(collidingCharacter);
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingCharacter = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingCharacter != null) {
			stopFollowingPlayer();
		}
		
		subAI.endContact(contact);
	}
	
	protected void followTarget(PlayableCharacter player) {
		targetToFollow = player;
	}
	protected void stopFollowingPlayer() {
		targetToFollow = null;
	}
	
	public void setMinDistanceToTarget(float distance) {
		this.minDistanceToTarget = distance;
	}
	
	public void setMaxDistanceFromStart(float maxDistanceFromStart) {
		this.maxDistanceFromStart = maxDistanceFromStart;
	}
}
