package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Wand extends Projectile {
	
	public Wand(ProjectileTypeConfig typeConfig, Sprite sprite) {
		super(typeConfig, sprite);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.9f).setSensor(true);
	}
	
	@Override
	protected boolean isAttackOver() {
		return false;
	}
	
	@Override
	protected boolean hasDamage() {
		return true;
	}
}
