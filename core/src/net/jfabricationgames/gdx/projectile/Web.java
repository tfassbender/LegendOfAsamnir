package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.enemy.EnemyPhysicsUtil;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class Web extends Projectile {
	
	private static int websTouchingPlayer = 0;
	
	private boolean touchingPlayer;
	
	public Web(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation) {
		super(typeConfig, animation);
		setImageOffset(0f, 0.5f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		EnemyPhysicsUtil.addSensor(body, 1f);
	}
	
	@Override
	protected void stopProjectile() {
		super.stopProjectile();
		changeBodyToSensor();
	}
	
	private void changeBodyToSensor() {
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setSensor(true);
		}
	}
	
	@Override
	public void remove() {
		if (!touchingPlayer) {
			super.remove();
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		
		PlayableCharacter character = getPlayerFromContact(contact);
		if (character != null) {
			character.setSlowedDown(true);
			touchingPlayer = true;
			websTouchingPlayer++;
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		super.endContact(contact);
		
		PlayableCharacter character = getPlayerFromContact(contact);
		if (character != null) {
			websTouchingPlayer--;
			touchingPlayer = false;
			if (websTouchingPlayer == 0) {
				character.setSlowedDown(false);
			}
			else if (websTouchingPlayer < 0) {
				throw new IllegalStateException("The field 'websTouchingPlayer' reached a negative value. This state should not be possible.");
			}
		}
		
	}
	
	private PlayableCharacter getPlayerFromContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object attackUserData = CollisionUtil.getCollisionTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
		if (attackUserData == this) {
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.ENEMY_SENSOR, fixtureA, fixtureB);
			
			if (attackedUserData instanceof PlayableCharacter) {
				PlayableCharacter character = (PlayableCharacter) attackedUserData;
				return character;
			}
		}
		return null;
	}
}
