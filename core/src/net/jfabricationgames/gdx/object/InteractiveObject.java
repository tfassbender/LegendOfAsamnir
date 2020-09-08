package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.hud.OnScreenTextBox;
import net.jfabricationgames.gdx.interaction.InteractionManager;
import net.jfabricationgames.gdx.interaction.Interactive;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class InteractiveObject extends GameObject implements Interactive {
	
	private enum Properties {
		
		DISPLAY_TEXT("displayText"), // text that is displayed in the OnScreenTextBox
		DISPLAY_TEXT_HEADER("displayTextHeader"); // header that is displayed in the OnScreenTextBox (only if DISPLAY_TEXT is defined)
		
		public final String key;
		
		private Properties(String key) {
			this.key = key;
		}
	}
	
	private boolean actionExecuted = false;
	
	public InteractiveObject(ObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties) {
		super(typeConfig, sprite, properties);
		PhysicsWorld.getInstance().registerContactListener(this);
	}
	
	@Override
	public void interact() {
		if (typeConfig.multipleActionExecutionsPossible || !actionExecuted) {
			if (typeConfig.animationAction != null) {
				animation = getActionAnimation();
			}
			performAction();
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
	
	@Override
	public float getDistanceFromDwarf(PlayableCharacter character) {
		return character.getPosition().sub(body.getPosition()).len();
	}
	
	private void performAction() {
		if (properties.containsKey(Properties.DISPLAY_TEXT.key)) {
			OnScreenTextBox onScreenTextBox = OnScreenTextBox.getInstance();
			onScreenTextBox.setHeaderText(properties.get(Properties.DISPLAY_TEXT_HEADER.key, String.class));
			onScreenTextBox.setText(properties.get(Properties.DISPLAY_TEXT.key, String.class));
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			InteractionManager.getInstance().movedInRange(this);
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		if (isPlayableCharacterContact(contact)) {
			InteractionManager.getInstance().movedOutOfRange(this);
		}
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
