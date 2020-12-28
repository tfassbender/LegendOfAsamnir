package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class PhysicsBodyCreator {
	
	public static Body createBody(BodyDef bodyDef) {
		Body body = PhysicsWorld.getInstance().getWorld().createBody(bodyDef);
		return body;
	}
	
	public static Body createBody(PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = PhysicsWorld.getInstance().getWorld().createBody(bodyDef);
		
		properties.setBody(body);
		addFixture(properties);
		
		return body;
	}
	public static Fixture addFixture(PhysicsBodyProperties properties) {
		switch (properties.physicsBodyShape) {
			case CIRCLE:
				return addCircularFixture(properties);
			case OCTAGON:
				return addOctagonFixture(properties);
			case RECTANGLE:
				return addRectangularFixture(properties);
			default:
				throw new IllegalStateException("Unknown PhysicsBodyShape type: " + properties.physicsBodyShape);
		}
	}
	
	public static Body createOctagonBody(PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = PhysicsWorld.getInstance().getWorld().createBody(bodyDef);
		
		properties.setBody(body);
		addOctagonFixture(properties);
		
		return body;
	}
	public static Fixture addOctagonFixture(PhysicsBodyProperties properties) {
		PolygonShape shape = new PolygonShape();
		shape.set(new Vector2[] {new Vector2(-0.5f * properties.width, -0.4f * properties.height),
				new Vector2(-0.5f * properties.width, 0.4f * properties.height), new Vector2(-0.4f * properties.width, 0.5f * properties.height),
				new Vector2(0.4f * properties.width, 0.5f * properties.height), new Vector2(0.5f * properties.width, 0.4f * properties.height),
				new Vector2(0.5f * properties.width, -0.4f * properties.height), new Vector2(0.4f * properties.width, -0.5f * properties.height),
				new Vector2(-0.4f * properties.width, -0.5f * properties.height)});
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		Fixture fixture = properties.body.createFixture(fixtureDef);
		shape.dispose();
		
		return fixture;
	}
	
	public static Body createCircularBody(PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = PhysicsWorld.getInstance().getWorld().createBody(bodyDef);
		
		properties.setBody(body);
		addCircularFixture(properties);
		
		return body;
	}
	public static Fixture addCircularFixture(PhysicsBodyProperties properties) {
		CircleShape shape = new CircleShape();
		shape.setRadius(properties.radius);
		shape.setPosition(properties.fixturePosition);
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		Fixture fixture = properties.body.createFixture(fixtureDef);
		shape.dispose();
		
		return fixture;
	}
	
	public static Body createRectangularBody(PhysicsBodyProperties properties) {
		BodyDef bodyDef = createBodyDef(properties);
		Body body = PhysicsWorld.getInstance().getWorld().createBody(bodyDef);
		
		properties.setBody(body);
		addRectangularFixture(properties);
		
		return body;
	}
	public static Fixture addRectangularFixture(PhysicsBodyProperties properties) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(properties.width * 0.5f, properties.height * 0.5f);
		
		FixtureDef fixtureDef = createFixtureDef(shape, properties);
		Fixture fixture = properties.body.createFixture(fixtureDef);
		shape.dispose();
		
		return fixture;
	}
	
	private static BodyDef createBodyDef(PhysicsBodyProperties properties) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = properties.type;
		bodyDef.position.set(properties.x, properties.y);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		bodyDef.linearDamping = properties.linearDamping;
		
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
	
	public static class PhysicsBodyProperties implements Cloneable {
		
		public PhysicsBodyShape physicsBodyShape;
		public BodyType type;
		public Body body;
		
		public float x;
		public float y;
		
		public float density;
		public float restitution;
		public float friction;
		public boolean sensor;
		public float linearDamping;
		
		public float width;
		public float height;
		public float radius;
		
		public Vector2 fixturePosition = new Vector2(0f, 0f);
		
		public PhysicsCollisionType collisionType;
		
		public PhysicsBodyProperties clone() {
			try {
				return (PhysicsBodyProperties) super.clone();
			}
			catch (CloneNotSupportedException e) {
				Gdx.app.error(getClass().getSimpleName(), "Couldn't create a clone of PhysicsBodyProperties: " + e.getMessage());
				return null;
			}
		}
		
		public PhysicsBodyProperties setPhysicsBodyShape(PhysicsBodyShape physicsBodyShape) {
			this.physicsBodyShape = physicsBodyShape;
			return this;
		}
		
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
		
		public PhysicsBodyProperties setLinearDamping(float linearDamping) {
			this.linearDamping = linearDamping;
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
		
		public PhysicsBodyProperties setFixturePosition(Vector2 fixturePosition) {
			this.fixturePosition = fixturePosition;
			return this;
		}
		
		public PhysicsBodyProperties setCollisionType(PhysicsCollisionType collisionType) {
			this.collisionType = collisionType;
			return this;
		}
	}
	
	public static enum PhysicsBodyShape {
		CIRCLE, //
		RECTANGLE, //
		OCTAGON, //
	}
}
