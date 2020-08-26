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

public class RunAwayAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private PlayableCharacter player;
	
	private float distanceToKeepFromPlayer = 5f;
	private float distanceToStopRunning = 2f;
	
	public RunAwayAI(ArtificialIntelligence subAI) {
		super(subAI);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		if (player != null) {
			float distanceToPlayer = enemy.getPosition().sub(player.getPosition()).len();
			if (distanceToPlayer < distanceToKeepFromPlayer && distanceToPlayer > distanceToStopRunning) {
				AIPositionChangingMove move = new AIPositionChangingMove(this);
				move.movementDirection = enemy.getPosition().sub(player.getPosition());
				setMove(MoveType.MOVE, move);
			}
		}
	}
	
	@Override
	public void executeMove() {
		AIMove move = getMove(MoveType.MOVE);
		if (move != null && !move.isExecuted() && move.isCreatingAi(this)) {
			AIPositionChangingMove positionMove = (AIPositionChangingMove) move;
			enemy.moveToDirection(positionMove.movementDirection);
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
				runFromPlayer((PlayableCharacter) sensorCollidingUserData);
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
				stopRunningPlayer();
			}
		}
		subAI.endContact(contact);
	}
	
	private void runFromPlayer(PlayableCharacter player) {
		this.player = player;
	}
	
	private void stopRunningPlayer() {
		player = null;
	}
}
