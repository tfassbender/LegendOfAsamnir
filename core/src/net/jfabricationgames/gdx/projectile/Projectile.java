package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public abstract class Projectile implements ContactListener {
	
	private static final int ANGLE_OFFSET_SPRITE_VECTOR = 90;
	
	protected GameMap gameMap;
	protected Body body;
	protected ProjectileTypeConfig typeConfig;
	
	protected float distanceTraveled;
	protected boolean attackPerformed;
	
	protected float damage;
	protected float pushForce;
	
	protected Sprite sprite;
	
	public Projectile(ProjectileTypeConfig typeConfig, Sprite sprite) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		
		distanceTraveled = 0;
		attackPerformed = false;
		
		registerAsContactListener();
	}
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
	}
	
	protected void createPhysicsBody(World world, Vector2 position, PhysicsCollisionType collisionType) {
		PhysicsBodyProperties bodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false)
				.setPhysicsBodyShape(PhysicsBodyShape.RECTANGLE).setX(position.x).setY(position.y).setWidth(0.75f).setHeight(0.25f)
				.setCollisionType(collisionType).setLinearDamping(0f);
		body = PhysicsBodyCreator.createBody(world, bodyProperties);
		body.setUserData(this);
	}
	
	protected void startProjectile(Vector2 direction) {
		Vector2 movement = direction.nor().scl(typeConfig.speed);
		
		float angle = direction.angle();
		body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * angle);
		sprite.setRotation(angle - typeConfig.textureInitialRotation + ANGLE_OFFSET_SPRITE_VECTOR);
		sprite.setScale(sprite.getScaleX() * typeConfig.textureScale, sprite.getScaleY() * typeConfig.textureScale);
		
		//apply the movement force only once because the linear damping is set to zero
		body.applyForceToCenter(movement.scl(body.getMass() * 10), true);
	}
	
	protected void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public void setPushForce(float pushForce) {
		this.pushForce = pushForce;
	}
	
	public void update(float delta) {
		distanceTraveled += delta * typeConfig.speed;
		
		if (distanceTraveled > typeConfig.range) {
			remove();
		}
	}
	
	public void draw(SpriteBatch batch) {
		sprite.setPosition(body.getPosition().x - sprite.getOriginX(), body.getPosition().y - sprite.getOriginY());
		sprite.draw(batch);
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (attackPerformed) {
			//don't hit twice
			return;
		}
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object attackUserData = CollisionUtil.getCollisionTypeUserData(PhysicsCollisionType.PLAYER_ATTACK, fixtureA, fixtureB);
		if (attackUserData == this) {
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.PLAYER_ATTACK, fixtureA, fixtureB);
			
			if (attackedUserData instanceof Hittable) {
				Hittable hittable = ((Hittable) attackedUserData);
				hittable.takeDamage(damage);
				//enemies define the force themselves; the force parameter is a factor for this self defined force
				hittable.pushByHit(body.getPosition().cpy(), pushForce);
			}
			body.setLinearDamping(typeConfig.dampingAfterObjectHit);
			attackPerformed = true;
		}
	}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	public void remove() {
		attackPerformed = true;
		removePhysicsBody();
		gameMap.removeProjectile(this);
	}
	
	private void removePhysicsBody() {
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;
	}
}
