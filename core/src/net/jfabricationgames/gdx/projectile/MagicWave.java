package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class MagicWave extends Projectile {
	
	public MagicWave(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, sprite, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.9f).setSensor(true);
	}
}
