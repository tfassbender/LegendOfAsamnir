package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class ImpFireball extends Projectile {
	
	public ImpFireball(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, animation, gameMap);
		setImageOffset(0f, 0f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		super.stopProjectileAfterObjectHit();
		changeBodyToSensor();
	}
}
