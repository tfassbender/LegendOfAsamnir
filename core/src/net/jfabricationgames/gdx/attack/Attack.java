package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class Attack {
	
	private float timer;
	private boolean started;
	
	private AttackConfig config;
	private PhysicsBodyProperties hitFixtureProperties;
	
	private PhysicsCollisionType collisionType;
	
	private Fixture hitFixture;
	
	public Attack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		this.config = config;
		this.collisionType = collisionType;
		hitFixtureProperties = new PhysicsBodyProperties().setBody(body).setCollisionType(collisionType).setSensor(true)
				.setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(config.hitFixtureRadius).setFixturePosition(getFixturePosition(direction));
		timer = 0;
		started = false;
	}
	
	private Vector2 getFixturePosition(Vector2 direction) {
		return direction.nor().scl(config.distFromCenter);
	}
	
	public boolean isExecuted() {
		return started && timer >= config.delay + config.duration;
	}
	
	public boolean isToStart() {
		return !started && timer >= config.delay;
	}
	
	public void start() {
		hitFixture = PhysicsBodyCreator.addFixture(hitFixtureProperties);
		started = true;
	}
	
	public void remove() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
		}
	}
	
	public void increaseTimer(float delta) {
		timer += delta;
	}
	
	public float getDamage() {
		return config.damage;
	}
	
	public float getPushForce() {
		return config.pushForce;
	}
	
	protected void dealAttackDamage(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(collisionType, fixtureA, fixtureB)) {
			Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
			Object attackedObjectUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackUserData == hitFixtureProperties.body.getUserData() && attackedObjectUserData instanceof Hittable) {
				Hittable attackedObject = ((Hittable) attackedObjectUserData);
				attackedObject.takeDamage(config.damage);
				attackedObject.pushByHit(hitFixture.getBody().getPosition(), config.pushForce);
			}
		}
	}
}
