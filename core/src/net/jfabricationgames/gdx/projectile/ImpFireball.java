package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class ImpFireball extends Projectile {
	
	public ImpFireball(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation) {
		super(typeConfig, animation);
		setImageOffset(0f, 0f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void stopProjectile() {
		super.stopProjectile();
		changeBodyToSensor();
	}
}
