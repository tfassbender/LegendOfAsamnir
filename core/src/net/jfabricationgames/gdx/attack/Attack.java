package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public abstract class Attack {
	
	protected float timer;
	protected boolean started;
	
	protected AttackConfig config;
	protected PhysicsBodyProperties hitFixtureProperties;
	
	protected PhysicsCollisionType collisionType;
	
	protected Fixture hitFixture;
	
	protected Body body;
	protected Vector2 direction;
	
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
	
	protected Attack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		this.config = config;
		this.collisionType = collisionType;
		this.direction = direction;
		this.body = body;
		
		timer = 0;
		started = false;
	}
	
	protected boolean isExecuted() {
		return started && timer >= config.delay + config.duration;
	}
	
	protected boolean isToStart() {
		return !started && timer >= config.delay;
	}
	
	protected void increaseTimer(float delta) {
		timer += delta;
	}
	
	protected abstract void start();
	
	protected abstract void remove();
	
	protected abstract void dealAttackDamage(Contact contact);
}
