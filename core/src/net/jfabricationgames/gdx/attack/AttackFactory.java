package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public abstract class AttackFactory {
	
	public static Attack createAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		switch (config.type) {
			case MELEE:
				return new MeleeAttack(config, direction, body, collisionType);
			case PROJECTILE:
				return new ProjectileAttack(config, direction, body, collisionType);
			default:
				throw new IllegalStateException("Unexpected attack type: " + config.type);
		}
	}
}
