package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class Chest extends GameObject {
	
	public Chest(ObjectTypeConfig type, Sprite sprite, MapProperties properties) {
		super(type, sprite, properties);
		hitSound = "wood_knock";
		physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f)
				.setRestitution(0).setCollisionType(PhysicsCollisionType.OBSTACLE);
		physicsBodySizeFactor = new Vector2(0.4f, 0.3f);
		physicsBodyOffsetFactor = new Vector2(0.01f, -0.025f);
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getHitAnimation() {
		return animationManager.getAnimationDirector("chest_hit");
	}
}
