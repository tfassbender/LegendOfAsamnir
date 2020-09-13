package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class MeleeAttack extends Attack {
	
	public MeleeAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
		
		hitFixtureProperties = new PhysicsBodyProperties().setBody(body).setCollisionType(collisionType).setSensor(true)
				.setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(config.hitFixtureRadius).setFixturePosition(getFixturePosition(direction));
	}
	
	private Vector2 getFixturePosition(Vector2 direction) {
		return direction.nor().scl(config.distFromCenter);
	}
	
	@Override
	public void start() {
		hitFixture = PhysicsBodyCreator.addFixture(hitFixtureProperties);
		started = true;
	}
	
	@Override
	public void remove() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
		}
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(collisionType, fixtureA, fixtureB)) {
			Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
			Object attackedObjectUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackedObjectUserData != null && attackUserData == hitFixtureProperties.body.getUserData()
					&& attackedObjectUserData instanceof Hittable) {
				Hittable attackedObject = ((Hittable) attackedObjectUserData);
				attackedObject.takeDamage(config.damage);
				attackedObject.pushByHit(hitFixture.getBody().getPosition(), config.pushForce);
			}
		}
	}
}
