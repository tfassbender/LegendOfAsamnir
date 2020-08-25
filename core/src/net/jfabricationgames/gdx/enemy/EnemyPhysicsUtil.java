package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public abstract class EnemyPhysicsUtil {
	
	public static void addSensor(Body body, float radius) {
		PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties().setBody(body).setSensor(true).setRadius(radius)
				.setCollisionType(PhysicsCollisionType.ENEMY_SENSOR);
		PhysicsBodyCreator.addCircularFixture(sensorProperties);
	}
}
