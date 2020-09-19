package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.projectile.ProjectileFactory;

public class ProjectileAttack extends Attack {
	
	public ProjectileAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
	}
	
	@Override
	protected void start() {
		Projectile projectile = ProjectileFactory.getInstance().createProjectile(config.projectileType, body.getPosition(), direction, collisionType);
		projectile.setDamage(config.damage);
		projectile.setPushForce(config.pushForce);
		projectile.setExplosionDamage(config.explosionDamage);
		projectile.setExplosionPushForce(config.explosionPushForce);
		
		started = true;
	}
	
	@Override
	protected void remove() {
		// the projectile will remove itself
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		// damage is calculated in the projectile
	}
}
