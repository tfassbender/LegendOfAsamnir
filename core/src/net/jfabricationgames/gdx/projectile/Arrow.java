package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Arrow extends Projectile {
	
	private static final int ANGLE_OFFSET_SPRITE_VECTOR = 90;
	
	protected Arrow(ProjectileTypeConfig typeConfig, Sprite sprite) {
		super(typeConfig, sprite);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.RECTANGLE).setWidth(0.75f).setHeight(0.25f);
	}
	
	@Override
	protected void prepareProjectile(Vector2 direction) {
		float angle = direction.angle();
		body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * angle);
		sprite.setRotation(angle - typeConfig.textureInitialRotation + ANGLE_OFFSET_SPRITE_VECTOR);
	}
}
