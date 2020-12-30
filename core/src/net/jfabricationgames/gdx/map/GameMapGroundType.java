package net.jfabricationgames.gdx.map;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class GameMapGroundType {
	
	public static GameMapGroundType handleGameMapGroundContact(Contact contact, PhysicsCollisionType targetCollisionType, GameMapGroundType groundProperties) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(targetCollisionType, fixtureA, fixtureB)) {
			Object collidingUserData = CollisionUtil.getOtherTypeUserData(targetCollisionType, fixtureA, fixtureB);
			
			if (collidingUserData instanceof GameMapGroundType) {
				contact.setEnabled(false);
				return applyGameMapGroundEffects(groundProperties, (GameMapGroundType) collidingUserData);
			}
			else if (collidingUserData instanceof GameMapGroundTypeContainer) {
				contact.setEnabled(false);
				return applyGameMapGroundEffects(groundProperties, ((GameMapGroundTypeContainer) collidingUserData).getGameMapGroundType());
			}
		}
		
		return null;
	}
	
	private static GameMapGroundType applyGameMapGroundEffects(GameMapGroundType groundProperties, GameMapGroundType collidingGroundType) {
		if (groundProperties == GameMap.DEFAULT_GROUND_PROPERTIES) {
			groundProperties = new GameMapGroundType();
		}
		groundProperties.movementSpeedFactor = Math.min(groundProperties.movementSpeedFactor, collidingGroundType.movementSpeedFactor);
		
		return groundProperties;
	}
	
	public float movementSpeedFactor = 1f;
}
