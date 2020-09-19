package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Bomb extends Projectile {
	
	public Bomb(ProjectileTypeConfig typeConfig, Sprite sprite) {
		super(typeConfig, sprite);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.2f);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		turnBombColorRed();
		super.draw(batch);
	}
	
	private void turnBombColorRed() {
		float timeTillExplosion = getTimeTillExplosion();
		if (timeTillExplosion <= 1) {
			float red = (1f - 0.5f * timeTillExplosion);
			sprite.setColor(new Color(red, 0f, 0f, 1f));
		}
	}
	
	private float getTimeTillExplosion() {
		return typeConfig.timeTillExplosion - timeActive;
	}
}
