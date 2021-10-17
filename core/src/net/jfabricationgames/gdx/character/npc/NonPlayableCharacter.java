package net.jfabricationgames.gdx.character.npc;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.animation.TextureAnimationDirector;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterPhysicsUtil;
import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.interaction.InteractionManager;
import net.jfabricationgames.gdx.interaction.Interactive;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class NonPlayableCharacter extends AbstractCharacter implements Interactive {
	
	private NonPlayableCharacterTypeConfig typeConfig;
	
	private PlayableCharacter player;
	private TextureAnimationDirector<TextureRegion> interactionAnimation;
	
	public NonPlayableCharacter(NonPlayableCharacterTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		movingSpeed = typeConfig.movingSpeed;
		
		initializeStates();
		initializeMovingState();
		initializeIdleState();
		
		createAI();
		ai.setCharacter(this);
		initializeInteractionAnimation();
		
		setImageOffset(typeConfig.graphicsConfig.imageOffsetX, typeConfig.graphicsConfig.imageOffsetY);
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.graphicsConfig.stateConfig, typeConfig.graphicsConfig.initialState, null);
	}
	
	private void createAI() {
		ai = typeConfig.aiConfig.buildAI(stateMachine, properties);
	}
	
	private void initializeInteractionAnimation() {
		interactionAnimation = InteractionManager.getInstance().getInteractionAnimationCopy();
		interactionAnimation.setSpriteConfig(createSpriteConfig());
		//don't show the interaction animation on startup
		playInteractionAnimationDisappear();
		interactionAnimation.endAnimation();
	}
	
	private AnimationSpriteConfig createSpriteConfig() {
		AnimationDirector<TextureRegion> animation = getAnimation();
		if (animation != null) {
			AnimationSpriteConfig spriteConfig = animation.getSpriteConfigCopy();
			spriteConfig.x += (animation.getKeyFrame().getRegionWidth() * Constants.WORLD_TO_SCREEN
					* InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_X + typeConfig.graphicsConfig.interactionMarkerOffsetX);
			spriteConfig.y += (animation.getKeyFrame().getRegionHeight() * Constants.WORLD_TO_SCREEN
					* InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_Y + typeConfig.graphicsConfig.interactionMarkerOffsetY);
			
			return spriteConfig;
		}
		
		return null;
	}
	
	private void updateInteractionAnimationSpriteConfig() {
		AnimationDirector<TextureRegion> animation = getAnimation();
		AnimationSpriteConfig interactionSpriteConfig = interactionAnimation.getSpriteConfig();
		AnimationSpriteConfig animationSpriteConfig = animation.getSpriteConfig();
		
		interactionSpriteConfig.x = animationSpriteConfig.x
				+ (animation.getKeyFrame().getRegionWidth() * Constants.WORLD_TO_SCREEN * InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_X
						+ typeConfig.graphicsConfig.interactionMarkerOffsetX);
		interactionSpriteConfig.y = animationSpriteConfig.y
				+ (animation.getKeyFrame().getRegionHeight() * Constants.WORLD_TO_SCREEN * InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_Y
						+ typeConfig.graphicsConfig.interactionMarkerOffsetY);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false).setCollisionType(PhysicsCollisionType.OBSTACLE)
				.setDensity(AbstractCharacter.DENSITY_IMMOVABLE) // use a very high density, so the NPC can (almost) not be moved by the player or other forces
				.setLinearDamping(10f).setPhysicsBodyShape(PhysicsBodyShape.OCTAGON).setWidth(typeConfig.graphicsConfig.bodyWidth)
				.setHeight(typeConfig.graphicsConfig.bodyHeight);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		if (typeConfig.addSensor) {
			CharacterPhysicsUtil.addNpcSensor(body, typeConfig.sensorRadius);
		}
	}
	
	@Override
	public void act(float delta) {
		stateMachine.updateState(delta);
		
		if (!cutsceneHandler.isCutsceneActive()) {
			ai.calculateMove(delta);
			ai.executeMove(delta);
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (showInteractionIcon()) {
			interactionAnimation.increaseStateTime(delta);
			interactionAnimation.draw(batch);
		}
	}
	
	private boolean showInteractionIcon() {
		return typeConfig.interactionPossible && // 
				(!interactionAnimation.isAnimationFinished() // the animation (to appear or disappear) is still playing 
						|| interactionAnimation.getAnimation().getPlayMode() == PlayMode.NORMAL);// the interaction icon appeared and is to be shown (animation finished)
	}
	
	@Override
	protected CharacterTypeConfig getTypeConfig() {
		return typeConfig;
	}
	
	@Override
	protected void updateTextureDirection(AnimationDirector<TextureRegion> animation) {
		if (intendedMovement.len2() > 0.1f) {
			stateMachine.flipAnimationTexturesToMovementDirection(animation, intendedMovement);
		}
		else if (player != null) {
			Vector2 directionToPlayer = player.getPosition().sub(getPosition());
			stateMachine.flipAnimationTexturesToMovementDirection(animation, directionToPlayer);
		}
	}
	
	@Override
	public void removeFromMap() {
		ai.characterRemovedFromMap();
		gameMap.removeNpc(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		
		noticePlayerContact(contact);
		registerInteractionContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		super.endContact(contact);
		
		noticePlayerLeaving(contact);
		registerInteractionContactLeaving(contact);
	}
	
	private void noticePlayerContact(Contact contact) {
		PlayableCharacter collidingPlayer = getCollidingPlayer(contact);
		if (collidingPlayer != null) {
			player = collidingPlayer;
		}
	}
	
	private void noticePlayerLeaving(Contact contact) {
		PlayableCharacter collidingPlayer = getCollidingPlayer(contact);
		if (collidingPlayer != null) {
			player = null;
		}
	}
	
	private PlayableCharacter getCollidingPlayer(Contact contact) {
		return CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact, PlayableCharacter.class);
	}
	
	private void registerInteractionContact(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			if (typeConfig.interactByContact) {
				interact();
			}
			else {
				InteractionManager.getInstance().movedInRange(this);
				playInteractionAnimationAppear();
			}
		}
	}
	
	private void registerInteractionContactLeaving(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			InteractionManager.getInstance().movedOutOfRange(this);
			playInteractionAnimationDisappear();
		}
	}
	
	private boolean isPlayableCharacterContact(Contact contact) {
		return CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact, PlayableCharacter.class) != null;
	}
	
	private void playInteractionAnimationAppear() {
		updateInteractionAnimationSpriteConfig();
		interactionAnimation.setPlayMode(PlayMode.NORMAL);
		interactionAnimation.resetStateTime();
	}
	private void playInteractionAnimationDisappear() {
		interactionAnimation.setPlayMode(PlayMode.REVERSED);
		interactionAnimation.resetStateTime();
	}
	
	@Override
	public void move(Vector2 delta) {
		super.move(delta);
		
		if (typeConfig.interactionPossible) {
			updateInteractionAnimationSpriteConfig();
		}
	}
	
	@Override
	public void interact() {
		EventConfig event = new EventConfig().setEventType(EventType.NPC_INTERACTION).setStringValue(typeConfig.interactionEventId)
				.setParameterObject(this);
		EventHandler.getInstance().fireEvent(event);
	}
	
	@Override
	public boolean interactionCanBeExecuted() {
		return true;
	}
	
	@Override
	public float getDistanceToPlayer(Vector2 playerPosition) {
		return getPosition().sub(playerPosition).len();
	}
}
