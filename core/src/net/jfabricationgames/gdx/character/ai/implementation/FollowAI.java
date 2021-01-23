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
public class FollowAI extends AbstractMovementAI {
	
	private PlayableCharacter playerToFollow;
	
	/** The distance till which the enemy follows the player (to not push him if to near) */
	private float minDistanceToPlayer = 1f;
	
	public FollowAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI, movingState, idleState);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (playerToFollow != null) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			if (distanceToPlayer() > minDistanceToPlayer) {
				move.movementTarget = playerToFollow.getPosition();
			}
			setMove(MoveType.MOVE, move);
		}
	}
	
	private float distanceToPlayer() {
		return enemy.getPosition().sub(playerToFollow.getPosition()).len();
	}
	
	@Override
	public void executeMove() {
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			if (move.movementTarget != null) {
				if (inMovingState() || changeToMovingState()) {
					enemy.moveTo(move.movementTarget);
					move.executed();
				}
			}
			else {
				if (inIdleState() || changeToIdleState()) {
					move.executed();
				}
			}
		}
		
		subAI.executeMove();
	}
	
	@Override
	public void beginContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		// if the sensor touches a PlayableCharacter -> start following him
		if (collidingPlayer != null) {
			followPlayer(collidingPlayer);
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		PlayableCharacter collidingPlayer = getObjectCollidingWithEnemySensor(contact, PlayableCharacter.class);
		if (collidingPlayer != null) {
			stopFollowingPlayer();
		}
		
		subAI.endContact(contact);
	}
	
	private void followPlayer(PlayableCharacter player) {
		playerToFollow = player;
	}
	private void stopFollowingPlayer() {
		playerToFollow = null;
	}
	
	public FollowAI setMinDistanceToPlayer(float distance) {
		this.minDistanceToPlayer = distance;
		return this;
	}
}
