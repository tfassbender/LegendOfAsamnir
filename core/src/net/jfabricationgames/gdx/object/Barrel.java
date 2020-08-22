package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class Barrel extends DestroyableObject {
	
	public Barrel(ObjectTypeConfig type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		health = 30f;
		hitSound = "wood_hit_2";
		destroySound = "wood_break";
		physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f)
				.setRestitution(0).setCollisionType(PhysicsCollisionType.OBSTACLE);
		physicsBodySizeFactor = new Vector2(0.3f, 0.5f);
		physicsBodyOffsetFactor = new Vector2(0.05f, -0.15f);
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("barrel_hit");
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		return animationManager.getAnimationDirector("barrel_break");
	}
}
