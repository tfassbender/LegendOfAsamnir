package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Explosion extends Projectile {
	
	public Explosion(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation) {
		super(typeConfig, animation);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(2f).setSensor(true);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		removeExplosionSensor();
	}
	
	private void removeExplosionSensor() {
		if (hasBody()) {
			for (Fixture fixture : body.getFixtureList()) {
				if (fixture.isSensor()) {
					body.destroyFixture(fixture);
				}
			}
		}
	}
}
