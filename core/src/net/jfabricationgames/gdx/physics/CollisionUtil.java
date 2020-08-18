package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class CollisionUtil {
	
	/**
	 * Checks whether at least one of the fixtures is of the given collision type
	 */
	public static boolean containsCollisionType(PhysicsCollisionType collisionType, Fixture fixtureA, Fixture fixtureB) {
		return PhysicsCollisionType.getByFilter(fixtureA.getFilterData()) == collisionType
				|| PhysicsCollisionType.getByFilter(fixtureB.getFilterData()) == collisionType;
	}
	
	/**
	 * Returns the fixture that's collision type is the given type. If both are of the given collision type the first one is returned. If none of them
	 * is of the given type null is returned.
	 */
	public static Fixture getCollisionTypeFixture(PhysicsCollisionType collisionType, Fixture fixtureA, Fixture fixtureB) {
		if (PhysicsCollisionType.getByFilter(fixtureA.getFilterData()) == collisionType) {
			return fixtureA;
		}
		else if (PhysicsCollisionType.getByFilter(fixtureB.getFilterData()) == collisionType) {
			return fixtureB;
		}
		return null;
	}
	/**
	 * Returns the user data of the body of the fixture that's collision type is the given type. If both are of the given collision type the first one
	 * is returned. If none of them is of the given type null is returned.
	 */
	public static Object getCollisionTypeUserData(PhysicsCollisionType collisionType, Fixture fixtureA, Fixture fixtureB) {
		if (PhysicsCollisionType.getByFilter(fixtureA.getFilterData()) == collisionType) {
			return fixtureA.getBody().getUserData();
		}
		else if (PhysicsCollisionType.getByFilter(fixtureB.getFilterData()) == collisionType) {
			return fixtureB.getBody().getUserData();
		}
		return null;
	}
	
	/**
	 * Returns the fixture that's collision type is NOT the given type. If both are of the given collision type the second one is returned. If none of
	 * them is of the given type null is returned.
	 */
	public static Fixture getOtherTypeFixture(PhysicsCollisionType collisionType, Fixture fixtureA, Fixture fixtureB) {
		if (PhysicsCollisionType.getByFilter(fixtureA.getFilterData()) == collisionType) {
			return fixtureB;
		}
		else if (PhysicsCollisionType.getByFilter(fixtureB.getFilterData()) == collisionType) {
			return fixtureA;
		}
		return null;
	}
	
	/**
	 * Returns the user data of the body of the fixture that's collision type is NOT the given type. If both are of the given collision type the
	 * second one is returned. If none of them is of the given type null is returned.
	 */
	public static Object getOtherTypeUserData(PhysicsCollisionType collisionType, Fixture fixtureA, Fixture fixtureB) {
		if (PhysicsCollisionType.getByFilter(fixtureA.getFilterData()) == collisionType) {
			return fixtureB.getBody().getUserData();
		}
		else if (PhysicsCollisionType.getByFilter(fixtureB.getFilterData()) == collisionType) {
			return fixtureA.getBody().getUserData();
		}
		return null;
	}
}
