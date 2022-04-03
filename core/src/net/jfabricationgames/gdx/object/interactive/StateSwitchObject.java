package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.projectile.MagicWave;
import net.jfabricationgames.gdx.rune.RuneType;

public class StateSwitchObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTIES_KEY_STATE_SWITCH_ID = "stateSwitchId";
	private static final String MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER = "eventParameter";
	private static final String MAP_PROPERTIES_KEY_CAN_BE_DEACTIVATED = "canBeDeactivated";
	private static final String RUNE_NEEDED_EVENT_KEY = "rune_needed__ansuz";
	
	private static ObjectMap<String, Boolean> switchObjectStates = new ObjectMap<>();
	
	public static boolean isStateSwitchActive(String stateSwitchId) {
		Boolean active = switchObjectStates.get(stateSwitchId);
		if (active == null) {
			Gdx.app.error(StateSwitchObject.class.getSimpleName(), "The required stateSwitchId '" + stateSwitchId + "' does not exist.");
			return false;
		}
		
		return active;
	}
	
	private static void changeSwitchObjectState(String stateSwitchId, boolean active) {
		switchObjectStates.put(stateSwitchId, active);
	}
	
	@MapObjectState
	private boolean active;
	private String stateSwitchId;
	
	public StateSwitchObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void processMapProperties() {
		super.processMapProperties();
		
		stateSwitchId = mapProperties.get(MAP_PROPERTIES_KEY_STATE_SWITCH_ID, String.class);
		if (stateSwitchId == null) {
			Gdx.app.error(getClass().getSimpleName(),
					"A StateSwitchObject should have stateSwitchId (configured in the map properties). Map-ID: " + getMapObjectId());
		}
		
		changeSwitchObjectState(stateSwitchId, false);
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {
		super.applyState(state);
		
		if (!isPressureActivated() && canBeDeactivated()) {
			active = Boolean.parseBoolean(state.get("active"));
			changeSwitchObjectState(stateSwitchId, active);
			updateSprite();
		}
	}
	
	@Override
	protected void executeInteraction() {
		if (runeCollected()) {
			if (!active || canBeDeactivated()) {
				performAction();
				active = !active;
				
				changeSwitchObjectState(stateSwitchId, active);
				updateSprite();
				executeEvent();
				playInteractionSound();
				changeBodySensorProperty();
				
				MapObjectDataHandler.getInstance().addStatefulMapObject(this);
			}
		}
		else {
			fireRuneNeededEvent();
		}
	}
	
	private boolean runeCollected() {
		return RuneType.ANSUZ.isCollected();
	}
	
	private void updateSprite() {
		if (active) {
			sprite = createSprite(typeConfig.textureAfterAction);
		}
		else {
			sprite = createSprite(typeConfig.texture);
		}
	}
	
	private void executeEvent() {
		if (mapProperties.containsKey(MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER)) {
			String eventParameter = mapProperties.get(MAP_PROPERTIES_KEY_STATE_CHANGED_EVENT_PARAMETER, String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.STATE_SWITCH_ACTION).setStringValue(eventParameter));
		}
	}
	
	private void changeBodySensorProperty() {
		if (typeConfig.changeBodyToSensorAfterAction) {
			if (active) {
				// changes the body to a sensor after the animation finished
				actionExecuted = true;
				changedBodyToSensor = false;
			}
			else {
				actionExecuted = false;
				changeBodyToNonSensor();
			}
		}
	}
	
	private void fireRuneNeededEvent() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.RUNE_NEEDED).setStringValue(RUNE_NEEDED_EVENT_KEY));
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.SET_STATE_SWITCH_STATE && event.stringValue.equals(stateSwitchId)) {
			active = !event.booleanValue; // set active to the opposite value, because the executeInteraction will switch it to the opposite value again
			executeInteraction();
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (isPressureActivated()) {
			if (isActivatedByCollision(contact)) {
				executeInteraction();
			}
		}
		else if (isMagicActivated()) {
			if (CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE, contact, MagicWave.class) != null) {
				executeInteraction();
			}
		}
		else {
			super.beginContact(contact);
		}
	}
	
	private boolean isActivatedByCollision(Contact contact) {
		Object collidingObject = CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE, contact, Object.class);
		PhysicsCollisionType collisionType = CollisionUtil.getCollisionTypeOfObjectCollidingWith(this, PhysicsCollisionType.OBSTACLE, contact);
		return collidingObject != null && collisionType != PhysicsCollisionType.PLAYER_ATTACK;
	}
	
	private boolean isPressureActivated() {
		return typeConfig.pressureActivated;
	}
	
	private boolean isMagicActivated() {
		return typeConfig.magicActivated;
	}
	
	private boolean canBeDeactivated() {
		boolean canBeDeactivatedByMapPropertiesConfig = Boolean
				.parseBoolean(mapProperties.get(MAP_PROPERTIES_KEY_CAN_BE_DEACTIVATED, "true", String.class));
		if (!canBeDeactivatedByMapPropertiesConfig) {
			return false;
		}
		return typeConfig.canBeDeactivated;
	}
	
	@Override
	public void endContact(Contact contact) {
		if (isPressureActivated()) {
			if (isActivatedByCollision(contact)) {
				executeInteraction();
			}
		}
		else {
			super.endContact(contact);
		}
	}
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
}
