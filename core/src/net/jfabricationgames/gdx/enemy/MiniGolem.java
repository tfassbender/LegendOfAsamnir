package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class MiniGolem extends Enemy {
	
	public MiniGolem(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return getDefaultPhysicsBodyProperties().setRadius(0.3f).setDensity(10f).setLinearDamping(10f).setPhysicsBodyShape(PhysicsBodyShape.CIRCLE);
	}
}
