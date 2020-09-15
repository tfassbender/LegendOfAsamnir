package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.interaction.InteractionManager;
import net.jfabricationgames.gdx.interaction.Interactive;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public class InteractiveObject extends GameObject implements Interactive {
	
	private enum Properties {
		
		DISPLAY_TEXT("displayText"), // text that is displayed in the OnScreenTextBox
		DISPLAY_TEXT_HEADER("displayTextHeader"); // header that is displayed in the OnScreenTextBox (only if DISPLAY_TEXT is defined)
		
		public final String mapPropertiesKey;
		
		private Properties(String mapPropertiesKey) {
			this.mapPropertiesKey = mapPropertiesKey;
		}
	}
	
	private boolean actionExecuted = false;
	private AnimationDirector<TextureRegion> interactionAnimation;
	
	public InteractiveObject(ObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
		PhysicsWorld.getInstance().registerContactListener(this);
		
		interactionAnimation = InteractionManager.getInstance().getInteractionAnimation();
		//don't show the interaction animation on startup
		playInteractionAnimationDisappear();
		interactionAnimation.endAnimation();
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (showInteractionIcon()) {
			interactionAnimation.increaseStateTime(delta);
			TextureRegion interactionTexture = interactionAnimation.getKeyFrame();
			float x = sprite.getX() + (sprite.getWidth() * GameScreen.WORLD_TO_SCREEN * 0.6f);
			float y = sprite.getY() + (sprite.getHeight() * GameScreen.WORLD_TO_SCREEN * 0.6f);
			batch.draw(interactionTexture, x, y, sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f, interactionTexture.getRegionWidth(),
					interactionTexture.getRegionHeight(), GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0f);
		}
	}
	
	private boolean showInteractionIcon() {
		return canBeExecuted() //
				&& (!interactionAnimation.isAnimationFinished() // the animation (to appear or disappear) is still playing 
						|| interactionAnimation.getAnimation().getPlayMode() == PlayMode.NORMAL); // the interaction icon appeared and is to be shown (animation finished)
	}
	
	@Override
	public void interact() {
		if (canBeExecuted()) {
			if (typeConfig.animationAction != null) {
				animation = getActionAnimation();
			}
			performAction();
			dropItems();
			actionExecuted = true;
			if (typeConfig.textureAfterAction != null) {
				sprite = createSprite(typeConfig.textureAfterAction);
			}
			
			if (!typeConfig.hitAnimationAfterAction && !typeConfig.multipleActionExecutionsPossible) {
				//reset the hit animation because it will not be played anymore after the action was executed
				typeConfig.animationHit = null;
			}
		}
	}
	
	private boolean canBeExecuted() {
		return typeConfig.multipleActionExecutionsPossible || !actionExecuted;
	}
	
	@Override
	public float getDistanceFromDwarf(PlayableCharacter character) {
		return character.getPosition().sub(body.getPosition()).len();
	}
	
	private void performAction() {
		if (mapProperties.containsKey(Properties.DISPLAY_TEXT.mapPropertiesKey)) {
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(mapProperties.get(Properties.DISPLAY_TEXT_HEADER.mapPropertiesKey, String.class));
			onScreenTextBox.setText(mapProperties.get(Properties.DISPLAY_TEXT.mapPropertiesKey, String.class));
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			InteractionManager.getInstance().movedInRange(this);
			playInteractionAnimationAppear();
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			InteractionManager.getInstance().movedOutOfRange(this);
			playInteractionAnimationDisappear();
		}
	}
	
	private void playInteractionAnimationAppear() {
		interactionAnimation.setPlayMode(PlayMode.NORMAL);
		interactionAnimation.resetStateTime();
	}
	private void playInteractionAnimationDisappear() {
		interactionAnimation.setPlayMode(PlayMode.REVERSED);
		interactionAnimation.resetStateTime();
	}
	
	private boolean isPlayableCharacterContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (CollisionUtil.containsCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR, fixtureA, fixtureB)) {
			Object sensorUserData = CollisionUtil.getCollisionTypeUserData(PhysicsCollisionType.OBSTACLE_SENSOR, fixtureA, fixtureB);
			Object sensorCollidingUserData = CollisionUtil.getOtherTypeUserData(PhysicsCollisionType.OBSTACLE_SENSOR, fixtureA, fixtureB);
			
			if (sensorUserData == this && sensorCollidingUserData != null && sensorCollidingUserData instanceof PlayableCharacter) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void remove() {
		super.remove();
		PhysicsWorld.getInstance().removeContactListener(this);
	}
}
