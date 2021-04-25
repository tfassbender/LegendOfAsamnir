package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapObject;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public abstract class Projectile implements ContactListener, GameMapObject {
	
	private static final String SOUND_SET_PROJECTILE = "projectile";
	private static final String EXPLOSION_PROJECTILE_TYPE = "explosion";
	
	protected Body body;
	protected ProjectileTypeConfig typeConfig;
	protected PhysicsCollisionType collisionType;
	
	protected float distanceTraveled;
	protected float timeActive;
	protected boolean attackPerformed;
	
	protected float damage;
	protected float pushForce;
	protected boolean pushForceAffectedByBlock = true;
	
	protected float explosionDamage;
	protected float explosionPushForce;
	protected boolean explosionPushForceAffectedByBlock;
	
	private float imageOffsetX;
	private float imageOffsetY;
	
	private float animationRotation;
	
	protected AnimationDirector<TextureRegion> animation;
	protected Sprite sprite;
	
	public Projectile(ProjectileTypeConfig typeConfig, Sprite sprite) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		
		initialize();
	}
	
	public Projectile(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation) {
		this.typeConfig = typeConfig;
		this.animation = animation;
		
		sprite = new Sprite(animation.getKeyFrame());
		
		initialize();
	}
	
	private void initialize() {
		distanceTraveled = 0;
		if (typeConfig.damping > 0 && isRangeRestricted()) {
			throw new IllegalStateException("A Projectile can not be restricted to be removed after a given range AND use linear damping");
		}
		attackPerformed = false;
		
		if (typeConfig.sound != null) {
			SoundSet soundSet = SoundManager.getInstance().loadSoundSet(SOUND_SET_PROJECTILE);
			soundSet.playSound(typeConfig.sound);
		}
		
		registerAsContactListener();
	}
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
	}
	
	protected void createPhysicsBody(Vector2 position, PhysicsCollisionType collisionType) {
		this.collisionType = collisionType;
		PhysicsBodyProperties bodyProperties = createShapePhysicsBodyProperties().setType(BodyType.DynamicBody).setX(position.x).setY(position.y)
				.setCollisionType(collisionType).setLinearDamping(typeConfig.damping);
		body = PhysicsBodyCreator.createBody(bodyProperties);
		body.setUserData(this);
		addAdditionalPhysicsParts();
	}
	
	protected void addAdditionalPhysicsParts() {}
	
	protected abstract PhysicsBodyProperties createShapePhysicsBodyProperties();
	
	protected void startProjectile(Vector2 direction) {
		Vector2 movement = direction.nor().scl(typeConfig.speed);
		
		if (typeConfig.rotateTextureToMovementDirection) {
			rotateProjectile(direction);
		}
		
		//apply the movement force only once because the linear damping is set to zero
		body.applyForceToCenter(movement.scl(body.getMass() * 10), true);
	}
	
	protected void rotateProjectile(Vector2 direction) {
		float angle = direction.angleDeg();
		float rotation = angle - typeConfig.textureInitialRotation + getSpriteVectorAngleOffset();
		body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * angle);
		sprite.setRotation(rotation);
		animationRotation = rotation;
	}
	
	protected float getSpriteVectorAngleOffset() {
		return 0;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public void setPushForce(float pushForce) {
		this.pushForce = pushForce;
	}
	
	public void setPushForceAffectedByBlock(boolean pushForceAffectedByBlock) {
		this.pushForceAffectedByBlock = pushForceAffectedByBlock;
	}
	
	public void update(float delta) {
		distanceTraveled += delta * typeConfig.speed;
		timeActive += delta;
		
		if (animation != null) {
			animation.increaseStateTime(delta);
			sprite = new Sprite(animation.getKeyFrame());
			sprite.setRotation(animationRotation);
			
			if (animation.isAnimationFinished() && typeConfig.removeAfterAnimationFinished) {
				remove();
			}
		}
		
		if (reachedMaxRange()) {
			if (isRangeRestricted()) {
				remove();
			}
			else if (isDampedAfterRangeExceeds()) {
				stopProjectileAfterRangeExceeds();
			}
		}
		if (activeTimeOver()) {
			remove();
		}
		if (explosionTimeReached()) {
			explode();
		}
	}
	
	private boolean reachedMaxRange() {
		return distanceTraveled > typeConfig.range;
	}
	
	private boolean isRangeRestricted() {
		return typeConfig.range > 0 && typeConfig.removeAfterRangeExceeded;
	}
	
	private boolean isDampedAfterRangeExceeds() {
		return typeConfig.range > 0 && typeConfig.dampingAfterRangeExceeded > 0;
	}
	
	private boolean activeTimeOver() {
		return timeActive > typeConfig.timeActive && isTimeRestricted();
	}
	
	private boolean isTimeRestricted() {
		return typeConfig.timeActive >= 0;
	}
	
	private boolean explosionTimeReached() {
		return timeActive > typeConfig.timeTillExplosion && isExplosive();
	}
	
	private boolean isExplosive() {
		return typeConfig.timeTillExplosion > 0;
	}
	
	protected void stopProjectileAfterObjectHit() {
		if (hasBody()) {
			body.setLinearDamping(typeConfig.dampingAfterObjectHit);
			attackPerformed = true;
		}
	}
	
	private void stopProjectileAfterRangeExceeds() {
		if (hasBody()) {
			body.setLinearDamping(typeConfig.dampingAfterRangeExceeded);
			attackPerformed = true;
		}
	}
	
	protected void changeBodyToSensor() {
		if (hasBody()) {
			for (Fixture fixture : body.getFixtureList()) {
				fixture.setSensor(true);
			}
		}
	}
	
	protected boolean hasBody() {
		return body != null;
	}
	
	private void explode() {
		Projectile explosion = ProjectileFactory.getInstance().createProjectileAndAddToMap(EXPLOSION_PROJECTILE_TYPE, body.getPosition(),
				Vector2.Zero, collisionType);
		explosion.setDamage(explosionDamage);
		explosion.setPushForce(explosionPushForce);
		explosion.setPushForceAffectedByBlock(explosionPushForceAffectedByBlock);
		remove();
	}
	
	public void draw(SpriteBatch batch) {
		sprite.setScale(sprite.getScaleX() * typeConfig.textureScale, sprite.getScaleY() * typeConfig.textureScale);
		sprite.setPosition(body.getPosition().x - sprite.getOriginX() + imageOffsetX, body.getPosition().y - sprite.getOriginY() + imageOffsetY);
		sprite.draw(batch);
	}
	
	protected void setImageOffset(float x, float y) {
		this.imageOffsetX = x;
		this.imageOffsetY = y;
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (isAttackOver() || damage == 0) {
			return;
		}
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
		if (attackUserData == this) {
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (attackedUserData instanceof Hittable) {
				Hittable hittable = ((Hittable) attackedUserData);
				//enemies define the force themselves; the force parameter is a factor for this self defined force
				hittable.pushByHit(body.getPosition().cpy(), pushForce, pushForceAffectedByBlock);
				hittable.takeDamage(damage, typeConfig.attackType);
			}
			body.setLinearDamping(typeConfig.dampingAfterObjectHit);
			attackPerformed = true;
		}
	}
	
	private boolean isAttackOver() {
		return attackPerformed && !typeConfig.multipleHitsPossible;
	}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void removeFromMap() {
		remove();
	}
	
	public void remove() {
		attackPerformed = true;
		GameMap.getInstance().removeProjectile(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
	
	public void setExplosionDamage(float explosionDamage) {
		this.explosionDamage = explosionDamage;
	}
	
	public void setExplosionPushForce(float explosionPushForce) {
		this.explosionPushForce = explosionPushForce;
	}
	
	public void setExplosionPushForceAffectedByBlock(boolean explosionPushForceAffectedByBlock) {
		this.explosionPushForceAffectedByBlock = explosionPushForceAffectedByBlock;
	}
}
