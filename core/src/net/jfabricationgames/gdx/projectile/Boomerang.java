package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Boomerang extends Projectile {
	
	private static final float ROTATION_PER_SECOND = 540f;
	private static final float RETURN_SPEED = 3f;
	
	private float rotation;
	
	private boolean moveBackToPlayer = false;
	
	public Boomerang(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, sprite, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.25f).setDensity(5f);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		rotation += ROTATION_PER_SECOND * delta;
		sprite.setRotation(rotation);
		super.draw(delta, batch);
	}
	
	@Override
	public void update(float delta) {
		if (!moveBackToPlayer) {
			super.update(delta);
		}
		else {
			moveToPlayer();
			if (reachedPlayer()) {
				super.removeFromMap();
			}
		}
	}
	
	private void moveToPlayer() {
		Vector2 direction = playerBody.getPosition().cpy().sub(body.getPosition());
		direction.nor();
		float force = 10f * RETURN_SPEED * body.getMass();
		
		body.applyForceToCenter(direction.x * force, direction.y * force, true);
	}
	
	private boolean reachedPlayer() {
		return playerBody.getPosition().cpy().sub(body.getPosition()).len() < 0.5f;
	}
	
	@Override
	public void removeFromMap() {
		moveBackToPlayer = true;
		changeBodyToSensor();
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		if (hasBody() && !moveBackToPlayer) {
			body.setLinearDamping(typeConfig.dampingAfterObjectHit);
		}
	}
	
	@Override
	protected void stopProjectileAfterRangeExceeds() {
		if (hasBody() && !moveBackToPlayer) {
			body.setLinearDamping(typeConfig.dampingAfterRangeExceeded);
		}
	}
	
	@Override
	protected void setBodyLinearDamping() {
		if (moveBackToPlayer) {
			return;
		}
		
		super.setBodyLinearDamping();
	}
}
