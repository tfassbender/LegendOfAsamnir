package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Bat extends Enemy {
	
	public Bat(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createPhysicsBody(World world, float x, float y) {
		PhysicsBodyProperties properties = getDefaultPhysicsBodyProperties().setX(x).setY(y).setRadius(0.3f).setDensity(10f).setLinearDamping(10f)
				.setPhysicsBodyShape(PhysicsBodyShape.CIRCLE);
		body = PhysicsBodyCreator.createBody(world, properties);
		body.setUserData(this);
	}
}
