package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapGroundType;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.screens.game.GameScreen;

class CharacterBodyHandler {
	
	private static final float PHYSICS_BODY_SIZE_FACTOR_X = 0.6f;
	private static final float PHYSICS_BODY_SIZE_FACTOR_Y = 0.7f;
	private static final float PHYSICS_BODY_SENSOR_RADIUS = 0.6f;
	
	private Dwarf player;
	
	protected Body body;
	
	protected GameMapGroundType groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	
	public CharacterBodyHandler(Dwarf player) {
		this.player = player;
		
		createPhysicsBody();
	}
	
	public void createPhysicsBody() {
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody)
				.setWidth(player.renderer.idleDwarfSprite.getRegionWidth() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_X)
				.setHeight(player.renderer.idleDwarfSprite.getRegionHeight() * GameScreen.WORLD_TO_SCREEN * PHYSICS_BODY_SIZE_FACTOR_Y)
				.setCollisionType(PhysicsCollisionType.PLAYER).setLinearDamping(10f);
		body = PhysicsBodyCreator.createOctagonBody(bodyProperties);
		body.setSleepingAllowed(false);
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(PHYSICS_BODY_SENSOR_RADIUS)
				.setCollisionType(PhysicsCollisionType.PLAYER_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
		body.setUserData(player);
	}
	
	public void move(float deltaX, float deltaY) {
		float force = 10f * groundProperties.movementSpeedFactor * body.getMass();
		body.applyForceToCenter(deltaX * force, deltaY * force, true);
	}
	
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB)) {
			//collect stuff that touches the player sensor (usually items)
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_SENSOR, fixtureA, fixtureB);
			
			if (sensorCollidingUserData instanceof Item) {
				player.itemDataHandler.collectItem((Item) sensorCollidingUserData, player);
			}
		}
		
		player.attackHandler.handleAttackDamage(contact);
	}
	
	public void preSolve(Contact contact) {
		GameMapGroundType updatedGroundProperties = GameMapGroundType.handleGameMapGroundContact(contact, PhysicsCollisionType.PLAYER,
				groundProperties);
		if (updatedGroundProperties != null) {
			groundProperties = updatedGroundProperties;
		}
	}
	
	public void pushByHit(Vector2 hitCenter, float force, boolean blockAffected) {
		if (player.isAlive()) {
			Vector2 pushDirection = player.getPushDirection(body.getPosition(), hitCenter);
			force *= 10f * body.getMass();
			if (player.isBlocking() && blockAffected) {
				force *= 0.33;
			}
			
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
}
