package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapGroundType;
import net.jfabricationgames.gdx.map.GameMapObject;
import net.jfabricationgames.gdx.physics.BeforeWorldStep;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public abstract class AbstractCharacter implements GameMapObject, ContactListener, CutsceneControlledUnit, CutsceneMoveableUnit {
	
	public static final String MAP_PROPERTIES_KEY_PREDEFINED_MOVEMENT_POSITIONS = "predefinedMovementPositions";
	
	protected static final AssetGroupManager assetManager = AssetGroupManager.getInstance();
	
	protected CharacterStateMachine stateMachine;
	protected CharacterState movingState;
	protected ArtificialIntelligence ai;
	protected CutsceneHandler cutsceneHandler;
	
	protected MapProperties properties;
	protected Body body;
	
	protected Vector2 intendedMovement;
	protected float movingSpeed;
	protected float imageOffsetX;
	protected float imageOffsetY;
	
	protected GameMapGroundType groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	
	public AbstractCharacter(MapProperties properties) {
		this.properties = properties;
		
		PhysicsWorld.getInstance().registerContactListener(this);
		cutsceneHandler = CutsceneHandler.getInstance();
	}
	
	protected void setImageOffset(float x, float y) {
		this.imageOffsetX = x;
		this.imageOffsetY = y;
	}
	
	/**
	 * Called from the factory to create a box2d physics body for this character.
	 */
	public void createPhysicsBody(float x, float y) {
		PhysicsBodyProperties properties = definePhysicsBodyProperties();
		properties.setX(x).setY(y);
		body = PhysicsBodyCreator.createBody(properties);
		addAdditionalPhysicsParts();
		body.setUserData(this);
	}
	
	protected abstract PhysicsBodyProperties definePhysicsBodyProperties();
	
	protected abstract void addAdditionalPhysicsParts();
	
	public abstract void act(float delta);
	
	public void draw(float delta, SpriteBatch batch) {
		AnimationDirector<TextureRegion> animation = getAnimation();
		if (animation != null) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			animation.getSpriteConfig().setX((body.getPosition().x - region.getRegionWidth() * 0.5f + imageOffsetX))
					.setY((body.getPosition().y - region.getRegionHeight() * 0.5f + imageOffsetY));
			updateTextureDirection(region);
			animation.draw(batch);
		}
	}
	
	protected abstract void updateTextureDirection(TextureRegion region);
	
	protected AnimationDirector<TextureRegion> getAnimation() {
		return stateMachine.getCurrentState().getAnimation();
	}
	
	public CharacterStateMachine getStateMachine() {
		return stateMachine;
	}
	
	@Override
	public String getUnitId() {
		return properties.get(CutsceneControlledUnit.MAP_PROPERTIES_KEY_UNIT_ID, String.class);
	}
	
	@Override
	public void changeToMovingState() {
		if (!stateMachine.getCurrentState().equals(movingState)) {
			stateMachine.setState(movingState);
		}
	}
	
	@Override
	public Vector2 getPosition() {
		return new Vector2(body.getPosition());
	}
	
	public void moveTo(Vector2 pos) {
		moveTo(pos, 1f);
	}
	
	@Override
	public void moveTo(Vector2 pos, float speedFactor) {
		Vector2 direction = pos.cpy().sub(getPosition());
		direction.nor().scl(movingSpeed * speedFactor);
		
		move(direction);
	}
	
	public void move(Vector2 delta) {
		intendedMovement = delta;
		float force = 10f * groundProperties.movementSpeedFactor * body.getMass();
		body.applyForceToCenter(delta.x * force, delta.y * force, true);
	}
	
	public void moveToDirection(Vector2 pos) {
		Vector2 direction = pos.cpy().nor().scl(movingSpeed);
		move(direction);
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
		GameMapGroundType updatedGroundProperties = GameMapGroundType.handleGameMapGroundContact(contact, PhysicsCollisionType.ENEMY,
				groundProperties);
		if (updatedGroundProperties != null) {
			groundProperties = updatedGroundProperties;
		}
		else {
			ai.preSolve(contact, oldManifold);
		}
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		ai.postSolve(contact, impulse);
	}
	
	@BeforeWorldStep
	public void resetGroundProperties() {
		groundProperties = GameMap.DEFAULT_GROUND_PROPERTIES;
	}
}
