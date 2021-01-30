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
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterPhysicsUtil;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
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
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class NonPlayableCharacter extends AbstractCharacter implements Interactive {
	
	private NonPlayableCharacterTypeConfig typeConfig;
	
	private PlayableCharacter player;
	private AnimationDirector<TextureRegion> interactionAnimation;
	
	public NonPlayableCharacter(NonPlayableCharacterTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		movingSpeed = typeConfig.movingSpeed;
		
		initializeStates();
		createAI();
		ai.setCharacter(this);
		initializeInteractionAnimation();
		
		setImageOffset(typeConfig.imageOffsetX, typeConfig.imageOffsetY);
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, null);
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
			spriteConfig.x += (animation.getKeyFrame().getRegionWidth() * GameScreen.WORLD_TO_SCREEN
					* InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_X + typeConfig.interactionMarkerOffsetX);
			spriteConfig.y += (animation.getKeyFrame().getRegionHeight() * GameScreen.WORLD_TO_SCREEN
					* InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_Y + typeConfig.interactionMarkerOffsetY);
			
			return spriteConfig;
		}
		
		return null;
	}
	
	private void updateInteractionAnimationSpriteConfig() {
		AnimationDirector<TextureRegion> animation = getAnimation();
		AnimationSpriteConfig interactionSpriteConfig = interactionAnimation.getSpriteConfig();
		AnimationSpriteConfig animationSpriteConfig = animation.getSpriteConfig();
		
		interactionSpriteConfig.x = animationSpriteConfig.x
				+ (animation.getKeyFrame().getRegionWidth() * GameScreen.WORLD_TO_SCREEN * InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_X
						+ typeConfig.interactionMarkerOffsetX);
		interactionSpriteConfig.y = animationSpriteConfig.y + (animation.getKeyFrame().getRegionHeight() * GameScreen.WORLD_TO_SCREEN
				* InteractionManager.INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_Y + typeConfig.interactionMarkerOffsetY);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setType(BodyType.DynamicBody).setSensor(false).setCollisionType(PhysicsCollisionType.OBSTACLE)
				.setDensity(10_000f) // use a very high density, so the NPC can (almost) not be moved by the player or other forces
				.setLinearDamping(10f).setPhysicsBodyShape(PhysicsBodyShape.OCTAGON).setWidth(typeConfig.bodyWidth).setHeight(typeConfig.bodyHeight);
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
			ai.executeMove();
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
		return !interactionAnimation.isAnimationFinished() // the animation (to appear or disappear) is still playing 
				|| interactionAnimation.getAnimation().getPlayMode() == PlayMode.NORMAL;// the interaction icon appeared and is to be shown (animation finished)
	}
	
	@Override
	protected void updateTextureDirection(TextureRegion region) {
		if (intendedMovement != null && intendedMovement.len2() > 0.1f) {
			stateMachine.flipTextureToMovementDirection(region, intendedMovement);
		}
		else if (player != null) {
			Vector2 directionToPlayer = player.getPosition().sub(getPosition());
			stateMachine.flipTextureToMovementDirection(region, directionToPlayer);
		}
	}
	
	@Override
	public void removeFromMap() {
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
		return CollisionUtil.isPlayableCharacterContact(this, PhysicsCollisionType.OBSTACLE_SENSOR, contact);
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
		updateInteractionAnimationSpriteConfig();
	}
	
	@Override
	public void interact() {
		EventConfig event = new EventConfig().setEventType(EventType.NPC_INTERACTION).setStringValue(typeConfig.interactionEventId)
				.setParameterObject(this);
		EventHandler.getInstance().fireEvent(event);
	}
	
	@Override
	public float getDistanceToPlayer(PlayableCharacter character) {
		return getPosition().sub(character.getPosition()).len();
	}
}
