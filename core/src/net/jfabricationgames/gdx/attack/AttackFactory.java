package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.attack.implementation.BeamAttack;
import net.jfabricationgames.gdx.attack.implementation.MeleeAttack;
import net.jfabricationgames.gdx.attack.implementation.ProjectileAttack;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class AttackFactory {
	
	private AttackFactory() {}
	
	public static Attack createAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		if (config.type.isSubTypeOf(AttackType.MELEE)) {
			return new MeleeAttack(config, direction, body, collisionType);
		}
		else if (config.type.isSubTypeOf(AttackType.PROJECTILE)) {
			return new ProjectileAttack(config, direction, body, collisionType);
		}
		else if (config.type.isSubTypeOf(AttackType.BEAM)) {
			return new BeamAttack(config, direction, body, collisionType);
		}
		throw new IllegalStateException("Unexpected attack type: " + config.type);
	}
}
