package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.move.AIMove;
import net.jfabricationgames.gdx.enemy.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.enemy.ai.move.MoveType;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An AI implementation that follows the player when he's in a range in which he's noticed by the enemy.
 */
public class FollowAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private PlayableCharacter playerToFollow;
	
	/** The distance till which the enemy follows the player (to not push him if to near) */
	private float distance = 1f;
	
	public FollowAI(ArtificialIntelligence subAI) {
		super(subAI);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		if (playerToFollow != null && enemy.getPosition().sub(playerToFollow.getPosition()).len() > distance) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			move.movementTarget = playerToFollow.getPosition();
			setMove(MoveType.MOVE, move);
		}
	}
	
	@Override
	public void executeMove() {
		AIMove move = getMove(MoveType.MOVE);
		if (move != null && !move.isExecuted() && move.isCreatingAi(this)) {
			AIPositionChangingMove positionMove = (AIPositionChangingMove) move;
			enemy.moveTo(positionMove.movementTarget);
			move.executed();
		}
		subAI.executeMove();
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB)) {
			Object sensorUserData = CollisionUtil.getCollisionTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
			
			// if the sensor touches a PlayableCharacter -> start following him
			if (sensorUserData == enemy && sensorCollidingUserData instanceof PlayableCharacter) {
				followPlayer((PlayableCharacter) sensorCollidingUserData);
			}
		}
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		// if the sensor looses touch of a PlayableCharacter -> stop following
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB)) {
			Object sensorUserData = CollisionUtil.getCollisionTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
			
			if (sensorUserData == enemy && sensorCollidingUserData instanceof PlayableCharacter) {
				stopFollowingPlayer();
			}
		}
		subAI.endContact(contact);
	}
	
	private void followPlayer(PlayableCharacter player) {
		playerToFollow = player;
	}
	private void stopFollowingPlayer() {
		playerToFollow = null;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public FollowAI setDistance(float distance) {
		this.distance = distance;
		return this;
	}
}
