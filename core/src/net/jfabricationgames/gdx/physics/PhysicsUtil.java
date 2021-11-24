package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class PhysicsUtil {
	
	private PhysicsUtil() {}
	
	public static void addNpcSensor(Body body, float radius) {
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(radius)
				.setCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
	}
	
	public static void addEnemySensor(Body body, float radius) {
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(radius)
				.setCollisionType(PhysicsCollisionType.ENEMY_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
	}
	
	public static void addCircularGroundFixture(Body body, float radius, String groundType) {
		PhysicsBodyProperties properties = new PhysicsBodyProperties().setBody(body).setRadius(radius)
				.setCollisionType(PhysicsCollisionType.MAP_GROUND);
		PhysicsBodyCreator.addCircularFixture(properties);
	}
}
