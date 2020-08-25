package net.jfabricationgames.gdx.enemy.ai.implementation;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An AI implementation that follows the player when he's in a range in which he's noticed by the enemy.
 */
public class FollowingAI extends AbstractArtificialIntelligence implements ArtificialIntelligence {
	
	private PlayableCharacter playerToFollow;
	
	public FollowingAI(ArtificialIntelligence subAI) {
		super(subAI);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		//TODO calculate move
	}
	
	@Override
	public void executeMove() {
		//TODO check if the move is still planed and not overturned by a higher AI
		if (playerToFollow != null) {
			enemy.moveTo(playerToFollow.getPosition());
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
				return;
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
				return;
			}
		}
		subAI.beginContact(contact);
	}
	
	private void followPlayer(PlayableCharacter player) {
		playerToFollow = player;
	}
	private void stopFollowingPlayer() {
		playerToFollow = null;
	}
}
