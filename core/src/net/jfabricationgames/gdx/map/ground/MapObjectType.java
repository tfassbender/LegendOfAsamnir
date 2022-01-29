package net.jfabricationgames.gdx.map.ground;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public enum MapObjectType {
	
	SOLID_OBJECT(PhysicsCollisionType.MAP_OBJECT), //
	UNREACHABLE_AREA(PhysicsCollisionType.MAP_UNREACHABLE_AREA), //
	INVISIBLE_PATH_BLOCKER(PhysicsCollisionType.MAP_UNREACHABLE_AREA);
	
	public final PhysicsCollisionType collisionType;
	
	private MapObjectType(PhysicsCollisionType collisionType) {
		this.collisionType = collisionType;
	}
	
	public PhysicsCollisionType getCollisionType() {
		return collisionType;
	}
}
