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
	
	public static Body createOctagonBody(World world, PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties.type, properties.x, properties.y);
		Body body = world.createBody(bodyDef);
		
		addOctagonFixture(properties);
		
		return body;
	}
	
	public static void addOctagonFixture(PhysicsBodyProperties properties) {
		PolygonShape shape = new PolygonShape();
		shape.set(new Vector2[] {new Vector2(-0.5f * properties.width, -0.4f * properties.height),
				new Vector2(-0.5f * properties.width, 0.4f * properties.height), new Vector2(-0.4f * properties.width, 0.5f * properties.height),
				new Vector2(0.4f * properties.width, 0.5f * properties.height), new Vector2(0.5f * properties.width, 0.4f * properties.height),
				new Vector2(0.5f * properties.width, -0.4f * properties.height), new Vector2(0.4f * properties.width, -0.5f * properties.height),
				new Vector2(-0.4f * properties.width, -0.5f * properties.height)});
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties.density, properties.restitution, properties.friction, properties.collisionType);
		properties.body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public static Body createCircularBody(World world, PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties.type, properties.x, properties.y);
		Body body = world.createBody(bodyDef);
		
		addCircularFixture(properties);
		
		return body;
	}
	
	public static void addCircularFixture(PhysicsBodyProperties properties) {
		CircleShape shape = new CircleShape();
		shape.setRadius(properties.radius);
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties.density, properties.restitution, properties.friction, properties.collisionType);
		fixtureDef.isSensor = properties.sensor;
		properties.body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public static Body createRectangularBody(World world, PhysicsBodyProperties properties) {
		//TODO
		return null;
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
	
	public static class PhysicsBodyProperties {
		
		public BodyType type;
		public Body body;
		
		public float x;
		public float y;
		
		public float density;
		public float restitution;
		public float friction;
		public boolean sensor;
		
		public float width;
		public float height;
		public float radius;
		
		public PhysicsCollisionType collisionType;
		
		public PhysicsBodyProperties setType(BodyType type) {
			this.type = type;
			return this;
		}
		
		public PhysicsBodyProperties setBody(Body body) {
			this.body = body;
			return this;
		}
		
		public PhysicsBodyProperties setX(float x) {
			this.x = x;
			return this;
		}
		
		public PhysicsBodyProperties setY(float y) {
			this.y = y;
			return this;
		}
		
		public PhysicsBodyProperties setDensity(float density) {
			this.density = density;
			return this;
		}
		
		public PhysicsBodyProperties setRestitution(float restitution) {
			this.restitution = restitution;
			return this;
		}
		
		public PhysicsBodyProperties setFriction(float friction) {
			this.friction = friction;
			return this;
		}
		
		public PhysicsBodyProperties setSensor(boolean sensor) {
			this.sensor = sensor;
			return this;
		}
		
		public PhysicsBodyProperties setWidth(float width) {
			this.width = width;
			return this;
		}
		
		public PhysicsBodyProperties setHeight(float height) {
			this.height = height;
			return this;
		}
		
		public PhysicsBodyProperties setRadius(float radius) {
			this.radius = radius;
			return this;
		}
		
		public PhysicsBodyProperties setCollisionType(PhysicsCollisionType collisionType) {
			this.collisionType = collisionType;
			return this;
		}
	}
}
