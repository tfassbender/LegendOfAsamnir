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
		BodyDef bodyDef = createBodyDef(properties);
		Body body = world.createBody(bodyDef);
		
		properties.setBody(body);
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
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		properties.body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public static Body createCircularBody(World world, PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = world.createBody(bodyDef);
		
		properties.setBody(body);
		addCircularFixture(properties);
		
		return body;
	}
	public static void addCircularFixture(PhysicsBodyProperties properties) {
		CircleShape shape = new CircleShape();
		shape.setRadius(properties.radius);
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		properties.body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public static Body createRectangularBody(World world, PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = world.createBody(bodyDef);
		
		properties.setBody(body);
		addRectangularBody(properties);
		
		return body;
	}
	public static void addRectangularBody(PhysicsBodyProperties properties) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(properties.width * 0.5f, properties.height * 0.5f);
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		properties.body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	private static BodyDef createBodyDef(PhysicsBodyProperties properties) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = properties.type;
		bodyDef.position.set(properties.x, properties.y);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		
		return bodyDef;
	}
	
	private static FixtureDef createFixtureDef(Shape shape, PhysicsBodyProperties properties) {
		FixtureDef fixtureDef;
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = properties.density;
		fixtureDef.friction = properties.friction;
		fixtureDef.restitution = properties.restitution;
		fixtureDef.filter.categoryBits = properties.collisionType.category;
		fixtureDef.filter.maskBits = properties.collisionType.mask;
		fixtureDef.isSensor = properties.sensor;
		
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
