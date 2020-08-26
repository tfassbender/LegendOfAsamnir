package net.jfabricationgames.gdx.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.attributes.Hittable;
import net.jfabricationgames.gdx.enemy.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.GameScreen;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public abstract class Enemy implements Hittable, ContactListener {
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("enemy");
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected EnemyTypeConfig typeConfig;
	protected AnimationManager animationManager;
	protected AnimationDirector<TextureRegion> animation;
	protected ArtificialIntelligence ai;
	
	protected MapProperties properties;
	protected GameMap gameMap;
	protected Body body;
	
	private PhysicsBodyProperties physicsBodyProperties;
	
	protected float health;
	protected float movingSpeed;
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		this.typeConfig = typeConfig;
		this.properties = properties;
		animationManager = AnimationManager.getInstance();
		physicsBodyProperties = new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false)
				.setCollisionType(PhysicsCollisionType.ENEMY);
		PhysicsWorld.getInstance().registerContactListener(this);
		
		readTypeConfig();
		createAI();
		ai.setEnemy(this);
		
		animation = getAnimation(EnemyState.IDLE);
	}
	
	protected void readTypeConfig() {
		health = typeConfig.health;
		movingSpeed = typeConfig.movingSpeed;
	}
	
	/**
	 * Create the {@link ArtificialIntelligence} that controls this enemy.
	 */
	protected abstract void createAI();
	
	/**
	 * Called from the factory to create a box2d physics body for this enemy.
	 */
	public void createPhysicsBody(World world, float x, float y) {
		PhysicsBodyProperties properties = definePhysicsBodyProperties();
		properties.setX(x).setY(y);
		body = PhysicsBodyCreator.createBody(world, properties);
		addAdditionalPhysicsParts();
		body.setUserData(this);
	}
	
	protected abstract PhysicsBodyProperties definePhysicsBodyProperties();
	
	protected abstract void addAdditionalPhysicsParts();
	
	protected PhysicsBodyProperties getDefaultPhysicsBodyProperties() {
		return physicsBodyProperties.clone();
	}
	
	public void act(float delta) {
		if (health < 0) {
			if (animation == null || animation.isAnimationFinished()) {
				remove();
			}
		}
		if (animation == null || animation.isAnimationFinished()) {
			animation = getAnimation(EnemyState.IDLE);
		}
		
		ai.calculateMove(delta);
		ai.executeMove();
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			float x = body.getPosition().x - region.getRegionWidth() * 0.5f;
			float y = body.getPosition().y - region.getRegionHeight() * 0.5f;
			batch.draw(region, x, y, region.getRegionWidth() * 0.5f, region.getRegionHeight() * 0.5f, region.getRegionWidth(),
					region.getRegionHeight(), GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
		}
	}
	
	public void moveTo(float x, float y) {
		moveTo(new Vector2(x, y));
	}
	public void moveTo(Vector2 pos) {
		Vector2 direction = pos.sub(getPosition()).nor().scl(movingSpeed);
		move(direction);
	}
	
	public void moveToDirection(float x, float y) {
		moveToDirection(new Vector2(x, y));
	}
	public void moveToDirection(Vector2 pos) {
		Vector2 direction = pos.nor().scl(movingSpeed);
		move(direction);
	}
	
	private void move(Vector2 delta) {
		float force = 10f * body.getMass();
		body.applyForceToCenter(delta.x * force, delta.y * force, true);
	}
	
	public Vector2 getPosition() {
		return new Vector2(body.getPosition());
	}
	
	/**
	 * Get the animation for the given state, that is defined in the animation config (see config/enemy/types).
	 */
	protected AnimationDirector<TextureRegion> getAnimation(EnemyState state) {
		return getAnimation(state.name().toLowerCase());
	}
	protected AnimationDirector<TextureRegion> getAnimation(String animation) {
		if (animation != null) {
			return animationManager.getAnimationDirector(typeConfig.animations.get(animation));
		}
		return null;
	}
	
	/**
	 * Play the sound for the given state, that is defined in the sound config (see config/enemy/types).
	 */
	protected void playSound(EnemyState state) {
		playSound(typeConfig.sounds.get(state.name().toLowerCase()));
	}
	protected void playSound(String sound) {
		if (sound != null) {
			soundSet.playSound(sound);
		}
	}
	
	@Override
	public void takeDamage(float damage) {
		health -= damage;
		
		if (health <= 0) {
			die();
		}
		else {
			animation = getHitAnimation(damage);
			playSound(EnemyState.DAMAGE);
		}
	}
	
	protected void die() {
		animation = getAnimation(EnemyState.DIE);
		playSound(EnemyState.DIE);
	}
	
	/**
	 * Hit animations are special because they can be based on the damage an enemy takes.<br>
	 * The default implementation will just return the EnemyState.DAMAGE animation.
	 */
	protected AnimationDirector<TextureRegion> getHitAnimation(float damage) {
		return getAnimation(EnemyState.DAMAGE);
	}
	
	public void remove() {
		gameMap.removeEnemy(this);
		removePhysicsBody();
	}
	
	public void removePhysicsBody() {
		PhysicsWorld.getInstance().destroyBodyAfterWorldStep(body);
		PhysicsWorld.getInstance().removeContactListener(this);
	}
	
	@Override
	public void beginContact(Contact contact) {
		ai.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		ai.endContact(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		ai.preSolve(contact, oldManifold);
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		ai.postSolve(contact, impulse);
	}
}
