package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public abstract class PhysicsBodyCreator {
	
	public static Body createOctagonBody(World world, BodyType type, float x, float y, float density, float restitution, float friction, float width,
			float height, PhysicsCollisionType collisionType) {
		BodyDef bodyDef = createBodyDef(type, x, y);
		Body body = world.createBody(bodyDef);
		
		addOctagonFixture(body, density, restitution, friction, width, height, collisionType);
		
		return body;
	}
	
	public static void addOctagonFixture(Body body, float density, float restitution, float friction, float width, float height,
			PhysicsCollisionType collisionType) {
		PolygonShape shape = new PolygonShape();
		shape.set(new Vector2[] {new Vector2(-0.5f * width, -0.4f * height), new Vector2(-0.5f * width, 0.4f * height),
				new Vector2(-0.4f * width, 0.5f * height), new Vector2(0.4f * width, 0.5f * height), new Vector2(0.5f * width, 0.4f * height),
				new Vector2(0.5f * width, -0.4f * height), new Vector2(0.4f * width, -0.5f * height), new Vector2(-0.4f * width, -0.5f * height)});
		
		FixtureDef fixtureDef = createFixtureDef(shape, density, restitution, friction, collisionType);
		body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public static Body createCircularBody(World world, BodyType type, float x, float y, boolean sensor, float density, float restitution, float friction,
			float radius, PhysicsCollisionType collisionType) {
		BodyDef bodyDef = createBodyDef(type, x, y);
		Body body = world.createBody(bodyDef);
		
		addCircularFixture(body, sensor, density, restitution, friction, radius, collisionType);
		
		return body;
	}
	
	public static void addCircularFixture(Body body, boolean sensor, float density, float restitution, float friction, float radius,
			PhysicsCollisionType collisionType) {
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		
		FixtureDef fixtureDef = createFixtureDef(shape, density, restitution, friction, collisionType);
		fixtureDef.isSensor = sensor;
		body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	private static BodyDef createBodyDef(BodyType type, float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x, y);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		
		return bodyDef;
	}
	
	private static FixtureDef createFixtureDef(Shape shape, float density, float restitution, float friction, PhysicsCollisionType collisionType) {
		FixtureDef fixtureDef;
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		fixtureDef.filter.categoryBits = collisionType.category;
		fixtureDef.filter.maskBits = collisionType.mask;
		
		return fixtureDef;
	}
}
